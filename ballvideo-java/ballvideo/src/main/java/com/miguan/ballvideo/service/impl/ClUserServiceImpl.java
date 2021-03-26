package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.constants.PushArticleConstant;
import com.miguan.ballvideo.common.enums.VideoESOptions;
import com.miguan.ballvideo.common.util.AppPackageUtil;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.dynamicquery.Dynamic5Query;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.mapper.ClUserMapper;
import com.miguan.ballvideo.mapper.FirstVideosMapper;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.vo.BuryingActionType;
import com.miguan.ballvideo.vo.ClUserVo;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.userBuryingPoint.UserBuryingPointVo;
import com.miguan.ballvideo.vo.video.HotListVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户表ServiceImpl
 *
 * @author xy.chen
 * @date 2019-08-09
 **/
@Slf4j
@Service("clUserService")
public class ClUserServiceImpl implements ClUserService {

    @Resource
    private ClUserMapper clUserMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private ClSmsService clSmsService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UserBuriedPointService userBuriedPointService;

    @Resource
    private DynamicQuery dynamicQuery;

    @Resource
    private FirstVideosOldService firstVideosOldService;

    @Resource
    private VideoEsService firstVideoEsItemService;

    @Resource
    private FirstVideosMapper firstVideosMapper;

    @Resource
    private Dynamic5Query dynamic5Query;

    /**
     * 用户登录
     *
     * @param request
     * @param clUserVo 用户实体
     * @param vcode    短信验证码
     * @return
     */
    @Override
    public Map<String, Object> login(HttpServletRequest request, ClUserVo clUserVo, String vcode) {
        Map<String, Object> result = new HashMap<String, Object>();
        String loginName = clUserVo.getLoginName();
        int results = clSmsService.verifySms(loginName, "login", vcode);
        String vmsg = null;
        if (results == 1) {
            vmsg = null;
        } else if (results == -1) {
            vmsg = "验证码已过期";
        } else {
            vmsg = "验证码错误";
        }
        if (vmsg != null) {
            result.put("success", "-1");
            result.put("msg", vmsg);
            return result;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("loginName", loginName);
        List<ClUserVo> clUserList = clUserMapper.findClUserList(paramMap);
        if (clUserList == null || clUserList.size() == 0) {
            result.put("register", "1");//注册标识
            String suffixName = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
            clUserVo.setName("xy_" + suffixName);
            String appEnvironment = Global.getValue("app_environment");
            if ("prod".equals(appEnvironment)){
                clUserVo.setImgUrl(Global.getValue("prod_default_head_img"));//生产默认头像
            }else {
                clUserVo.setImgUrl(Global.getValue("dev_default_head_img"));//测试默认头像
            }
            clUserVo.setState("10");//
            clUserVo.setAppPackage(clUserVo.getAppPackage());
            clUserMapper.saveClUser(clUserVo);
            //添加埋点 add hyl 2019年9月12日11:24:29
            clUserVo.setId(0L);
            clUserVo.setImei(clUserVo.getImei());
            sendToMQ(clUserVo, BuryingActionType.XY_REGISTER);
        } else {
            //用户被禁用状态
            if ("20".equals(clUserList.get(0).getState())){
                result.put("success", "-1");
                result.put("msg", "该用户被禁用");
                return result;
            }
            //更新登陆时间
            clUserVo.setAppPackage(clUserVo.getAppPackage());
            clUserMapper.updateClUser(clUserVo);
        }
        clUserList = clUserMapper.findClUserList(paramMap);

        //初始化任务系统金币信息
        if (!initUserGold(result, clUserVo, loginName, clUserList)) {
            return result;
        }

        String token = UUID.randomUUID().toString().replaceAll("-", "");
        result.put("token", token);
        result.put("userId", String.valueOf(clUserList.get(0).getId()));
        redisService.set(RedisKeyConstant.USER_TOKEN + clUserList.get(0).getId(), token,RedisKeyConstant.USER_TOKEN_SECONDS);  //token失效时间30天

        result.put("success", "0");
        result.put("msg", "登录成功");
        result.put("userInfo", clUserList.get(0));
        //将前端传过来 当前用户的手机版本 和 APP版本 添加到User对象中
        clUserList.get(0).setAppVersion(clUserVo.getAppVersion());
        clUserList.get(0).setOsVersion(clUserVo.getOsVersion());
        clUserList.get(0).setSystemVersion(clUserVo.getSystemVersion());
        clUserList.get(0).setChannelId(clUserVo.getChannelId());
        clUserList.get(0).setImei(clUserVo.getImei());
        clUserList.get(0).setAppPackage(clUserVo.getAppPackage());
        sendToMQ(clUserList.get(0), BuryingActionType.XY_LOGIN);
        return result;
    }

    /**
     * 初始化任务系统金币信息
     * @param clUserVo
     * @param loginName
     * @param clUserList
     */
    public boolean initUserGold(Map<String, Object> result, ClUserVo clUserVo, String loginName, List<ClUserVo> clUserList) {
        /*if(!VersionUtil.compareIsHigh(Constant.APPVERSION_267,clUserVo.getAppVersion())){
            return true;
        }*/
        try {
            String appType = AppPackageUtil.getAppType(clUserVo.getAppPackage());
            String nativeSql = "select count(1) from user_gold a " +
                    "where a.user_id = '" + clUserList.get(0).getId() + "' and a.app_type = '" + appType + "' ";
            Object obj = dynamic5Query.nativeQueryObject(nativeSql);
            if (Integer.parseInt(obj.toString()) == 0) {
                String insertSql = "insert into user_gold(user_id, phone, total_gold, used_gold, created_at, app_type, app_package) " +
                        "VALUES ('" + clUserList.get(0).getId() + "','" + loginName + "',0,0,NOW(),'" + appType + "','" + clUserVo.getAppPackage() + "')";
                dynamic5Query.nativeExecuteUpdate(insertSql);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化任务系统金币信息失败：userId:"+clUserList.get(0).getId()+","+e.getMessage());
            result.put("success", "-1");
            result.put("msg", "网络异常，请重试");
            return false;
        }
        return true;
    }

    /**
     * 获取全部华为推送的tokens
     * @param pushArticle 消息推送配置
     */
    @Override
    public List<String> findAllHuaweiTotken(PushArticle pushArticle) {
        if (PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType())) {
            final String huaweiTokens = pushArticle.getHuaweiTokens();
            if (StringUtils.isBlank(huaweiTokens)) {
                return new ArrayList<>();
            }
            final String[] split = huaweiTokens.split(",");
            return Arrays.asList(split);
        }
        List<String> result = new ArrayList<>();
        final List<String> allHuaweiToken = this.clUserMapper.findAllHuaweiToken(pushArticle.getAppPackage());
        if (allHuaweiToken != null) {
            for (String token : allHuaweiToken) {
                if (token != null) {
                    result.add(token);
                }
            }
        }
        return result;
    }

    /**
     * 获取全部小米推送的tokens
     * @param pushArticle 消息推送配置
     */
    @Override
    public List<String> findAllXiaoMiTotken(PushArticle pushArticle) {
        if (PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType())) {
            final String xiaoMiTokens = pushArticle.getXiaomiTokens();
            if (StringUtils.isBlank(xiaoMiTokens)) {
                return new ArrayList<>();
            }
            final String[] split = xiaoMiTokens.split(",");
            return Arrays.asList(split);
        }
        List<String> result = new ArrayList<>();
        final List<String> allXiaoMiToken = this.clUserMapper.findAllXiaoMiToken(pushArticle.getAppPackage());
        if (allXiaoMiToken != null) {
            for (String token : allXiaoMiToken) {
                if (token != null) {
                    result.add(token);
                }
            }
        }
        return result;
    }

    @Override
    public List<String> findAllOppoToken(PushArticle pushArticle){
        return clUserMapper.findAllOppoToken(pushArticle.getAppPackage());
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId, String vcode) {
        //验证短信验证码
        if (StringUtils.isNotEmpty(vcode)) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("id", userId);
            List<ClUserVo> users = clUserMapper.findClUserList(paramMap);
            if (CollectionUtils.isNotEmpty(users)) {
                ClUserVo user = users.get(0);
                int results = clSmsService.verifySms(user.getLoginName(), "cancel", vcode);
                if (results != 1) {
                    log.info("用户（userID=" + userId + "）注销失败:短信验证码错误！");
                    return;
                }
            }
        }
        int a = resetUserNameAndImage(userId);
        int b = deleteUserVideoFavoritesDatas(userId);
        int c = deleteUserCommontDatas(userId);
        int d = deleteUserCommontReply(userId);
        log.info("用户（userID="+userId+"）注销"+(a>0?"成功":"失败")+"，删除收藏记录"+b+"条，评论记录"+c+"条，回复记录"+d+"条");
    }

    private int deleteUserVideoFavoritesDatas(Long userId) {
        String sql = "delete from cl_user_videos where user_id =?";
        return dynamicQuery.nativeExecuteUpdate(sql, new Object[]{userId});
    }

    private int deleteUserCommontDatas(Long userId) {
        String sql = "delete from cl_user_comment where user_id =?";
        return dynamicQuery.nativeExecuteUpdate(sql, new Object[]{userId});
    }

    private int deleteUserCommontReply(Long userId) {
        String sql = "delete from comment_reply where from_uid = ? or to_from_uid = ? ";
        return dynamicQuery.nativeExecuteUpdate(sql, new Object[]{userId,userId});
    }

    private int resetUserNameAndImage(Long userId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", userId);
        List<ClUserVo> users = clUserMapper.findClUserList(paramMap);
        if(CollectionUtils.isNotEmpty(users)){
            ClUserVo user = users.get(0);
            int num = (int) ((Math.random() * 9 + 1) * 100000);
            String imgUrlStr = Global.getValue("defaultImage");
            if (StringUtils.isNotBlank(imgUrlStr)) {
                String[] imgUrls = imgUrlStr.split(",");
                Random r = new Random();
                int imgNum = r.nextInt(15);
                user.setName(String.valueOf(num));
                user.setSign("reset");
                user.setImgUrl(imgUrls[imgNum]);
                return clUserMapper.updateClUser(user);
            }
        }
        return 0;
    }

    public void sendToMQ(ClUserVo clUserVo, String actionId) {
        UserBuryingPointVo userBuryingPointVo = new UserBuryingPointVo();
        userBuryingPointVo.setUserId(clUserVo.getId().toString());
        userBuryingPointVo.setActionId(actionId);
        userBuryingPointVo.setPhone(clUserVo.getLoginName());
        userBuryingPointVo.setDeviceId(clUserVo.getDeviceId());
        userBuryingPointVo.setOsVersion(clUserVo.getOsVersion());
        userBuryingPointVo.setAppVersion(clUserVo.getAppVersion());
        userBuryingPointVo.setSystemVersion(clUserVo.getSystemVersion());
        userBuryingPointVo.setChannelId(clUserVo.getChannelId());
        userBuryingPointVo.setCreatTime(new Date());
        userBuryingPointVo.setImei(clUserVo.getImei());
        userBuryingPointVo.setAppPackage(clUserVo.getAppPackage());
        userBuryingPointVo.setCreateDate(DateUtil.parseDateToStr(new Date(), tool.util.DateUtil.DATEFORMAT_STR_002));
        Integer flag = userBuriedPointService.judgeUser(userBuryingPointVo.getDeviceId(),clUserVo.getChannelId());
        String dataStr = JSON.toJSONString(userBuryingPointVo);

        StringBuilder stringBuilder = new StringBuilder(flag.toString());
        stringBuilder.append(RabbitMQConstant._MQ_);
        stringBuilder.append(dataStr);
        String jsonStr = stringBuilder.toString();

        rabbitTemplate.convertAndSend(RabbitMQConstant.BURYPOINT_EXCHANGE, RabbitMQConstant.BURYPOINT_RUTE_KEY, jsonStr);
    }

    @Override
    public List<ClUserVo> findClUserList(Map<String, Object> params) {
        return clUserMapper.findClUserList(params);
    }

    @Override
    public int saveClUser(ClUserVo clUserVo) {
        return clUserMapper.saveClUser(clUserVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateClUser(ClUserVo clUserVo) {
        ClUserVo odlUserVo = clUserMapper.findClUserById(clUserVo.getId().toString());
        int count = clUserMapper.updateClUser(clUserVo);

        String oldImgUrl = odlUserVo.getImgUrl() == null ? "" : odlUserVo.getImgUrl();
        String imgUrl = clUserVo.getImgUrl() == null ? "" : clUserVo.getImgUrl();
        // 判断是否又修改用户名或者头像
        if(count > 0 && (!StringUtils.equals(odlUserVo.getName(), clUserVo.getName()) || !StringUtils.equals(oldImgUrl.trim(), imgUrl.trim()))){
            // 修改用户关联信息，first_videos的作者名称和头像地址， 以及刷新对应的es表
            List<String> videoIdList = firstVideosMapper.findIdListByUserId(clUserVo.getId());
            if(CollectionUtils.isNotEmpty(videoIdList)){
                // 更新first_videos
                firstVideosOldService.updatevideoAuthorAndbsyHeadUrlByUserId(clUserVo.getName(), clUserVo.getImgUrl(), clUserVo.getId());
                // 刷新Es
                String videoIds = String.join(",", videoIdList);
                firstVideoEsItemService.update(videoIds, VideoESOptions.videoUpdate.name());
            }
        }

        return count;
    }

    @Override
    public List<ClUserVo> findAllTokens(Map<String, Object> params) {
        return clUserMapper.findAllTokens(params);
    }

    @Override
    public void packagingUserAndVideos(List<Videos161Vo> firstVideos){
        List<Long> idsList = firstVideos.stream().filter(s->s.getUserId() != null).map(Videos161Vo::getUserId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(idsList)){
            List<ClUserVo> userVoList = clUserMapper.findUserListByIds(idsList);
            Map<Long, List<ClUserVo>> listMap = userVoList.stream().collect(Collectors.groupingBy(ClUserVo::getId));
            for (Videos161Vo videos161Vo : firstVideos) {
                Long userId = videos161Vo.getUserId();
                if (userId != null && userId > 0) {
                    List<ClUserVo> list = listMap.get(userId);
                    if (CollectionUtils.isNotEmpty(list)) {
                        ClUserVo clUserVo = list.get(0);
                        videos161Vo.setBsyHeadUrl(clUserVo.getImgUrl());
                        videos161Vo.setVideoAuthor(clUserVo.getName());
                    }
                }
            }
        }
    }

    @Override
    public void packagingUserAndVideos2(List<HotListVo> hotList){
        List<Long> idsList = hotList.stream().filter(s->s.getUserId() != null).map(Videos161Vo::getUserId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(idsList)){
            List<ClUserVo> userVoList = clUserMapper.findUserListByIds(idsList);
            Map<Long, List<ClUserVo>> listMap = userVoList.stream().collect(Collectors.groupingBy(ClUserVo::getId));
            for (HotListVo hotListVo : hotList) {
                Long userId = hotListVo.getUserId();
                if (userId != null && userId > 0) {
                    List<ClUserVo> list = listMap.get(userId);
                    if (CollectionUtils.isNotEmpty(list)) {
                        ClUserVo clUserVo = list.get(0);
                        hotListVo.setBsyHeadUrl(clUserVo.getImgUrl());
                        hotListVo.setVideoAuthor(clUserVo.getName());
                    }
                }
            }
        }
    }

    @Override
    public void packagingUserAndfirstVideos(List<FirstVideos> firstVideosList){
        List<Long> idsList = firstVideosList.stream().filter(s->s.getUserId() != null).map(FirstVideos::getUserId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(idsList)) {
            List<ClUserVo> userVoList = clUserMapper.findUserListByIds(idsList);
            Map<Long, List<ClUserVo>> listMap = userVoList.stream().collect(Collectors.groupingBy(ClUserVo::getId));
            for (FirstVideos firstVideos : firstVideosList) {
                Long userId = firstVideos.getUserId();
                if (userId != null && userId > 0) {
                    List<ClUserVo> list = listMap.get(userId);
                    if (CollectionUtils.isNotEmpty(list)){
                        ClUserVo clUserVo = list.get(0);
                        firstVideos.setBsyHeadUrl(clUserVo.getImgUrl());
                        firstVideos.setVideoAuthor(clUserVo.getName());
                    }
                }
            }
        }
    }
}
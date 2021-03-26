package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.video.VideoSQLUtils;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.entity.UserLabel;
import com.miguan.ballvideo.entity.UserLabelDefault;
import com.miguan.ballvideo.entity.UserLabelGrade;
import com.miguan.ballvideo.mapper.VideosCatMapper;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.repositories.UserLabelGradeJpaRepository;
import com.miguan.ballvideo.repositories.UserLabelJpaRepository;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.UserLabelDefaultService;
import com.miguan.ballvideo.service.UserLabelService;
import com.miguan.ballvideo.vo.BuryingActionType;
import com.miguan.ballvideo.vo.QueryParamsVo;
import com.miguan.ballvideo.vo.userBuryingPoint.UserBuryingPointVo;
import com.miguan.ballvideo.vo.video.CatIdWeightVo;
import com.miguan.ballvideo.vo.video.UserLabelVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 用户标签ServiceImpl
 * @author laiyudan
 * @date 2019-10-22
 **/
@Slf4j
@Service("userLabelService")
public class UserLabelServiceImpl implements UserLabelService {

	@Resource
    private UserLabelJpaRepository userLabelJpaRepository;

    @Resource
    private DynamicQuery dynamicQuery;

    @Resource
    private VideosCatMapper videosCatMapper;

    @Resource
    private UserLabelDefaultService userLabelDefaultService;

    @Resource
    private UserLabelGradeJpaRepository userLabelGradeJpaRepository;

    //最大权重值
    public final String MAX_WEIGHT_CNT = "maxWeightCnt";

    //权重值总和
    public final String TOTAL_WEIGHT_CNT = "totalWeightCnt";

    public final int MAX = 3000;//迁移用户标签数据，每次按照3000条分组操作

    @Resource
    private RedisService redisService;

    @Resource
    private RabbitTemplate rabbitTemplate;

	/**
	 * 更新用户标签V1.8（只更新CatId1，CatId2）
	 * @param params
	 * @return
	 */
	@Override
	public UserLabel updateUserLabelInfo(QueryParamsVo params) {
		UserLabel userLabel  = new UserLabel();
        //获取符合条件的埋点
        List<UserBuryingPointVo> userBuryingPointList = queryUserBuryingPoints(params.getDeviceId());
        if(userBuryingPointList.isEmpty()) {
            return userLabel;
        }
        Double labelUpdateValueRatio = Double.parseDouble(Global.getValue("label_update_value") == null ? "0" : Global.getValue("label_update_value"));
        Map<String,BigDecimal> weightMap = new HashMap<>();
        weightMap.put(MAX_WEIGHT_CNT, new BigDecimal(0));
        weightMap.put(TOTAL_WEIGHT_CNT, new BigDecimal(0));
        //返回每个catID的权重
        List<CatIdWeightVo> catIdWeightList = getCatIdWeightVos(userBuryingPointList,weightMap);
        //推荐的埋点cat=0，所以catIdWeightList有可能为空 add shixh1126
        if (CollectionUtils.isEmpty(catIdWeightList)) return userLabel;
        BigDecimal resultWeight = weightMap.get(MAX_WEIGHT_CNT).divide(weightMap.get(TOTAL_WEIGHT_CNT), 2, BigDecimal.ROUND_HALF_UP);
        //重新设置用户标签
        if (resultWeight.compareTo(BigDecimal.valueOf(labelUpdateValueRatio)) > 0) {
            catIdWeightList.sort(Comparator.comparing(CatIdWeightVo::getSumCount).reversed().thenComparing(Comparator.comparing(CatIdWeightVo::getCatId).reversed()));
            UserLabel findUserLabel = findTopByDeviceId(params.getDeviceId());
            if (catIdWeightList.size() == 1) {
                findUserLabel.setCatId1(catIdWeightList.get(0).getCatId());
                findUserLabel.setCatId2(catIdWeightList.get(0).getCatId());
            } else {
                findUserLabel.setCatId1(catIdWeightList.get(0).getCatId());
                findUserLabel.setCatId2(catIdWeightList.get(1).getCatId());
            }
            findUserLabel.setUpdateDate(new Date());
            userLabel = saveToMQ(findUserLabel);
        }
        return userLabel;
    }

    /**
     * 据指定actionID查询当天埋点信息
     * @param deviceId
     * @return
     */
    private List<UserBuryingPointVo> queryUserBuryingPoints(String deviceId) {
        String nativeSql = "select video_id,action_id,catid,source,video_rate " +
                "        from xy_burying_point_label " +
                "        where device_id = ? and catid is not null";
        return dynamicQuery.nativeQueryList(UserBuryingPointVo.class,nativeSql,deviceId);
    }

    /**
     * 计算每个catID的权重值
     *
     * @param userBuryingPointListNew
     * @param weightMap
     * @return
     */
    private List<CatIdWeightVo> getCatIdWeightVos(List<UserBuryingPointVo> userBuryingPointListNew, Map<String, BigDecimal> weightMap) {
        Double videoPlayRatio = Double.parseDouble(Global.getValue("video_play") == null ? "0" : Global.getValue("video_play"));
        Double videoAllPlayRatio = Double.parseDouble(Global.getValue("video_all_play") == null ? "0" : Global.getValue("video_all_play"));
        Double videoPraiseRatio = Double.parseDouble(Global.getValue("video_praise") == null ? "0" : Global.getValue("video_praise"));
        Double videoCommentRatio = Double.parseDouble(Global.getValue("video_comment") == null ? "0" : Global.getValue("video_comment"));
        Double videoCollectRatio = Double.parseDouble(Global.getValue("video_collect") == null ? "0" : Global.getValue("video_collect"));
        Double videoShareRatio = Double.parseDouble(Global.getValue("video_share") == null ? "0" : Global.getValue("video_share"));
        List<CatIdWeightVo> catIdWeightList = new ArrayList<>();
        Map<Long, List<UserBuryingPointVo>> catIdMap = userBuryingPointListNew.stream().collect(Collectors.groupingBy(UserBuryingPointVo::getCatid));
        for (Map.Entry<Long, List<UserBuryingPointVo>> map : catIdMap.entrySet()) {
            if(map.getKey() <= 0) {
                continue;
            }
            Map<String, Long> videoPlayCount = map.getValue().stream().collect(Collectors.groupingBy(UserBuryingPointVo::getActionId, Collectors.counting()));
            Double sumCnt = 0.00;
            for (Map.Entry<String, Long> map2 : videoPlayCount.entrySet()) {
                String actionId = map2.getKey();
                Long cntValue = map2.getValue() == null ? 0L : map2.getValue();
                if (BuryingActionType.XY_VIDEO_PLAY.equals(actionId)) {
                    sumCnt += cntValue * videoPlayRatio;
                } else if (BuryingActionType.XY_VIDEO_PLAYOVER.equals(actionId)) {
                    sumCnt += cntValue * videoAllPlayRatio;
                } else if (BuryingActionType.XY_VIDEO_PRAISE.equals(actionId)) {
                    sumCnt += cntValue * videoPraiseRatio;
                } else if (BuryingActionType.XY_VIDEO_COMMENT.equals(actionId)) {
                    sumCnt += cntValue * videoCommentRatio;
                } else if (BuryingActionType.XY_VIDEO_COLLECT.equals(actionId)) {
                    sumCnt += cntValue * videoCollectRatio;
                } else if (BuryingActionType.SHARE_WAY.equals(actionId)) {
                    sumCnt += cntValue * videoShareRatio;
                }
            }
            if (weightMap.get(MAX_WEIGHT_CNT).compareTo(BigDecimal.valueOf(sumCnt)) < 0) {
                weightMap.put(MAX_WEIGHT_CNT, BigDecimal.valueOf(sumCnt));
            }
            if (sumCnt > 0) {
                weightMap.put(TOTAL_WEIGHT_CNT, weightMap.get(TOTAL_WEIGHT_CNT).add(BigDecimal.valueOf(sumCnt)));
                CatIdWeightVo catIdWeightVo = new CatIdWeightVo();
                catIdWeightVo.setCatId(map.getKey());
                catIdWeightVo.setSumCount(sumCnt);
                catIdWeightList.add(catIdWeightVo);
            }
        }
        return catIdWeightList;
    }

    @Override
    public UserLabel findTopByDeviceId(String deviceId) {
        String key = RedisKeyConstant.USERLABEL_KEY + deviceId;
        if(redisService.exits(key)) {
            UserLabelVo userLabelVo = redisService.get(key,UserLabelVo.class);
            return userLabelVo.getUserLabel();
        }
        return userLabelJpaRepository.findTopByDeviceId(deviceId);
    }

    @Override
    public UserLabel saveToMQ(UserLabel userLabel) {
        String jsonStr = JSON.toJSONString(userLabel);
        rabbitTemplate.convertAndSend(
                RabbitMQConstant.UserLabel_SAVE_EXCHANGE,
                RabbitMQConstant.UserLabel_SAVE_KEY,jsonStr);
        return userLabel;
    }

  /**
   * 安卓-访问首页菜单栏-初始化用户标签
   * @param deviceId
   * @param channelId
   */
  @Override
  public void initUserLabel(String deviceId, String channelId, String appPackage) {
        UserLabel userLabel = userLabelJpaRepository.findTopByDeviceId(deviceId);
        if (userLabel == null) {
            createUserLabel(deviceId, channelId, appPackage);
        }else{
            if (StringUtils.isEmpty(userLabel.getCatIdsSort())) {
                //catIdsSort为空需要初始化
               this.updateDefaultCatIdsSort(userLabel, appPackage);
            }else{
                updateUserLabelByRedis(userLabel);
            }
        }
    }

    /**
     * 新用户-创建用户标签-放缓存
     * @param deviceId
     * @param channelId
     * @return
     */
    public UserLabel createUserLabel(String deviceId, String channelId,String appPackage) {
        UserLabel userLabel = new UserLabel();
        Date currDate = DateUtil.getNow();
        userLabel.setDeviceId(deviceId);
        userLabel.setCreateDate(currDate);
        userLabel.setUpdateDate(currDate);
        //通过渠道ID查询初始标签配置
        UserLabelDefault userLabelDefault = userLabelDefaultService.getUserLabelDefault(channelId);
        userLabel.setCatId1(userLabelDefault.getCatId1());
        userLabel.setCatId2(userLabelDefault.getCatId2());
        //新用户随机生成catIdsSort总数:用户第一标签条数+用户第二标签条数
        String catIdsSort = getUserCatIdsSort(userLabel.getCatId1()+"",appPackage);
        userLabel.setCatIdsSort(catIdsSort);
        //初始化用户标签信息
        userLabel = saveToMQ(userLabel);
        updateUserLabelByRedis(userLabel);
        log.info("-------用户标签"+deviceId+"初始化完成--------");
        return userLabel;
    }

    @Override
    public UserLabel createIOSUserLabel(String deviceId, String channelId,String appPackage) {
        UserLabel userLabel = new UserLabel();
        Date currDate = DateUtil.getNow();
        userLabel.setDeviceId(deviceId);
        userLabel.setCreateDate(currDate);
        userLabel.setUpdateDate(currDate);
        //通过渠道ID查询初始标签配置
        UserLabelDefault userLabelDefault = userLabelDefaultService.getUserLabelDefault(channelId);
        userLabel.setCatId1(userLabelDefault.getCatId1());
        userLabel.setCatId2(userLabelDefault.getCatId2());
        //新用户随机生成catIdsSort总数:用户第一标签条数+用户第二标签条数
        String catIdsSort = getUserCatIdsSort(userLabel.getCatId1()+"",appPackage);
        userLabel.setCatIdsSort(catIdsSort);
        return userLabel;
    }

  /**
   * 更新用户默认catIdsSort，更新缓存
   * @param userLabel
   * @return
   */
  private UserLabel updateDefaultCatIdsSort(UserLabel userLabel,String appPackage) {
        Date currDate = DateUtil.getNow();
        userLabel.setUpdateDate(currDate);
        String catIdsSort = getUserCatIdsSort(userLabel.getCatId1()+"", appPackage);
        userLabel.setCatIdsSort(catIdsSort);
        userLabel = saveToMQ(userLabel);
        updateUserLabelByRedis(userLabel);
        return userLabel;
    }


    //随机生成catIdsSort总数:用户第一标签条数+后台配置分类数据
    public String getUserCatIdsSort(String cat1,String appPackage){
        //新用户随机生成catIdsSort总数:用户第一标签条数+用户第二标签条数
        int nums = Global.getInt("first_label_value") + Global.getInt("second_label_value");
        List params = Arrays.asList(cat1);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("otherCatIds", params);
        paramMap.put("appPackage", appPackage);
        List<String> catIds = videosCatMapper.findCatIdsNotIn(paramMap);
        Collections.shuffle(catIds);
        catIds.add(0, cat1 + "");
        if (catIds.size() < nums) {
            log.error("获取随机视频分类失败，分类数不足7个，包名：" + appPackage);
            nums = catIds.size();
        }
        String catIdsSort = org.apache.commons.lang3.StringUtils.join(catIds.subList(0, nums).toArray(), ",");
        return catIdsSort;
    }


    @Override
    public UserLabel calculateCatIdsSort(UserLabel userLabel, String appPackage) {
        //获取标签权重分信息
        List<UserLabelGrade> userLabelGradeList = userLabelGradeJpaRepository.getUserLabelGradeList(userLabel.getDeviceId());
        //根据埋点数据重新设置catIdsSort
        StringBuffer catIdsSortStr = getCatIdsSortList(userLabel, userLabelGradeList, appPackage);
        userLabel.setCatIdsSort(catIdsSortStr.toString());
        userLabel.setUpdateDate(new Date());
        return userLabel;
    }

    /**
     * 根据标签权重分信息生成视频标签
     * @param userLabel
     * @param userLabelGradeList
     * @return
     */
    private StringBuffer getCatIdsSortList(UserLabel userLabel, List<UserLabelGrade> userLabelGradeList, String appPackage) {
        StringBuffer catIdsSortStr = new StringBuffer();
        List<Long> catList = new ArrayList<>();
        //catIdsSort总数:用户第一标签条数+用户第二标签条数
        int nums = Global.getInt("first_label_value") + Global.getInt("second_label_value");
        //需随机生成的标签数
        int needNum = nums - userLabelGradeList.size();
        if (CollectionUtils.isNotEmpty(userLabelGradeList)) {
            catList = userLabelGradeList.stream().map(UserLabelGrade::getCatId).collect(toList());
            boolean putFlag = true;
            if (StringUtils.isNotEmpty(userLabel.getCatIdsSort())) {
                String[] catIdsStr = userLabel.getCatIdsSort().split(",");
                if (catIdsStr != null && catIdsStr[0].equals(catList.get(0).toString())) {
                    if (catList.size() > 1) {
                        catList.set(0,catList.get(1));
                        catList.set(1,Long.valueOf(catIdsStr[0]));
                    } else {
                        putFlag = false;
                        needNum = nums;
                    }
                }
            }
            if (putFlag) {
                for (int i = 0; i < catList.size(); i++) {
                    if (i < nums) {
                        catIdsSortStr = catIdsSortStr.append(catList.get(i).toString()).append(",");
                    }
                }
                catIdsSortStr.deleteCharAt(catIdsSortStr.length() - 1);
            }
        }
        //随机生成视频标签
        catIdsSortStr = getCatIdsSortList(userLabel.getCatId1().toString(), userLabel.getCatIdsSort(), needNum, catIdsSortStr, catList, appPackage);
        return catIdsSortStr;
    }

    /**
     * 随机生成视频标签
     * @param CatId1
     * @param needNum
     * @param catIdsSortStr
     * @param catList
     * @return
     */
    private StringBuffer getCatIdsSortList(String CatId1, String catIdsSort, int needNum, StringBuffer catIdsSortStr, List<Long> catList, String appPackage) {
        if (needNum > 0) {
            List<String> strList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(catList)) {
                strList = catList.stream().map(String::valueOf).collect(Collectors.toList());
            } else {
                strList.add(CatId1);
            }
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("otherCatIds", strList);
            paramMap.put("appPackage", appPackage);
            List<String> catIds = videosCatMapper.findCatIdsNotIn(paramMap);
            Collections.shuffle(catIds);
            if (CollectionUtils.isEmpty(catList) && StringUtils.isEmpty(catIdsSort)) {
                catIds.add(0, CatId1);
            }
            if (catIdsSortStr.length() > 0) {
                catIdsSortStr = catIdsSortStr.append(",");
            }
            if (catIds.size() < needNum) {
                log.error("获取随机视频分类失败，分类数不足7个，包名：" + appPackage);
                needNum = catIds.size();
            }
            catIdsSortStr = catIdsSortStr.append(StringUtils.join(catIds.subList(0, needNum).toArray(), ","));
        }
        return catIdsSortStr;
    }

    /**
     * 根据埋点信息获得标签List,按标签分从高到低排序
     * @param deviceId
     * @return
     */
    private List<CatIdWeightVo> getCatIdWeightList(String deviceId) {
        List<CatIdWeightVo> catIdWeightList = new ArrayList<>();
        //获取符合条件的埋点
        List<UserBuryingPointVo> userBuryingPointList = queryUserBuryingPoints(deviceId);
        if(userBuryingPointList.isEmpty()) {
            return catIdWeightList;
        }
        Map<String, BigDecimal> weightMap = new HashMap<>();
        weightMap.put(MAX_WEIGHT_CNT, new BigDecimal(0));
        weightMap.put(TOTAL_WEIGHT_CNT, new BigDecimal(0));
        //返回每个catID的权重，然后倒序排序
        catIdWeightList = getCatIdWeightVos(userBuryingPointList, weightMap);
        if (CollectionUtils.isNotEmpty(catIdWeightList)) {
            catIdWeightList.sort(Comparator.comparing(CatIdWeightVo::getSumCount).reversed().thenComparing(Comparator.comparing(CatIdWeightVo::getCatId).reversed()));
        }
        return catIdWeightList;
    }

    @Override
    public UserLabel getUserLabelByDeviceId(String deviceId) {
        final UserLabel userLabel;
        String key = new StringBuilder("userLabel:").append(deviceId).toString();
        if(redisService.exits(key)) {
            String jsonStr = redisService.get(key,String.class);
            userLabel = JSONObject.parseObject(jsonStr,UserLabel.class);
        } else {
            userLabel = userLabelJpaRepository.findTopByDeviceId(deviceId);
            redisService.set(key,JSONObject.toJSONString(userLabel),30);
        }
        return userLabel;
    }

    @Override
    public UserLabelVo getUserLabelVoByDeviceId(String deviceId, String appPackage) {
        String key = RedisKeyConstant.USERLABEL_KEY + deviceId;
        if (redisService.exits(key)) {
            return redisService.get(key, UserLabelVo.class);
        }
        UserLabel userLabel = userLabelJpaRepository.findTopByDeviceId(deviceId);
        if(userLabel==null){
            //IOS为空处理,给一个默认数据，真正维护用户标签放在MQ，再更新缓存
            userLabel = createIOSUserLabel(deviceId, "xysp_guanwang", appPackage);
        }
        UserLabelVo userLabelVo = this.updateUserLabelByRedis(userLabel);
        return userLabelVo;
    }

    /**
     * 初始化缓存，设置默认Duty
     * @param userLabel
     */
    @Override
    public UserLabelVo updateUserLabelByRedis(UserLabel userLabel) {
        String key = RedisKeyConstant.USERLABEL_KEY + userLabel.getDeviceId();
        UserLabelVo userLabelVo = new UserLabelVo(userLabel, VideoUtils.getDefaultDuty());
        redisService.set(key, JSONObject.toJSONString(userLabelVo), RedisKeyConstant.USERLABELKEY_SECONDS);
        return userLabelVo;
    }

    @Override
    public void updateIdFromRedis(UserLabel userLabel){
        String key = RedisKeyConstant.USERLABEL_KEY + userLabel.getDeviceId();
        if (redisService.exits(key)) {
            UserLabelVo userLabelVo =  redisService.get(key, UserLabelVo.class);
            userLabelVo.getUserLabel().setId(userLabel.getId());
            redisService.set(key, JSONObject.toJSONString(userLabelVo), RedisKeyConstant.USERLABELKEY_SECONDS);
        }
    }

    @Override
    public UserLabelVo updateDBAndRedis(UserLabel userLabel) {
        this.saveToMQ(userLabel);
        return this.updateUserLabelByRedis(userLabel);
    }

    @Override
    public UserLabel saveToDB(UserLabel userLabel){
        UserLabel userLabelResult = null;
        try {
            userLabelResult = this.userLabelJpaRepository.save(userLabel);
        } catch (Exception e) {
            log.error("用户标签唯一索引重复："+userLabel.getDeviceId());
        }
        return userLabelResult;
    }

    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    public void deleteUserLabelDatas() {
        RedisLock redisLock = new RedisLock(RedisKeyConstant.USER_LABEL_DELETE, RedisKeyConstant.USER_LABEL_DELETE_SECONDS);
        if (redisLock.lock()) {
            log.info("用户标签数据迁移：定时器开始！");
            String today = DateUtil.getLatelySaturday(new Date());
            String tableName1 = "user_label_history_"+today;
            String tableName2 = "user_label_grade_history_"+today;
            dynamicQuery.nativeExecuteUpdate("create table IF NOT EXISTS "+tableName1+" like user_label ");
            dynamicQuery.nativeExecuteUpdate("create table IF NOT EXISTS "+tableName2+" like user_label_grade ");
            log.info("创建表{},{}成功.....",tableName1,tableName2);
            String countSQL = "select count(*) from user_label  where TO_DAYS(updated_at) < TO_DAYS(NOW())-7";
            Object o = dynamicQuery.nativeQueryObject(countSQL);
            if(o==null){
                log.info("无可操作数据");
                return;
            }
            int count = Integer.parseInt(o+"");
            int loop = 0;
            while(true){
                String buffer = loop+RabbitMQConstant._MQ_+tableName1+RabbitMQConstant._MQ_+tableName2;
                rabbitTemplate.convertAndSend(
                        RabbitMQConstant.UserLabel_DELETE_EXCHANGE,
                        RabbitMQConstant.UserLabel_DELETE_KEY,buffer);
                if(loop>=count)break;
                loop +=5000;
            }
        }
    }

    @Transactional
    public void deleteUserLabel(int loop,String tableName1,String tableName2) {
        List<UserLabel> userLabels = dynamicQuery.nativeQueryList(UserLabel.class,"select * from user_label  where TO_DAYS(updated_at) < TO_DAYS(NOW())-7 order by id desc limit "+loop+",5000 ");
        String operateDate = DateUtil.parseDateToStr(new Date(),"MMdd");
        if(CollectionUtils.isNotEmpty(userLabels)){
            int insert_rows = 0;
            int delete_rows = 0;
            List<List<UserLabel>> userLabelGroups = Lists.partition(userLabels,MAX);
            List<List<Long>> idsGroups = Lists.newArrayList();
            for(List<UserLabel> userLabelsGroup:userLabelGroups){
                insert_rows += saveUserLabelByGroup(userLabelsGroup,tableName1);
                List<Long> ids =userLabelsGroup.stream().map(UserLabel::getId).collect(Collectors.toList());
                idsGroups.add(ids);
            }
            for(List<Long> ids:idsGroups){
                delete_rows += deleteUserLabelByIds(ids);
            }
            log.info(operateDate+"{}成功插入{}条数据",tableName1,insert_rows);
            log.info(operateDate+"user_label成功删除{}条数据",delete_rows);
            insert_rows = 0;
            delete_rows = 0;
            List<UserLabelGrade> userLabelGrades = dynamicQuery.nativeQueryList(UserLabelGrade.class,"select * from user_label_grade  where device_id in (select device_id from "+tableName1+")");
            if(CollectionUtils.isNotEmpty(userLabelGrades)){
                List<List<UserLabelGrade>> userLabelGradeGroups = Lists.partition(userLabelGrades,MAX);
                idsGroups = Lists.newArrayList();
                for(List<UserLabelGrade> userLabelGrade:userLabelGradeGroups){
                    insert_rows += saveUserLabelGradeByGroup(userLabelGrade,tableName2);
                    List<Long> ids =userLabelGrade.stream().map(UserLabelGrade::getId).collect(Collectors.toList());
                    idsGroups.add(ids);
                }
                for(List<Long> ids:idsGroups){
                    delete_rows += deleteUserLabelGradeByIds(ids);
                }
                log.info(operateDate+"{}成功插入{}条数据",tableName2,insert_rows);
                log.info(operateDate+"user_label_grade成功删除{}条数据",delete_rows);
            }
        }
    }

    private int saveUserLabelGradeByGroup(List<UserLabelGrade> userLabelGrades,String table) {
        int rows = 0;
        List<List<UserLabelGrade>> groups = Lists.partition(userLabelGrades,200);
        for(List<UserLabelGrade> group:groups){
            String sql = "replace INTO `"+table+"` (`id`, `device_id`, `cat_id`, `cat_grade`) VALUES "+ VideoSQLUtils.getInsertValuesByUserLabelGrades(group);
            rows+= dynamicQuery.nativeExecuteUpdate(sql);
        }
        return rows;
    }

    private int saveUserLabelByGroup(List<UserLabel> userLabels,String table) {
        int rows = 0;
        List<List<UserLabel>> groups = Lists.partition(userLabels,200);
        for(List<UserLabel> group:groups){
            String sql = "replace INTO `"+table+"` (`id`, `cat_id1`,`cat_id2`,`device_id`, `cat_ids_sort`) VALUES "+ VideoSQLUtils.getInsertValuesByUserLabels(group);
            rows+= dynamicQuery.nativeExecuteUpdate(sql);
        }
        return rows;
    }

    private int deleteUserLabelByIds(List<Long> ids) {
        String sql = "delete from user_label where id in ("+StringUtils.join(ids,",")+")";
        return dynamicQuery.nativeExecuteUpdate(sql);
    }

    private int deleteUserLabelGradeByIds(List<Long> ids) {
        String sql = "delete from user_label_grade where id in ("+StringUtils.join(ids,",")+")";
        return dynamicQuery.nativeExecuteUpdate(sql);
    }

}
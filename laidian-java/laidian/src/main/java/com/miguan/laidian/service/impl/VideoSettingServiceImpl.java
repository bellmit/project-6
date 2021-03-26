package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.Video;
import com.miguan.laidian.entity.VideoSettingPhone;
import com.miguan.laidian.entity.VideoSettingUser;
import com.miguan.laidian.mapper.VideoMapper;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.repositories.VideoSettingPhoneDao;
import com.miguan.laidian.repositories.VideoSettingUserDao;
import com.miguan.laidian.service.VideoService;
import com.miguan.laidian.service.VideoSettingService;
import com.miguan.laidian.vo.VideoSettingDetailVo;
import com.miguan.laidian.vo.VideoSettingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service("videoSettingService")
public class VideoSettingServiceImpl implements VideoSettingService {

    @Resource
    private VideoSettingUserDao videoSettingUserDao;

    @Resource
    private VideoSettingPhoneDao videoSettingPhoneDao;

    @Resource
    private VideoService videoService;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private RedisService redisService;

    /**
     * 根据用户Id保存视频信息到记录表：1.没有历史记录则新增记录
     * 2.有历史记录，且当前保存的视频Id存在历史记录，则只更新updateDate字段
     * 3.有历史记录，且当前保存的视频Id不存在历史记录，且该setType未满30条则新增记录
     * 4.有历史记录，且当前保存的视频Id不存在历史记录，且该setType已满30条则覆盖第30条（更新时间最早的一条）
     *
     * @param commomParams
     * @param videoId
     * @param setType
     */
    @Override
    public void saveVideoSettingInfo(CommonParamsVo commomParams, String videoId, String setType) {
        Long userId = Long.valueOf(commomParams.getUserId());
        Long videoIdL = Long.valueOf(videoId);
        Integer setTypeI = Integer.valueOf(setType);
        List<VideoSettingUser> videoSettingUserList = getVideoUserInfoCacheList(userId, setTypeI, "1");
        VideoSettingUser videoSettingUser;
        if (videoSettingUserList == null) {
            videoSettingUser = setVideoSettingUser(videoIdL, setTypeI, userId);
        } else {
            videoSettingUser = videoSettingUserList.stream().filter(v -> videoIdL.equals(v.getVideoId())).findAny().orElse(null);
            if (videoSettingUser == null) {
                if (videoSettingUserList.size() >= 30) {
                    videoSettingUser = videoSettingUserList.get(29);
                    videoSettingUser.setVideoId(videoIdL);
                } else {
                    videoSettingUser = setVideoSettingUser(videoIdL, setTypeI, userId);
                }
            }
        }
        videoSettingUser.setUpdateDate(new Date());
        videoSettingUserDao.save(videoSettingUser);
    }

    /**
     * 初始化VideoSettingUser
     *
     * @param videoIdL
     * @param setTypeI
     * @param userId
     * @return
     */
    private VideoSettingUser setVideoSettingUser(Long videoIdL, Integer setTypeI, Long userId) {
        VideoSettingUser vUser = new VideoSettingUser();
        vUser.setUserId(userId);
        vUser.setVideoId(videoIdL);
        vUser.setSetType(setTypeI);
        vUser.setCreateDate(new Date());
        return vUser;
    }

    @Override
    public List<Video> findVideoSettingInfo(CommonParamsVo commomParams, String setType) {
        List<Video> list = new ArrayList<>();
        Integer setTypeI = Integer.valueOf(setType);
        Long userId = Long.valueOf(commomParams.getUserId());
        List<VideoSettingUser> videoSettingUserList = getVideoUserInfoCacheList(userId, setTypeI, "2");
        if (CollectionUtils.isNotEmpty(videoSettingUserList)) {
            String settingIds = videoSettingUserList.stream().map(s -> s.getVideoId().toString()).collect(Collectors.joining(","));
            list = getVideos(commomParams, settingIds);
        }
        return list;
    }

    void saveVideoSettingPhone(String userId, String videoId, String phone) {
        //根据时间正序排序获取集合
        List<VideoSettingPhone> allByUserIdAndPhone = videoSettingPhoneDao.findAllByUserIdAndPhoneOrderByUpdateDateAsc(Long.valueOf(userId), phone);
        if (CollectionUtils.isEmpty(allByUserIdAndPhone)) allByUserIdAndPhone = Lists.newArrayList();
        VideoSettingPhone videoSettingPhone = ifContains(allByUserIdAndPhone, videoId);
        if (videoSettingPhone != null) {
            videoSettingPhone.setUpdateDate(new Date());
            videoSettingPhoneDao.save(videoSettingPhone);
        } else {
            if (allByUserIdAndPhone.size() >= 30) {
                videoSettingPhone = allByUserIdAndPhone.get(0);
            } else {
                videoSettingPhone = new VideoSettingPhone();
                videoSettingPhone.setUserId(Long.valueOf(userId));
                videoSettingPhone.setPhone(phone);
                videoSettingPhone.setCreateDate(new Date());
            }
            videoSettingPhone.setVideoId(Long.valueOf(videoId));
            videoSettingPhone.setUpdateDate(new Date());
            videoSettingPhoneDao.save(videoSettingPhone);
        }

    }

    @Override
    @Transactional
    public ResultMap saveVideoSettingPhone(CommonParamsVo commomParams, String videoId, String phones) {
        String[] phonesArra = phones.split(",");
        for (String phone : phonesArra) {
            saveVideoSettingPhone(commomParams.getUserId(), videoId, phone);
            //删除当前key中元素
            redisService.delRedisByKey("findAllByUserIdAndPhoneOrderByUpdateDateDesc::laidian:cacheAble:findAllByUserIdAndPhoneOrderByUpdateDateDesc:" + commomParams.getUserId() + phone);
        }
        return ResultMap.success();
    }

    public VideoSettingPhone ifContains(List<VideoSettingPhone> allByUserIdAndPhone, String videoId) {
        for (VideoSettingPhone videoSettingPhone1 : allByUserIdAndPhone) {
            if (videoSettingPhone1.getVideoId().toString().equals(videoId)) {
                return videoSettingPhone1;
            }
        }
        return null;
    }


    @Override
    public List<Video> findVideoSettingPhone(CommonParamsVo commomParams, String phone) {
        List<Video> list = new ArrayList<>();
        List<VideoSettingPhone> allByUserIdAndPhone = videoSettingPhoneDao.findAllByUserIdAndPhoneOrderByUpdateDateDesc(Long.valueOf(commomParams.getUserId()), phone);
        String settingIds = allByUserIdAndPhone.stream().map(s -> s.getVideoId().toString()).collect(Collectors.joining(","));
        if ("".equals(settingIds)) {
            return list;
        }
        List<Video> videos = getVideos(commomParams, settingIds);
        return videos;
    }

    @Override
    @Transactional
    public int delVideoSettinginfo(String userId, String videoIds, String phone, String setType) {
        Long userIdL = Long.valueOf(userId);
        Integer setTypeI = Integer.valueOf(setType);
        List<Long> videoIdsL = new ArrayList<>();
        if (videoIds.contains(",")) {
            String[] str = videoIds.split(",");
            for (int i = 0; i < str.length; i++) {
                videoIdsL.add(Long.valueOf(str[i]));
            }
        } else {
            videoIdsL.add(Long.valueOf(videoIds));
        }
        if (setTypeI == 5) {
            redisService.delRedisByKey("findAllByUserIdAndPhoneOrderByUpdateDateDesc::laidian:cacheAble:findAllByUserIdAndPhoneOrderByUpdateDateDesc:" + userId + phone);
            return videoSettingPhoneDao.deleteVideoSettingPhone(userIdL, videoIdsL, phone);
        } else {
            redisService.del(RedisKeyConstant.VIDEO_SETTING + userId);
            return videoSettingUserDao.deleteVideoSettingUser(userIdL, videoIdsL, setTypeI);
        }
    }

    /**
     * 查询视频列表信息
     *
     * @param commomParams
     * @param settingIds
     * @return
     */
    private List<Video> getVideos(CommonParamsVo commomParams, String settingIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("settingIds", settingIds);
        params.put("state", Constant.open);//状态 1开启 2关闭
        List<Video> videosList = videoMapper.findVideosList(params);
        //视频是否历史设置过（登录状态才判断）
        String userId = commomParams.getUserId();
        if (StringUtils.isNotBlank(userId)&&!"0".equals(userId)){
            List<Long> videoIds = this.judgeUserIsSet(Long.valueOf(userId));
            if (CollectionUtils.isNotEmpty(videoIds)&&CollectionUtils.isNotEmpty(videosList)){
                for (int i = 0; i < videosList.size(); i++) {
                    Video video = videosList.get(i);
                    if(videoIds.contains(video.getId())){
                        video.setHistoryTab(1);//历史设置过
                    }
                }
            }
        }
        videoService.setAdvConfig(commomParams.getAppType(), videosList);
        return videosList;
    }

    /**
     * 用户Id查询视频设置记录历史缓存信息：
     * 一个用户ID一条缓存记录，每条缓存记录里面一个setType为一个list,每个list里面包含该分类的所有视频历史记录list
     *
     * @param userId
     * @param setType
     * @param getType 获取缓存类型：1保存，2查询。查询接口保存缓存记录到redis，保存接口查询并删除该类型的缓存记录
     */
    private List<VideoSettingUser> getVideoUserInfoCacheList(Long userId, Integer setType, String getType) {
        String key = RedisKeyConstant.VIDEO_SETTING + userId;
        List<VideoSettingUser> videoSettingUserList = null;
        List<VideoSettingDetailVo> voList = new ArrayList<>();
        VideoSettingVo videoSettingVo = new VideoSettingVo();
        if (redisService.exits(key)) {
            videoSettingVo = redisService.get(key, VideoSettingVo.class);
            voList = videoSettingVo.getVideoSettingDetailVoList();
            if ("1".equals(getType)) {
                videoSettingUserList = videoCacheForSave(videoSettingVo, voList, key, setType);
            } else {
                for (VideoSettingDetailVo videoSettingDetailVo : voList) {
                    if (setType == videoSettingDetailVo.getSetType()) {
                        videoSettingUserList = videoSettingDetailVo.getVideoSettingUserList();
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(videoSettingUserList)) {
            videoSettingUserList = videoSettingUserDao.findVideoSettingUserInfo(userId, setType);
            if (CollectionUtils.isNotEmpty(videoSettingUserList) && "2".equals(getType)) {
                VideoSettingDetailVo videoSettingDetailVo = new VideoSettingDetailVo();
                videoSettingDetailVo.setSetType(setType);
                videoSettingDetailVo.setVideoSettingUserList(videoSettingUserList);
                voList.add(videoSettingDetailVo);
                videoSettingVo.setVideoSettingDetailVoList(voList);
                redisService.set(key, JSONObject.toJSONString(videoSettingVo), RedisKeyConstant.VIDEO_SETTING_SECONDS);
            }
        }
        return videoSettingUserList;
    }

    /**
     * 保存接口redis操作
     *
     * @param videoSettingVo
     * @param voList
     * @param key
     * @param setType
     * @return
     */
    private List<VideoSettingUser> videoCacheForSave(VideoSettingVo videoSettingVo, List<VideoSettingDetailVo> voList, String key, Integer setType) {
        List<VideoSettingUser> videoSettingUserList = null;
        boolean exitFlag = false;
        for (VideoSettingDetailVo videoSettingDetailVo : voList) {
            if (setType == videoSettingDetailVo.getSetType()) {
                videoSettingUserList = videoSettingDetailVo.getVideoSettingUserList();
                voList.remove(videoSettingDetailVo);
                exitFlag = true;
                break;
            }
        }
        if (exitFlag) {
            //redis存在该类别的缓存，则重新保存或删除redis
            if (CollectionUtils.isNotEmpty(voList)) {
                redisService.set(key, JSONObject.toJSONString(videoSettingVo), RedisKeyConstant.VIDEO_SETTING_SECONDS);
            } else {
                redisService.del(key);
            }
        }
        return videoSettingUserList;
    }

    public List<Long> judgeUserIsSet(Long userId) {
        List<VideoSettingPhone> list1 = videoSettingPhoneDao.queryVideoSettingPhoneByUserId(userId);
        List<VideoSettingUser>  list2 = videoSettingUserDao.queryVideoSettingUserByUserId(userId);
        List<Long> bList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list2)){
            bList = list2.stream().map(VideoSettingUser::getVideoId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(list1)) {
                List<Long> aList = list1.stream().map(VideoSettingPhone::getVideoId).collect(Collectors.toList());
                for (int i = 0; i < aList.size(); i++) {
                    Long aLong = aList.get(i);
                    if (!bList.contains(aLong)) {
                        bList.add(aLong);
                    }
                }
            }
        }
        return bList;
    }
}

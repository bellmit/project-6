package com.miguan.laidian.service;

import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.Video;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VideoSettingService {
    /**
     * 保存视频设置记录信息
     *
     * @param commomParams
     * @return
     */
    @Transactional
    void saveVideoSettingInfo(CommonParamsVo commomParams, String videoId, String setType);

    /**
     * 根据用户Id查询视频设置记录信息
     *
     * @param commomParams
     * @return
     */
    List<Video> findVideoSettingInfo(CommonParamsVo commomParams, String setType);


    /**
     * 联系人保存视频设置记录信息
     *
     * @param commomParams
     * @return
     */
    ResultMap saveVideoSettingPhone(CommonParamsVo commomParams, String videoId, String phone);

    /**
     * 根据用户Id或者设备Id查询专属视频设置记录信息
     *
     * @param commomParams
     * @return
     */
    List<Video> findVideoSettingPhone(CommonParamsVo commomParams, String phone);

    /**
     * 视频设置记录信息删除
     * @param userId
     * @param videoIds
     * @param setType
     * @return
     */
    int delVideoSettinginfo(String userId, String videoIds, String phone, String setType);

    /**
     * 用户历史设置过视频ID
     *
     * @param userId
     * @return
     */
    List<Long> judgeUserIsSet(Long userId);
}

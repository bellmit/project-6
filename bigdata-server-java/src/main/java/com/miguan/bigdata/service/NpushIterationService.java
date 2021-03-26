package com.miguan.bigdata.service;

import com.miguan.bigdata.dto.NpushIterationDto;
import com.miguan.bigdata.entity.npush.PushDataIterArcticle;
import com.miguan.bigdata.entity.npush.PushDataIterConf;

import java.util.List;

public interface NpushIterationService {

    /**
     * 根据激活日，初始化设备推送状态
     * @param actDay yyyy-MM-dd
     */
    public void initDistinctNpushStateOfSomeActDay(String actDay);

    /**
     * 将设备的推送状态修改为禁止
     * @param distinctId
     */
    public void updateDistinctPushStateToStop(String distinctId);

    /**
     * 根据星期获取推送时间
     *
     * @param dayOfWeek
     * @return
     */
    public List<PushDataIterConf> getTaskFromConfig(Integer dayOfWeek);

    /**
     * 根据推送时间的序号，获取推送内容
     *
     * @param projectType 项目
     * @param actDay 激活日
     * @param sortNum 批次
     * @return
     */
    public List<PushDataIterArcticle> getPushDateIterArcticleByActDatAndSortNum(Integer projectType, Integer actDay, Integer sortNum);

    /**
     * 获取备选推送内容
     *
     * @param projectType 项目
     * @param actDay 激活日
     * @param maxBatchNum 最大批次
     * @return
     */
    public List<PushDataIterArcticle> getAlternativeArcticleList(Integer projectType, Integer actDay, Integer maxBatchNum);

    /**
     * 根据激活日，从Mongo表查询设备进行推送
     * @param dto 推送信息
     * @param arcticle 推送素材
     * @param excludeChannel 屏蔽推送的渠道
     */
    public void pushAnyUserAsSomeOneActDayFromMongo(NpushIterationDto dto, PushDataIterArcticle arcticle, List<String> excludeChannel);

}

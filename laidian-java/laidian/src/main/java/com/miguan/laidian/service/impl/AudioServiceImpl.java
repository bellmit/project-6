package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.laidian.common.constants.AudioConstant;
import com.miguan.laidian.mapper.AudioDownloadMapper;
import com.miguan.laidian.mapper.AudioMapper;
import com.miguan.laidian.mapper.AudioUserMapper;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.AudioService;
import com.miguan.laidian.vo.AudioCatVo;
import com.miguan.laidian.vo.AudioDownloadVo;
import com.miguan.laidian.vo.AudioUserVo;
import com.miguan.laidian.vo.AudioVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 音频ServiceImpl
 *
 * @author xy.chen
 * @date 2020-05-22
 **/

@Slf4j
@Service("audioService")
public class AudioServiceImpl implements AudioService {

    @Resource
    private AudioMapper audioMapper;

    @Resource
    private AudioUserMapper audioUserMapper;

    @Resource
    private AudioDownloadMapper audioDownloadMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public List<AudioCatVo> findAudioCatList() {
        return audioMapper.findAudioCatList();
    }

    @Override
    public AudioVo findAudioDetail(Long audioId) {
        Map<String, Object> params = new HashMap<>();
        params.put("audioId", audioId);
        List<AudioVo> audioList = audioMapper.findAudioList(params);
        if (CollectionUtils.isNotEmpty(audioList)){
            return audioList.get(0);
        }
        return null;
    }


    @Override
    public Page<AudioVo> findAudioList(Map<String, Object> params, int currentPage, int pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AudioVo> audioList = audioMapper.findAudioList(params);
        return (Page<AudioVo>) audioList;
    }

    @Override
    public void updateCountSendMQ(Map<String, Object> params) {
        String json = JSON.toJSONString(params);
        rabbitTemplate.convertAndSend(RabbitMQConstant.AUDIO_UPDATECOUNT_EXCHANGE, RabbitMQConstant.AUDIO_UPDATECOUNT_KEY, json);
    }

    @Transactional
    @Override
    public void updateAudioCount(Map<String, Object> params) {
        try {
            String opType = MapUtils.getString(params, "opType");
            AudioUserVo audioUserVo = audioUserMapper.getAudioUserByAudioIdAndDeviceId(params);
            if (audioUserVo != null) {
                //试听数（一个用户同一个铃声点击试听只统计一次，重复点击不记录内）
                if (audioUserVo.getAudition() == 1 && AudioConstant.OP_AUDITION.equals(opType)) {
                    return;
                }
                //下载数（一个用户同一个铃声设为铃声只统计一次，重复设置不记录内）
                if (audioUserVo.getDownload() == 1 && AudioConstant.OP_DOWNLOAD.equals(opType)) {
                    return;
                }
            }
            audioMapper.updateAudioCount(params);//更新音频主表
            long audioId = Long.valueOf(MapUtils.getString(params, "audioId"));
            String deviceId = MapUtils.getString(params, "deviceId");
            if (audioUserVo == null) {
                //保存音频用户关系表信息
                this.saveAudioUser(opType, audioId, deviceId);
            } else {
                audioUserMapper.updateAudioCount(params);
            }
            //操作类型：10--收藏 20--分享 30--取消收藏 40--试听数 50--下载数
            if (AudioConstant.OP_DOWNLOAD.equals(opType)) {
                //保存音频下载记录（方便PHP统计最近三小时内的下载数）
                this.saveAudioDownload(audioId, deviceId);
            }
        } catch (Exception e) {
            log.error("更新收藏数、分享数、下载数、试听数报错数据：{}", JSON.toJSONString(params));
            log.error(e.getMessage(), e);
        }
    }

    //保存音频下载记录（方便PHP统计最近三小时内的下载数）
    private void saveAudioDownload(long audioId, String deviceId) {
        AudioDownloadVo audioDownloadVo = new AudioDownloadVo();
        audioDownloadVo.setDeviceId(deviceId);
        audioDownloadVo.setAudioId(audioId);
        audioDownloadMapper.saveAudioDownload(audioDownloadVo);
    }

    //保存音频用户关系表信息
    private void saveAudioUser(String opType, long audioId, String deviceId) {
        AudioUserVo audioUserVo = new AudioUserVo();
        audioUserVo.setDeviceId(deviceId);
        audioUserVo.setAudioId(audioId);
        audioUserVo.setCollection(AudioConstant.OP_COLLECTION.equals(opType) ? 1 : 0);
        audioUserVo.setShareCount(AudioConstant.OP_SHARE.equals(opType) ? 1 : 0);
        audioUserVo.setAudition(AudioConstant.OP_AUDITION.equals(opType) ? 1 : 0);
        audioUserVo.setDownload(AudioConstant.OP_DOWNLOAD.equals(opType) ? 1 : 0);
        audioUserMapper.saveAudioUser(audioUserVo);
    }

    /**
     * 凌晨4点，删除前一天的音频下载记录
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void delAudioDownload() {
        audioDownloadMapper.delAudioDownload();
    }
}
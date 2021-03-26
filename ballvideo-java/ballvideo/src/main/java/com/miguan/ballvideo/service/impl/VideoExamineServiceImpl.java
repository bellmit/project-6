package com.miguan.ballvideo.service.impl;


import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.entity4.MessageDictionary;
import com.miguan.ballvideo.entity4.MessageModel;
import com.miguan.ballvideo.mapper.*;
import com.miguan.ballvideo.service.VideoExamineService;
import com.miguan.ballvideo.vo.ClUserOpinionVo;
import com.miguan.ballvideo.vo.ClUserVo;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.video.FirstVideosVo;
import com.miguan.ballvideo.vo.video.VideoExamineVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频审核 Service
 *
 * @author xy.chen
 * @date 2019-09-09
 **/
@Slf4j
@Service
public  class VideoExamineServiceImpl implements VideoExamineService {

    @Resource
    private VideoExamineMapper videoExamineMapper;
    @Resource
    private ToolMofangServiceImpl toolMofangService;
    @Resource
    private FirstVideosMapper firstVideosMapper;
    @Resource
    private ClUserMapper clUserMapper;

    /** 判断审核是否审核过，审核过的话，返回内容准备组装消息本身*/
    @Override
    public VideoExamineVo getVideoExamineAudited(String videoExamineId) {
        Map<String, Object> search = new HashMap<>(2);
        search.put("id", videoExamineId);
        search.put("examineStatus", 0);

        VideoExamineVo videoExamineVo = videoExamineMapper.findSelective(search);
        return videoExamineVo;
    }

    @Override
    public Map<String, Object> getRelateObjByVideoExamineVo(VideoExamineVo videoExamineVo) {
        FirstVideos firstVideos = firstVideosMapper.getFirstVideosTitleById(videoExamineVo.getVideoId().longValue());

        ClUserVo clUserVo = clUserMapper.findClUserById(String.valueOf(videoExamineVo.getUserId()));

        Map<String, Object> result = new HashMap<>(2);
        result.put("firstVideos",firstVideos);
        result.put("clUserVo",clUserVo);

        return result;
    }

    @Override
    public MessageModel getMessageModelByVideoExamineVo(VideoExamineVo videoExamineVo){
        List<MessageModel> messageModelList = toolMofangService.getMessageModelByModelId(String.valueOf(videoExamineVo.getMessageModelId()));
        if(CollectionUtils.isNotEmpty(messageModelList)){
            return messageModelList.get(0);
        }else{
            return null;
        }

    }

    @Override
    public List<MessageDictionary> getMessageDictionaryByVideoExamineVo(VideoExamineVo videoExamineVo) {
        List<MessageDictionary> messageDictionaryList = toolMofangService.getMessageDictionaryByModelId(String.valueOf(videoExamineVo.getMessageModelId()));
        return messageDictionaryList;
    }

    @Override
    public List<MessageDictionary> getReplaceListByDictionary(List<MessageDictionary> messageDictionarySourceList, FirstVideos firstVideos, ClUserVo clUserVo) {
        messageDictionarySourceList.forEach(messageDictionary -> {
            if("sp_Video_title".equals(messageDictionary.getEnglishField())){
                messageDictionary.setChineseExplain(firstVideos.getTitle());
            }
            if("sp_User_name".equals(messageDictionary.getEnglishField())){
                messageDictionary.setChineseExplain(clUserVo.getName());
            }
        });
        return messageDictionarySourceList;
    }

    @Override
    public String assembleMessageContent(String content, List<MessageDictionary> replaceMessageContentList) {
        String message = content;

        if(CollectionUtils.isNotEmpty(replaceMessageContentList)){
            for(MessageDictionary messageDictionary:replaceMessageContentList){
                message = message.replaceAll("\\{"+messageDictionary.getEnglishField()+"\\}",messageDictionary.getChineseExplain());
                //log.info("message = " + message);
            }
        }

        return message;
    }

}

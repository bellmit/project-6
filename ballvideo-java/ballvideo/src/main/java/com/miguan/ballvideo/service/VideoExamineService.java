package com.miguan.ballvideo.service;


import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.entity4.MessageDictionary;
import com.miguan.ballvideo.entity4.MessageModel;
import com.miguan.ballvideo.vo.ClUserVo;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.video.VideoExamineVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频分享Service
 *
 * @author xy.chen
 * @date 2019-09-09
 **/
public interface VideoExamineService {

    /**
     * 判断审核是否审核过，审核过的话，返回内容准备组装消息本身
     * @param videoExamineId
     * @return
     */
    VideoExamineVo getVideoExamineAudited(String videoExamineId);

    /**
     * 获取这个审核申请会用到的对象，分别是视频和用户对象
     * @param videoExamineVo
     * @return
     */
    Map<String, Object> getRelateObjByVideoExamineVo(VideoExamineVo videoExamineVo);

    /**
     * 根据项目id，获取对应的模板
     * @param videoExamineVo
     * @return
     */
    MessageModel getMessageModelByVideoExamineVo(VideoExamineVo videoExamineVo);

    /**
     * 根据项目id，获取对应可能的关键字列表
     * @param videoExamineVo
     * @return
     */
    List<MessageDictionary> getMessageDictionaryByVideoExamineVo(VideoExamineVo videoExamineVo);

    /**
     * 据约定，把字典中的内容先替换为最终内容
     * @param messageDictionarySourceList
     * @param firstVideos
     * @param clUserVo
     * @return
     */
    List<MessageDictionary> getReplaceListByDictionary(List<MessageDictionary>  messageDictionarySourceList,FirstVideos firstVideos,ClUserVo clUserVo);


    /**
     * 返回组装后的内容
     * * @param content
     * @param replaceMessageContentList
     * @return
     */
    String assembleMessageContent(String content, List<MessageDictionary>  replaceMessageContentList);
}

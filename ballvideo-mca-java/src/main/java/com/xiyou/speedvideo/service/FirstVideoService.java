package com.xiyou.speedvideo.service;

import com.xiyou.speedvideo.dto.BaiduLabelParams;
import com.xiyou.speedvideo.dto.LabelDto;
import com.xiyou.speedvideo.dto.VideoLabelDto;
import com.xiyou.speedvideo.entity.FirstVideos;
import com.xiyou.speedvideo.entity.FirstVideosMca;
import com.xiyou.speedvideo.entity.FirstVideosMcaResult;

import java.util.List;
import java.util.Map;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/10/14 4:28 下午
 */
public interface FirstVideoService {

    List<String> getMCAExistList();

    List<FirstVideos> getDownloadList(Map<String, Object> paramMap);

    boolean insertDownloading(List<FirstVideos> downloadList);

    FirstVideosMca downloadAndUpdate(FirstVideos firstVideos,String path);

    List<FirstVideosMcaResult> speedAndUpdate(List<String> cmdList,FirstVideosMca mca);

    List<FirstVideosMca> getDownloadCompleteList();

    List<FirstVideosMcaResult> getWaitUploadList();

    List<FirstVideosMcaResult> getWaitApplyList();

    void doUploadAndUpdateResult(FirstVideosMcaResult result);

    void doResultSave(String param);

    void doApplyAction(FirstVideosMcaResult result);

    List<FirstVideosMcaResult> getMCAResult(Map<String, Object> paramMap);

    /**
     * 调用百度云ai解析视频标签
     * @param path
     * @param videoIds
     * @param limit
     * @param watchCount
     * @param minute
     * @param secondStart
     * @param secondEnd
     * @return
     */
    String allInOneMethod(String path, String videoIds, Integer limit, Integer watchCount, Integer minute, Integer secondStart, Integer secondEnd);

    /**
     * 获取百度云AI标签解析结果,如果视频没解析则开启解析任务
     * @param blParams
     * @return
     */
    List<VideoLabelDto> getBaiduLabelResult(List<BaiduLabelParams> blParams);

    /**
     * 获取算法AI标签解析结果
     * @param videoIds 视频id，多个则逗号分隔
     * @return
     */
    List<VideoLabelDto> getAlgorithmLabelResult(List<Integer> videoIds);

    /**
     * 解析百度返回的json，并返回视频的人物和图谱标签类型
     * @param labelJsonStr
     * @param index 获取置信值最高的前index个标签。如果为0，则获取全部
     * @return
     */
    List<LabelDto> parseLabelJson(String labelJsonStr, int index);

    /**
     * 上报历史百度云解析的视频标签
     */
    void upLoadHistoryVideoLabel(Integer startRow);

    /**
     * 上报历史算法解析的视频标签
     */
    void uploadHistoryAlgorithmLabel(Integer startRow);

    /**
     * 调用视频标签上报接口
     * @param bsyUrl 视频地址
     * @param jsonResult 解析结果json
     * @param type 类型：1--百度云AI，2--算法解析
     */
    void labelUpLoad(Integer videoId, String bsyUrl, String jsonResult, int type);
}

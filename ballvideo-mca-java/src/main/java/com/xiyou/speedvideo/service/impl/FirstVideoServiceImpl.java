package com.xiyou.speedvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidubce.services.vca.model.AnalyzeResponse;
import com.baidubce.services.vca.model.QueryResultResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiyou.speedvideo.dao.VideoMcaResultDao;
import com.xiyou.speedvideo.dto.BaiduLabelParams;
import com.xiyou.speedvideo.dto.LabelDto;
import com.xiyou.speedvideo.dto.VideoLabelDto;
import com.xiyou.speedvideo.entity.FirstVideos;
import com.xiyou.speedvideo.entity.FirstVideosMca;
import com.xiyou.speedvideo.entity.FirstVideosMcaResult;
import com.xiyou.speedvideo.entity.LabelUpLoadLog;
import com.xiyou.speedvideo.entity.mongo.VideoPaddleTag;
import com.xiyou.speedvideo.enums.LabelTypeEnum;
import com.xiyou.speedvideo.mapper.FirstVideosMapper;
import com.xiyou.speedvideo.mapper.FirstVideosMcaMapper;
import com.xiyou.speedvideo.mapper.FirstVideosMcaResultMapper;
import com.xiyou.speedvideo.service.FirstVideoService;
import com.xiyou.speedvideo.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/10/14 4:28 下午
 */
@Service
@Slf4j
public class FirstVideoServiceImpl implements FirstVideoService {

    @Resource
    private FirstVideosMapper firstVideosMapper;

    @Resource
    private FirstVideosMcaMapper firstVideosMcaMapper;

    @Resource
    private FirstVideosMcaResultMapper firstVideosMcaResultMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private MongoTemplate mongoTemplate;

    @Value("${content-label-server.video-label}")
    private String contentLabelUrl;

    private static ExecutorService executor = new ThreadPoolExecutor(14, 20,
            60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    /**
     * ffmpeg脚本临时路径
     */
    private static final String tmpDir = "/usr/local/webserver/mca/shell";

    /**
     * 白山链接前缀
     */
    private static final String bsPrefix = "https://ss.bscstorage.com/xiyou-huangjunxian/speed-video/";

    /**
     * 视频提交到百度云解析
     * @param videos
     */
    private void doMcaApplyAction(List<BaiduLabelParams> videos) {
        for(BaiduLabelParams video: videos) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("视频提交到百度云解析(start)："+ JSON.toJSONString(video));
                    AnalyzeResponse response = MCAUtil.analyzeMedia(video.getBsyUrl());
                    log.info("视频提交到百度云解析返回结果：{}", response);
                    FirstVideosMcaResult result = new FirstVideosMcaResult(video.getVideoId(),video.getBsyUrl(),FirstVideosMcaResult.STATE_APPLYING,null,null,new Date());
                    firstVideosMcaResultMapper.insert(result);
                    log.info("视频提交到百度云解析(end)："+ JSON.toJSONString(video));
                }
            });
        }
    }

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
    public String allInOneMethod(String path, String videoIds, Integer limit, Integer watchCount, Integer minute, Integer secondStart, Integer secondEnd) {
        // 获取下载列表
        Map paramMap = new HashedMap();
        paramMap.put("videoIds",CommonUtil.string2List(videoIds,","));
        paramMap.put("excludeList",this.getMCAExistList());
        paramMap.put("limit",limit);
        paramMap.put("watchCount",watchCount);
        paramMap.put("minute",minute);
        paramMap.put("secondStart",secondStart);
        paramMap.put("secondEnd",secondEnd);
        List<FirstVideos> resultList = this.getDownloadList(paramMap);
        // 判断路径没有则创建
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        if(resultList.isEmpty()){
            return "error不存在待解析视频！";
        }
        //信息插入待处理表
        if(this.insertDownloading(resultList)){
            // 下载并更新状态
            for (FirstVideos videos : resultList) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //1、下载视频
                        FirstVideosMca mca = downloadAndUpdate(videos,path);
                        log.info("视频下载完成："+ JSON.toJSONString(mca));
                        //2、FFMPEG
                        List<String> cmdList = new ArrayList<>();
                        String cmd = CommonUtil.getCmdByRule(mca);
                        if(cmd!=null){
                            cmdList.add(cmd);
                        }
                        mca.setState(FirstVideosMca.STATE_SPEED_COMPLETE);
                        //执行ffmpeg
                        List<FirstVideosMcaResult> resultList = speedAndUpdate(cmdList,mca);
                        log.info("视频转换完成："+ JSON.toJSONString(resultList));
                        //3、上传白山
                        for(FirstVideosMcaResult result:resultList){
                            doUploadAndUpdateResult(result);
                            log.info("视频上传白山完成："+ JSON.toJSONString(result));
                            //4、发起MCA
                            doApplyAction(result);
                            log.info("视频发起MCA完成："+ JSON.toJSONString(result));
                        }
                    }
                });
            }
        }
        return String.valueOf(resultList.size());
    }

    @Override
    public List<String> getMCAExistList(){
        List<String> resultList = firstVideosMapper.getMCAExistList();
        return resultList.size()==0?null:resultList;
    }

    @Override
    public List<FirstVideos> getDownloadList(Map<String, Object> paramMap){
        return firstVideosMapper.getDownloadList(paramMap);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean insertDownloading(List<FirstVideos> downloadList){
        int result = firstVideosMapper.batchInsert2MCA(downloadList);
        return result>0;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public FirstVideosMca downloadAndUpdate(FirstVideos firstVideos,String path){
        String filePath = DownloadURLFile.downloadFromUrl(firstVideos.getBsyUrl(), path,String.valueOf(firstVideos.getId()));
        FirstVideosMca firstVideosMca = new FirstVideosMca();
        firstVideosMca.setVideoId(firstVideos.getId());
        firstVideosMca.setLocalPath(filePath);
        firstVideosMca.setState(filePath!=null ? FirstVideosMca.STATE_DOWNLOAD_COMPLETE : FirstVideosMca.STATE_DOWNLOAD_ERROR);
        firstVideosMapper.updateMCAByVideoId(firstVideosMca);
        //找出firstVideosMca返回
        firstVideosMca = firstVideosMapper.selectMCAByVideoId(firstVideosMca);
        return firstVideosMca;
    }

    @Override
    public List<FirstVideosMcaResult> speedAndUpdate(List<String> cmdList,FirstVideosMca mca){
        List<FirstVideosMcaResult> resultList = new ArrayList<>();
        if(cmdList!=null && cmdList.size()>=0){
            // 写入临时文件
            String tmpFilePrefix = tmpDir + "/" + System.currentTimeMillis() + new Random().nextInt(999);
            String inputTxt = tmpFilePrefix + "/speed.sh";
            FileUtils.mkdir(tmpFilePrefix);
            try {
                FileUtils.writeLineToFile(cmdList, inputTxt);
                String cmd = "sh "+inputTxt;
                System.out.println(cmd);
                FileUtils.execCmd(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 删除临时文件（特征文件、预测结果、bin文件）
            FileUtils.deleteDir(tmpFilePrefix);
            resultList = doSpeedUpdate(cmdList,mca);
        }
        return resultList;
    }

    @Transactional(rollbackFor=Exception.class)
    public List<FirstVideosMcaResult> doSpeedUpdate(List<String> cmdList,FirstVideosMca mca){
        List<FirstVideosMcaResult> resultList = new ArrayList<>();
        //更新状态
        if(firstVideosMapper.updateMCAByVideoId(mca)>0){
            //原视频加入解析，这块后期可以去掉
//            FirstVideosMcaResult result = new FirstVideosMcaResult(mca.getVideoId(),mca.getBsyUrl(),FirstVideosMcaResult.STATE_WAIT_APPLY,mca.getLocalPath(),1,new Date());
//            firstVideosMcaResultMapper.insert(result);
//            resultList.add(result);
            //写入子表
            for(String cmd:cmdList){
                Integer speed = CommonUtil.getSpeed(cmd);
                String localPath = CommonUtil.getSpeedLocalPath(cmd);
                FirstVideosMcaResult result = new FirstVideosMcaResult(mca.getVideoId(),null,FirstVideosMcaResult.STATE_WAIT_UPLOAD,localPath,speed,new Date());
                firstVideosMcaResultMapper.insert(result);
                resultList.add(result);
            }
        }
        return resultList;
    }

    @Override
    public List<FirstVideosMca> getDownloadCompleteList() {
        QueryWrapper<FirstVideosMca> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state",1);
        return firstVideosMcaMapper.selectList(queryWrapper);
    }

    @Override
    public List<FirstVideosMcaResult> getWaitUploadList(){
        QueryWrapper<FirstVideosMcaResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state",FirstVideosMcaResult.STATE_WAIT_UPLOAD);
        return firstVideosMcaResultMapper.selectList(queryWrapper);
    }

    @Override
    public List<FirstVideosMcaResult> getWaitApplyList(){
        QueryWrapper<FirstVideosMcaResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state",FirstVideosMcaResult.STATE_WAIT_APPLY);
        return firstVideosMcaResultMapper.selectList(queryWrapper);
    }

    @Override
    public void doUploadAndUpdateResult(FirstVideosMcaResult result){
        BSCloudUtil.uploadFile(result.getLocalPath());
        String fileName = DownloadURLFile.getFileNameFromUrl(result.getLocalPath());
        result.setBsyUrl(bsPrefix+fileName);
        result.setUpdateAt(new Date());
        result.setState(FirstVideosMcaResult.STATE_WAIT_APPLY);
        if(firstVideosMcaResultMapper.updateById(result)>0){
            //删除本地视频
//            deleteVideo(result);
        }
    }

    @Override
    public void doResultSave(String param){
        //获取taskID 万一以后有用
//        JSONObject jsonObject = JSON.parseObject(param);
//        String taskId = String.valueOf(jsonObject.get("taskId"));
        QueryResultResponse response = JSON.parseObject(param,QueryResultResponse.class);
        String status = response.getStatus();
        if ("FINISHED".equals(status)) {
            // 根据视频url更新对应表
            String source = response.getSource();
            FirstVideosMcaResult result = new FirstVideosMcaResult();
            result.setState(FirstVideosMcaResult.STATE_APPLY_COMPLETE);
            result.setBsyUrl(source);
            result.setResult(param);
            if(firstVideosMapper.updateMCAResultBySource(result)>0){
                // 如果不存在则存入mongodb
//                QueryWrapper<FirstVideosMcaResult> queryWrapper = new QueryWrapper<>();
//                queryWrapper.eq("bsy_url",source);
//                result = firstVideosMcaResultMapper.selectOne(queryWrapper);
//                deleteVideo(result);
//                VideoMcaResult exist = videoMcaResultDao.findByVideoId(result.getVideoId());
//                if(exist==null){
//                    VideoMcaResult videoMcaResult = MCAUtil.parsingMongoResult(param);
//                    videoMcaResult.setVideoId(result.getVideoId());
//                    videoMcaResultDao.saveVideoMcaResult(videoMcaResult);
//                }
                labelUpLoad(null,source, param, 1);  //调用视频标签上报接口
            }
        }
    }

    /**
     * 调用视频标签上报接口
     * @param bsyUrl 视频地址
     * @param jsonResult 解析结果json
     * @param type 类型：1--百度云AI，2--算法解析
     */
    public void labelUpLoad(Integer videoId, String bsyUrl, String jsonResult, int type) {
        log.info("调用视频标签上报接口,videoId:{},bsyUrl:{}", videoId, bsyUrl);
        VideoLabelDto dto = new VideoLabelDto();
        if(videoId == null) {
            videoId = firstVideosMapper.getMcaVideoId(bsyUrl);  //根据视频地址查询 视频id
        }
        dto.setVideoId(videoId);
        dto.setResultType(1);
        dto.setResult("标签解析完成");
        List<LabelDto> tags = new ArrayList<>();  //标签集合
        if(type == 1) {
            //百度云AI
            tags = this.parseLabelJson(jsonResult, 5);
            dto.setSource("BAIDUAI");
        } else {
            //算法
            tags = this.parseAlgorithmLabelJson(jsonResult, 5);
            dto.setSource("ALGORITHM");
        }
        dto.setTags(tags);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(JSON.toJSONString(dto), headers);
        String result = null;
        try {
            result = restTemplate.postForObject(contentLabelUrl, request, String.class);
        } catch (Exception e) {
            log.error("上报视频标签异常", e.getMessage());
            log.error("上报视频标签error,videoId:{},bsyUrl:{},jsonResult:{}", videoId, bsyUrl,jsonResult);
        }
        LabelUpLoadLog labelUpLoadLog = new LabelUpLoadLog(videoId, bsyUrl, type, JSON.toJSONString(dto), result);
        firstVideosMapper.insertLabelUploadLog(labelUpLoadLog);
    }

    /**
     * 获取视频标签
     * @param result
     * @return
     */
    private String getLabel(String result) {
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONArray resultsJson = jsonObject.getJSONArray("results");
        List<String> labelList = new ArrayList<>();
        for(int i=0;i<resultsJson.size();i++) {
            JSONArray resultJson = resultsJson.getJSONObject(i).getJSONArray("result");
            for(int j=0;j<resultJson.size();j++) {
                String label = resultJson.getJSONObject(j).getString("attribute");
                if(!StringUtils.isEmpty(label)) {
                    labelList.add(label);
                }
            }
        }
        return String.join(",", labelList);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void doApplyAction(FirstVideosMcaResult result){
        MCAUtil.analyzeMedia(result.getBsyUrl());
        //更新表状态
        result.setState(FirstVideosMcaResult.STATE_APPLYING);
        result.setUpdateAt(new Date());
        firstVideosMcaResultMapper.updateById(result);
        //删除本地视频
        deleteVideo(result);
    }

    /**
     * 删除视频
     * @param result
     */
    private void deleteVideo(FirstVideosMcaResult result){
        //删除本地视频
        String rmShell = "rm -rf "+result.getLocalPath();
        log.info("删除倍速文件:"+rmShell);
        FileUtils.execCmd(rmShell);
        //删除原视频
        rmShell = "rm -rf "+CommonUtil.speed2LocalPath(result.getLocalPath());
        log.info("删除原文件:"+rmShell);
        FileUtils.execCmd(rmShell);
    }

    @Override
    public List<FirstVideosMcaResult> getMCAResult(Map<String, Object> paramMap) {
        return firstVideosMapper.getMCAResult(paramMap);
    }

    /**
     * 获取算法AI标签解析结果
     * @param videoIds 视频id，多个则逗号分隔
     * @return
     */
    public List<VideoLabelDto> getAlgorithmLabelResult(List<Integer> videoIds) {
        List<VideoPaddleTag> completeVideos = findPaddleTagByVideoId(videoIds);
        List<Integer> completeVideoIds = completeVideos.stream().map(r->r.getVideo_id()).collect(Collectors.toList());  //已经解析过的视频id;
        videoIds.removeAll(completeVideoIds);  //解析中的视频

        //获取解析完成的标签信息
        List<VideoLabelDto> videoLabelList = new ArrayList<>();
        for(VideoPaddleTag videoPaddleTag:completeVideos) {
            VideoLabelDto videoLabelDto = new VideoLabelDto();
            videoLabelDto.setVideoId(videoPaddleTag.getVideo_id());
            videoLabelDto.setResultType(1);
            videoLabelDto.setSource("ALGORITHM");
            videoLabelDto.setResult("标签解析完成");
            videoLabelDto.setTags(this.parseAlgorithmLabelJson(videoPaddleTag.getFull_label(), 0));
            videoLabelList.add(videoLabelDto);
        }

        //解析中的标签信息
        for(Integer videoId:videoIds) {
            VideoLabelDto videoLabelDto = new VideoLabelDto();
            videoLabelDto.setVideoId(videoId);
            videoLabelDto.setResultType(0);
            videoLabelDto.setSource("ALGORITHM");
            videoLabelDto.setResult("标签解析中");
            videoLabelList.add(videoLabelDto);
        }
        return videoLabelList;
    }

    /**
     * 获取百度云AI标签解析结果,如果视频没解析则开启解析任务
     * @param blParams
     * @return
     */
    public List<VideoLabelDto> getBaiduLabelResult(List<BaiduLabelParams> blParams) {
        if(blParams == null || blParams.isEmpty()) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("videoList", blParams);

        List<FirstVideosMcaResult> completeVideos = this.firstVideosMapper.findVideoMcaResult(params); //根据视频id和视频url查询出已经解析的视频信息
        List<String> completeVideoIds = completeVideos.stream().map(r->String.valueOf(r.getVideoId())).collect(Collectors.toList());  //已经解析过的视频id
//        videoList.removeAll(completeVideoIds); //还没使用百度ai解析的视频IE计划
        List<BaiduLabelParams> noCompleteVideos = blParams.stream().filter(r->!completeVideoIds.contains(String.valueOf(r.getVideoId()))).collect(Collectors.toList());

        //获取解析完成的标签信息
        List<VideoLabelDto> videoLabelList = new ArrayList<>();
        for(FirstVideosMcaResult mcaResult:completeVideos) {
            VideoLabelDto videoLabelDto = new VideoLabelDto();
            videoLabelDto.setVideoId(mcaResult.getVideoId());
            videoLabelDto.setResultType(1);
            videoLabelDto.setSource("BAIDUAI");
            videoLabelDto.setResult("标签解析完成");
            videoLabelDto.setTags(this.parseLabelJson(mcaResult.getResult(), 0));
            videoLabelList.add(videoLabelDto);
        }

        //解析中的标签信息
        for(BaiduLabelParams video:noCompleteVideos) {
            VideoLabelDto videoLabelDto = new VideoLabelDto();
            videoLabelDto.setVideoId(video.getVideoId());
            videoLabelDto.setResultType(0);
            videoLabelDto.setSource("BAIDUAI");
            videoLabelDto.setResult("标签解析中");
            videoLabelList.add(videoLabelDto);
        }
        if(noCompleteVideos != null && !noCompleteVideos.isEmpty()) {
            //异步解析视频
            doMcaApplyAction(noCompleteVideos);
        }
        return videoLabelList;
    }

    /**
     * 解析百度返回的json，并返回视频的人物和图谱标签类型
     * @param labelJsonStr
     * @param index 获取置信值最高的前index个标签。如果为0，则获取全部
     * @return
     */
    public List<LabelDto> parseLabelJson(String labelJsonStr, int index) {
        List<LabelDto> tags = new ArrayList<>();  //标签集合
        try {
            JSONObject labelJson = JSONObject.parseObject(labelJsonStr);
            JSONArray labelArray = labelJson.getJSONArray("results");
            for(int i=0;i < labelArray.size();i++) {
                JSONObject labelType = labelArray.getJSONObject(i);
                String type = labelType.getString("type");
                JSONArray result = labelType.getJSONArray("result");
                //只返回人物、场景、知识图谱标签
                if(LabelTypeEnum.figure.getCode().equals(type) || LabelTypeEnum.scenario.getCode().equals(type) || LabelTypeEnum.knowledgeGraph.getCode().equals(type) ) {
                    for(int j=0;j<result.size();j++) {
                        JSONObject oneLabel = result.getJSONObject(j);
                        LabelDto labelDto = new LabelDto();
                        labelDto.setValue(oneLabel.getString("attribute")); //标签名
                        labelDto.setScore(oneLabel.getDouble("confidence"));  //置信值
                        labelDto.setType(LabelTypeEnum.getLabelType(type));
                        tags.add(labelDto);
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析百度ai视频标签返回结果json异常", e);
        }

        tags = tags.stream().sorted(Comparator.comparing(LabelDto::getScore).reversed()).collect(Collectors.toList());
        if(index > 0) {
            //需要截取标签数组
            tags = tags.subList(0, (tags.size() > index ? index : tags.size()));
        }
        return tags;
    }

    /**
     * 上报历史百度云解析的视频标签
     */
    public void upLoadHistoryVideoLabel(Integer startRow) {
        int pageSize = 1000;  //每页记录数

        Map<String, Object> params = new HashMap<>();
        params.put("startRow", startRow);
        params.put("pageSize", pageSize);
        List<FirstVideosMcaResult> mcaResults = firstVideosMapper.findVideoMcaHisResult(params);

        while(mcaResults != null && !mcaResults.isEmpty()) {
            for(FirstVideosMcaResult video : mcaResults) {
                labelUpLoad(video.getVideoId(), null, video.getResult(), 1);  //调用视频标签上报接口
            }

            log.info("同步历史百度云解析的视频标签,startRow:{}, pageSize:{}", startRow, pageSize);
            startRow+=pageSize;
            params.put("startRow", startRow);
            mcaResults = firstVideosMapper.findVideoMcaHisResult(params);
        }
    }

    /**
     * 上报历史算法解析的视频标签
     */
    public void uploadHistoryAlgorithmLabel(Integer startRow) {
        int pageSize = 100;  //每页记录数
        List<VideoPaddleTag> paddleTags = this.findVideoPaddleTag(startRow, pageSize);

        while(paddleTags != null && !paddleTags.isEmpty()) {

            for(VideoPaddleTag video : paddleTags) {
                labelUpLoad(video.getVideo_id(), null, video.getFull_label(), 2);  //调用视频标签上报接口
            }

            log.info("同步历史算法解析的视频标签,startRow:{}, pageSize:{},total:{}", startRow, pageSize);
            startRow+=pageSize;
            paddleTags = this.findVideoPaddleTag(startRow, pageSize);
        }

    }

    /**
     * 解析算法视频标签的json，并返回视频的人物和图谱标签类型
     * @param labelJsonStr
     * @param index 获取置信值最高的前index个标签。如果为0，则获取全部
     * @return
     */
    private List<LabelDto> parseAlgorithmLabelJson(String labelJsonStr, int index) {
        List<LabelDto> tags = new ArrayList<>();  //标签集合
        try {
            JSONArray labelArray = JSONObject.parseArray(labelJsonStr);
            for(int i=0;i<labelArray.size();i++) {
                JSONObject oneLabel = labelArray.getJSONObject(i);
                LabelDto labelDto = new LabelDto();
                labelDto.setType(LabelTypeEnum.keyword.getType());
                labelDto.setScore(oneLabel.getDouble("probability") * 100);
                labelDto.setValue(oneLabel.getString("class_name"));
                tags.add(labelDto);
            }
        } catch (Exception e) {
            log.error("解析算法标签返回结果json异常", e);
        }

        tags = tags.stream().sorted(Comparator.comparing(LabelDto::getScore).reversed()).collect(Collectors.toList());
        if(index > 0) {
            //需要截取标签数组
            tags = tags.subList(0, (tags.size() > index ? index : tags.size()));
        }
        return tags;
    }

    /**
     * 分页从mongodb查询算法标签解析表
     * @param startRow
     * @param pageSize
     * @return
     */
    private List<VideoPaddleTag> findVideoPaddleTag(Integer startRow, Integer pageSize) {
        Query query = new Query();
        query.skip(startRow);
        query.limit(pageSize);
        List<LinkedHashMap> list = mongoTemplate.find(query, LinkedHashMap.class, "video_paddle_tag");

        List<VideoPaddleTag> result = new ArrayList<>();
        for(LinkedHashMap map : list) {
            VideoPaddleTag tag = new VideoPaddleTag();
            tag.setUrl(String.valueOf(map.get("url")));
            tag.setVideo_id(Integer.parseInt(String.valueOf(map.get("video_id"))));
            tag.setFull_label(JSONObject.toJSONString(map.get("full_label")));
            result.add(tag);
        }
        return result;
    }

    /**
     * 根据视频id查询已解析的视频标签
     * @param videoIds
     * @return
     */
    private List<VideoPaddleTag> findPaddleTagByVideoId(List<Integer> videoIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("video_id").in(videoIds));
        List<LinkedHashMap> list = mongoTemplate.find(query, LinkedHashMap.class, "video_paddle_tag");
        List<VideoPaddleTag> result = new ArrayList<>();
        for(LinkedHashMap map : list) {
            VideoPaddleTag tag = new VideoPaddleTag();
            tag.setUrl(String.valueOf(map.get("url")));
            tag.setVideo_id(Integer.parseInt(String.valueOf(map.get("video_id"))));
            tag.setFull_label(JSONObject.toJSONString(map.get("full_label")));
            result.add(tag);
        }
        return result;
    }
}

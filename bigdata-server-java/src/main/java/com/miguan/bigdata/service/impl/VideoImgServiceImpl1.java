package com.miguan.bigdata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miguan.bigdata.common.constant.BigDataConstants;
import com.miguan.bigdata.common.util.NumCalculationUtil;
import com.miguan.bigdata.common.util.RobotUtil;
import com.miguan.bigdata.common.util.SimHash;
import com.miguan.bigdata.dto.SimilarParamDto;
import com.miguan.bigdata.dto.SimilarVideoDto;
import com.miguan.bigdata.mapper.VideoImgMapper;
import com.miguan.bigdata.service.VideoImgService;
import com.miguan.bigdata.vo.FirstVideosVo;
import com.miguan.bigdata.vo.RepeatVideoLogVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 视频标题和背景图片相似度查询Service
 */
@Slf4j
@Service("VideoImgServiceImpl1")
public class VideoImgServiceImpl1 implements VideoImgService {
    @Resource
    private VideoImgMapper videoImgMapper;
    @Value("${python-img.get-img-vector}")
    private String imgVectorUrl;
    @Resource
    private RestTemplate restTemplate;
    @Value("${spring.es.host}")
    public String host;
    @Value("${spring.es.port}")
    public String port;
    @Value("${spring.es.host1}")
    public String host1;
    @Value("${spring.es.port1}")
    public String port1;


    /**
     * 根据视频标题、视频背景图片相似度查询
     *
     * @param paramDto
     * @return
     */
    public List<SimilarVideoDto> findSimVideoList(SimilarParamDto paramDto) {
        List<SimilarVideoDto> similarList = new ArrayList<>();
        if (StringUtils.isNotBlank(paramDto.getTitle())) {
            //根据标题相似度查询
            similarList = findSimTitleVideoList(paramDto.getTitle(), paramDto.getTitleThreshold());
        }
        if (StringUtils.isNotBlank(paramDto.getImgUrl())) {
            //根据图像向量相似度查询
            similarList = findSimImgVideoList(similarList, paramDto.getImgUrl(), paramDto.getImgThreshold());
        }
        return similarList;
    }

    /**
     * 根据标题相似度查询
     *
     * @param title          标题
     * @param titleThreshold 标题相似度阀值
     * @return
     */
    private List<SimilarVideoDto> findSimTitleVideoList(String title, Double titleThreshold) {
        String json = this.findVideoByTitle(title);  //在ES中根据标题查询
        List<SimilarVideoDto> list = parseJsonToDto(json, BigDataConstants.TITLE_SIM_TYPE);  //把ES查询出来的记过 转成List
        List<SimilarVideoDto> similarList = this.filterResult(list, BigDataConstants.TITLE_SIM_TYPE, title, titleThreshold, null);
        return similarList;
    }

    /**
     * 根据图像向量相似度查询
     *
     * @param titleSimList 先经过title相似度匹配后结果
     * @param imgUrl       图片url
     * @param imgThreshold 图像相似度阀值
     * @return
     */
    private List<SimilarVideoDto> findSimImgVideoList(List<SimilarVideoDto> titleSimList, String imgUrl, Double imgThreshold) {
        String imgVector = getImgVector(imgUrl);  //调用生成图片向量接口
        String videoIds = null;
        if (titleSimList != null && !titleSimList.isEmpty()) {
            List<String> videoIdList = titleSimList.stream().map(r -> String.valueOf(r.getVideoId())).collect(Collectors.toList());
            videoIds = "[" + String.join(",", videoIdList) + "]";
        }

        String json = this.findVideoByImgVector(videoIds, imgVector);  //根据用图像量数据，在es中用余弦相似度函数查询
        List<SimilarVideoDto> list = parseJsonToDto(json, BigDataConstants.IMG_SIM_TYPE);  //把ES查询出来的记过 转成List
        List<SimilarVideoDto> similarList = this.filterResult(list, BigDataConstants.IMG_SIM_TYPE, null, imgThreshold, null);

        if(StringUtils.isNotBlank(videoIds)) {
            //把title的相似度值重新设置进去
            Map<Integer, Double> titleScoreMap = titleSimList.stream().collect(Collectors.toMap(SimilarVideoDto::getVideoId, SimilarVideoDto::getTitleSimScore));
            for(SimilarVideoDto sim:similarList) {
                sim.setTitleSimScore(titleScoreMap.get(sim.getVideoId()));
            }
            similarList = similarList.stream()
                    .sorted(Comparator.comparing(SimilarVideoDto::getTitleSimScore).reversed().thenComparing(Comparator.comparing(SimilarVideoDto::getImgSimScore)))
                    .collect(Collectors.toList());
        }
        return similarList;
    }


    /**
     * 生成历史视频的背景图片的向量特征
     * @param startRow 开始记录数
     */
    public void createHistoryImageVector(Integer startRow, String videoIds) {
        int pageSize = 1000;  //每页记录数
        Map<String, Object> param = new HashMap<>();
        param.put("startRow", startRow);
        param.put("pageSize", pageSize);
        param.put("videoIds", videoIds);
        List<FirstVideosVo> videoList = videoImgMapper.fingVideoByPage(param);
        int totle = 0;
        while (videoList != null && !videoList.isEmpty()) {
            for (FirstVideosVo videosVo : videoList) {
                try {
                    String imgVector = this.getImgVector(videosVo.getImgUrl()); //调用python接口，生成图片向量
                    if (this.isCanSave(imgVector)) {
                        saveEsImgVector(videosVo, imgVector);  //ES新增或更新图像特征向量
                        log.info("总记录数：{}", totle++);
                    }
                } catch (Exception e) {
                    log.error("生成历史视频的背景图片的向量特征异常,videoId:{}", videosVo.getVideoId());
                }
            }
            log.info("同步历史视频的背景图片的向量特征,startRow:{}, pageSize:{}", startRow, pageSize);
            startRow += pageSize;
            param.put("startRow", startRow);
            param.put("videoIds", videoIds);
            videoList = videoImgMapper.fingVideoByPage(param);
        }
    }

    /**
     * 查询出当前视频库中标题和封面图片有重复可能性的视频
     * @param startRow
     * @param titleThreshold
     * @param imgThreshold
     */
    public void repeatHistoryVideo(Integer type, Integer startRow, String videoIds, Double titleThreshold, Double imgThreshold) {
        int pageSize = 300;  //每页记录数
        Map<String, Object> param = new HashMap<>();
        param.put("startRow", startRow);
        param.put("pageSize", pageSize);
        param.put("videoIds", videoIds);
        List<FirstVideosVo> videoList = videoImgMapper.fingVideoByPage(param);

        int totle = 0;
        List<RepeatVideoLogVo> repeatVideoLogList = new ArrayList<>();
        while (videoList != null && !videoList.isEmpty()) {
            for(FirstVideosVo videosVo : videoList) {
                try {
                    SimilarParamDto paramDto = new SimilarParamDto();
                    if(type == 0) {
                        paramDto.setTitle(videosVo.getTitle());
                        paramDto.setImgUrl(videosVo.getImgUrl());
                    } else if(type == 1) {
                        paramDto.setImgUrl(videosVo.getImgUrl());
                    } else if(type == 2) {
                        paramDto.setTitle(videosVo.getTitle());
                    }
                    paramDto.setTitleThreshold(titleThreshold);
                    paramDto.setImgThreshold(imgThreshold);
                    List<SimilarVideoDto> similarList = this.findSimVideoList(paramDto);  //相似的视频

                    for(SimilarVideoDto similarVideo : similarList) {
                        if(videosVo.getVideoId().intValue() != similarVideo.getVideoId().intValue()) {
                            double titleSimScore = (similarVideo.getTitleSimScore() == null ? 0.0 : similarVideo.getTitleSimScore());
                            double imgSimScore = (similarVideo.getImgSimScore() == null ? 0.0 : similarVideo.getImgSimScore());
                            titleSimScore = NumCalculationUtil.roundHalfUpDouble(8,  titleSimScore);
                            imgSimScore = NumCalculationUtil.roundHalfUpDouble(8,  imgSimScore);
                            RepeatVideoLogVo repeatVideoLogVo = new RepeatVideoLogVo(videosVo.getVideoId(), similarVideo.getVideoId(), titleSimScore, imgSimScore, type);
                            repeatVideoLogList.add(repeatVideoLogVo);
//                            if(titleSimScore == null || imgSimScore == null || videosVo.getVideoId() == null || similarVideo.getVideoId() == null) {
//                                log.info("1212");
//                            }
                        }
                    }
                    log.info("查询出当前视频库中标题数：{},type:{}", totle++, type);
                } catch (Exception e) {
                    log.error("查询出当前视频库中标题和封面图片有重复可能性的视频异常,videoId:{}", videosVo.getVideoId());
                    RobotUtil.talkText("查询出当前视频库中标题和封面图片有重复可能性的视频异常，totle:"+totle,"SECd8f655dd754d04d14530b28715c1624270ed090da51f373c186c1cc7bb082f4b","d9336ff9e14555359a8f07a15f16b3e813b741341bd82f9111bd12deb9bf24b3");  //调用钉钉机器人，往群里发送消息
                }
            }
            if(!repeatVideoLogList.isEmpty()) {
                videoImgMapper.batchInsertRepeatVideoLog(repeatVideoLogList);
            }

            log.info("查询出当前视频库中标题和封面图片有重复可能性的视频,startRow:{}, pageSize:{}，type：{}", startRow, pageSize, type);
            repeatVideoLogList.clear();
            startRow += pageSize;
            param.put("startRow", startRow);
            param.put("videoIds", videoIds);
            videoList = videoImgMapper.fingVideoByPage(param);
        }
    }

    private boolean isCanSave(String imgVector) {
        try {
            if(StringUtil.isBlank(imgVector)) {
                return false;
            }
            imgVector = imgVector.replace("[", "").replace("]", "");
            String[] array = imgVector.split(",");
            long total = 0;
            for(int i=0;i<array.length;i++) {
                total+=Long.parseLong(array[i]);
            }
            if(total == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("判断是否插入es错误,imgVector:{}", imgVector);
            return false;
        }
    }

    /**
     * 调用生成图片向量接口
     *
     * @param imgUrl 图像的在线url
     * @return
     */
    private String getImgVector(String imgUrl) {
        try {
            String url = imgVectorUrl + "?imgUrl=" + imgUrl;
            return restTemplate.postForObject(url, null, String.class);
        } catch (Exception e) {
            log.error("调用生成图片向量接口异常", e);
            return "";
        }
    }


    /**
     * 根据阀值取前maxSize记录（如果是标题相似度查询，则在根据simHash算法计算文本相似度）
     *
     * @param list      相似度匹配结果
     * @param type      类型,1--标题相似度查询，2--图像相似度查询
     * @param title     需要比对的标题（类型为1的时候，才需要传入）
     * @param threshold 阀值
     * @param maxSize   返回最大记录数
     */
    private List<SimilarVideoDto> filterResult(List<SimilarVideoDto> list, int type, String title, Double threshold, Integer maxSize) {
        List<SimilarVideoDto> similarList = new ArrayList<>();
        maxSize = (maxSize == null ? 20 : maxSize);
        SimHash paramTitle = (type == BigDataConstants.TITLE_SIM_TYPE ? new SimHash(title) : null);

        if (type == BigDataConstants.TITLE_SIM_TYPE) {
            //如果是标题相似度查询,则计算标题的相似度
            for (SimilarVideoDto video : list) {
                SimHash dataTitle = new SimHash(video.getTitle());
                video.setTitleSimScore(SimHash.getSemblance(paramTitle, dataTitle));  //es查询出来的评分用simHash修改成相似度
            }
            list = list.stream().sorted(Comparator.comparing(SimilarVideoDto::getTitleSimScore).reversed()).collect(Collectors.toList());
        }
        //根据相似度阀值进行过滤
        for (SimilarVideoDto video : list) {
            if (similarList.size() >= maxSize) {
                //超过最大记录数,结束遍历
                break;
            }
            Double similarScore = (type == BigDataConstants.TITLE_SIM_TYPE ? video.getTitleSimScore() : video.getImgSimScore());
            if (threshold != null && similarScore < threshold) {
                //数据的相似度 小于 阀值的时候，则结束遍历
                break;
            }
            similarList.add(video);
        }
        return similarList;
    }


    /**
     * 把ES查询出来的记过 转成List
     *
     * @param json
     * @param type 类型,1--标题相似度查询，2--图像相似度查询
     * @return
     */
    private List<SimilarVideoDto> parseJsonToDto(String json, int type) {
        List<SimilarVideoDto> list = new ArrayList<>();
        if (StringUtil.isBlank(json)) {
            return list;
        }

        JSONObject topJson = JSONObject.parseObject(json);
        JSONArray jsonArray = topJson.getJSONObject("hits").getJSONArray("hits");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject oneData = jsonArray.getJSONObject(i);
            double score = oneData.getDouble("_score");  //相似度

            JSONObject video = oneData.getJSONObject("_source");
            SimilarVideoDto videoDto = new SimilarVideoDto();
            if (type == BigDataConstants.TITLE_SIM_TYPE) {
                videoDto.setTitleSimScore(score);
            } else {
                videoDto.setImgSimScore(score - 1);
            }
            videoDto.setVideoId(video.getInteger("video_id"));
            videoDto.setTitle(video.getString("title"));
            videoDto.setBsyImgUrl(video.getString("bsy_img_url"));
            list.add(videoDto);
        }
        return list;
    }

    /**
     * ES新增或更新图像特征向量
     *
     * @param videosVo  视频信息
     * @param imgVector 图像特征向量
     */
    private void saveEsImgVector(FirstVideosVo videosVo, String imgVector) {
        StringBuffer queryBody = new StringBuffer();
        Integer videoId = videosVo.getVideoId();
        queryBody.append("{");
        queryBody.append("  \"video_id\" : %d,");
        queryBody.append("  \"title\": \"%s\",");
        queryBody.append("  \"bsy_img_url\": \"%s\",");
        queryBody.append("  \"img_vector\" : %s");
        queryBody.append("}");
        String url = String.format("http://%s:%s/img_vector/_doc/%d", host1, port1, videoId);
        String json = String.format(queryBody.toString(), videoId, videosVo.getTitle(), videosVo.getImgUrl(), imgVector);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        restTemplate.exchange(url.toLowerCase(), HttpMethod.PUT, request, String.class);
    }

    /**
     * 根据标题相似度查询
     *
     * @param title
     * @return
     */
    private String findVideoByTitle(String title) {
        StringBuffer queryBody = new StringBuffer();
        queryBody.append("{");
        queryBody.append("    \"from\": 0,");
        queryBody.append("    \"size\": 200,");
        queryBody.append("    \"query\":{");
        queryBody.append("        \"match\":{");
        queryBody.append("            \"title\":\"" + title + "\"");
        queryBody.append("        }");
        queryBody.append("    }");
        queryBody.append("}");
        String url = String.format("http://%s:%s/img_vector/_search", host1, port1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(queryBody.toString(), headers);
        return restTemplate.postForObject(url, request, String.class);
    }

    /**
     * 根据标题相似度查询
     *
     */
    private String pageImgVector(int from, int size) {
        StringBuffer queryBody = new StringBuffer();
        queryBody.append("{");
        queryBody.append("    \"from\": " + from +",");
        queryBody.append("    \"size\": " + size +",");
        queryBody.append("    \"query\":{");
        queryBody.append("        \"match_all\":{}");
        queryBody.append("    }");
        queryBody.append("}");
        String url = String.format("http://%s:%s/img_vector/_search", host, port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(queryBody.toString(), headers);
        return restTemplate.postForObject(url, request, String.class);
    }

    /**
     * ES新增或更新图像特征向量
     *
     */
    private void saveEsImgVector1(String json) {
        StringBuffer queryBody = new StringBuffer();
        JSONObject jsonObject = JSONObject.parseObject(json);
        Integer videoId = jsonObject.getInteger("video_id");
        queryBody.append("{");
        queryBody.append("  \"video_id\" : %d,");
        queryBody.append("  \"title\": \"%s\",");
        queryBody.append("  \"bsy_img_url\": \"%s\",");
        queryBody.append("  \"img_vector\" : %s");
        queryBody.append("}");
        String url = String.format("http://%s:%s/img_vector/_doc/%d", host1, port1, videoId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        restTemplate.exchange(url.toLowerCase(), HttpMethod.PUT, request, String.class);
    }

    public void syncImgVector(Integer from, Integer size) {
        from = (from == null ? 0 : from);
        size = (size == null ? 3000 : size);

        String jsonArrayStr = pageImgVector(from, size);
        JSONArray list = JSONObject.parseObject(jsonArrayStr).getJSONObject("hits").getJSONArray("hits");
        int totle = 0;
        while(list.size() > 0) {
            log.info("同步图片向量到新es库，from:{}, size:{}", from, size);
            try {
                for(int i=0;i<list.size();i++) {
                    String imgJson = list.getJSONObject(i).getString("_source");
                    try {
                        this.saveEsImgVector1(imgJson);
                        log.info("同步图片向量到新es库记录数：{}", totle++);
                    } catch (Exception e) {
                        log.error("保存新图片向量异常，from:{}, totle:{}", from, totle);
                        RobotUtil.talkText("保存新图片向量异常，from:"+from+"totle:"+totle,"SECd8f655dd754d04d14530b28715c1624270ed090da51f373c186c1cc7bb082f4b","d9336ff9e14555359a8f07a15f16b3e813b741341bd82f9111bd12deb9bf24b3");  //调用钉钉机器人，往群里发送消息
                    }
                }

                from+=size;
                jsonArrayStr = pageImgVector(from, size);
                list = JSONObject.parseObject(jsonArrayStr).getJSONObject("hits").getJSONArray("hits");
            } catch (Exception e) {
                log.error("查询推荐库图片向量异常，from:{}, size:{}", from, size);
                RobotUtil.talkText("查询推荐库图片向量异常，from:"+from,"SECd8f655dd754d04d14530b28715c1624270ed090da51f373c186c1cc7bb082f4b","d9336ff9e14555359a8f07a15f16b3e813b741341bd82f9111bd12deb9bf24b3");  //调用钉钉机器人，往群里发送消息
            }
        }

    }

    /**
     * 根据用图像量数据，在es中用余弦相似度函数查询
     *
     * @param videoIds  视频id列表
     * @param imgVector 图像向量
     * @return
     */
    private String findVideoByImgVector(String videoIds, String imgVector) {
        StringBuffer queryBody = new StringBuffer();
        queryBody.append("{");
        queryBody.append("  \"from\": 0,");
        queryBody.append("  \"size\": 200,");
        queryBody.append("  \"query\": {");
        queryBody.append("    \"script_score\": {");
        queryBody.append("      \"query\": {");
        if (StringUtils.isNotBlank(videoIds)) {
            //标题不为空
            queryBody.append("        \"terms\":{");
            queryBody.append("            \"video_id\":" + videoIds);
            queryBody.append("        }");
        } else {
            //标题为空
            queryBody.append("        \"match_all\": {}");
        }
        queryBody.append("      },");
        queryBody.append("      \"script\": {");
        queryBody.append("        \"source\": \"cosineSimilarity(params.queryVector, doc['img_vector'])+1.0\",");
        queryBody.append("        \"params\": {");
        queryBody.append("          \"queryVector\": %s");
        queryBody.append("        }");
        queryBody.append("      }");
        queryBody.append("    }");
        queryBody.append("  }");
        queryBody.append("}");
        String url = String.format("http://%s:%s/img_vector/_search", host1, port1);
        String json = String.format(queryBody.toString(), imgVector);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(json, headers);
        return restTemplate.postForObject(url, request, String.class);
    }
}

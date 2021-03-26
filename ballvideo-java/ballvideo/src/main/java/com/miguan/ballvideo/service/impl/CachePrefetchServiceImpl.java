package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.HttpUtils;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.entity.BsyLog;
import com.miguan.ballvideo.mapper.FirstVideosMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.BsyLogService;
import com.miguan.ballvideo.service.CachePrefetchService;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("cachePrefetchService")
public class CachePrefetchServiceImpl implements CachePrefetchService {

    @Resource
    private FirstVideosMapper firstVideosMapper;

    @Resource
    private BsyLogService bsyLogService;

    //请求地址
    private static String URL = "https://cdn.api.baishan.com/v2/cache/prefetch";
    //鉴权参数
    private static String TOKEN = "92815fee3499d1cf94d971f88e218f95";

  /**
   * 根据权重把有效视频地址调用第三方接口进行预热
   * 白云山文件预热接口文档：https://portal.baishancloud.com/track/document/api/1/1185
   * @return */
  @Override
  public ResultMap videoCachePrefetch() {
        String active = Global.getValue("app_environment");
        if(!"prod".equals(active)){
            return ResultMap.error("每日预热数量有限制，非正式环境不进行预热");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("queryNum", Global.getInt("video_cache_prefetch_num"));
        List<Videos161Vo> urls_db = firstVideosMapper.findBsyUrlList(param);
        int max = Global.getInt("video_cache_prefetch_max");
        if (CollectionUtils.isNotEmpty(urls_db)) {
            List<List<Videos161Vo>> urls_batch = Lists.partition(urls_db,max);
            ResultMap result = null;
            for(List<Videos161Vo> videos:urls_batch){
                result = doPrefetchWork(videos);
                if (result.getCode()!=200)return result;
            }
            log.info(DateUtil.dateStr4(new Date()) + "========视频预热成功=======");
            return result;
        }
        return ResultMap.error("没有合适的预热数据，请稍后再试");
    }

    //视频预热并保存日志
    private ResultMap doPrefetchWork(List<Videos161Vo> videos) {
        List<String> urls = videos.stream().map(t -> t.getBsyUrl()).collect(Collectors.toList());
        List<String> ids = videos.stream().map(t -> t.getId()+"").collect(Collectors.toList());
        Map<String, Object> params = new HashMap<>();
        params.put("token", TOKEN);
        params.put("urls", urls);
        String post = HttpUtils.postWithJson(URL, JSON.toJSONString(params));
        if (StringUtils.isNotEmpty(post)) {
            JSONObject jsonObject = JSONObject.parseObject(post);
            int code = jsonObject.getInteger("code");//code=0正常，code!=0请求失败
            if (code == 0) {
                String data = jsonObject.getString("data");
                BsyLog bsyLog = JSON.parseObject(data, BsyLog.class);
                bsyLog.setCode(code);
                bsyLog.setSendCount(urls.size());
                bsyLog.setIds(String.join(",",ids));
                bsyLogService.saveBsyLog(bsyLog);
                return ResultMap.success();
            }else{
                String message = jsonObject.getString("message");
                BsyLog bsyLog = new BsyLog();
                bsyLog.setCode(code);
                bsyLog.setSendCount(urls.size());
                bsyLog.setIds(String.join(",",ids));
                bsyLog.setMessage(message);
                bsyLogService.saveBsyLog(bsyLog);
                return ResultMap.error(message);
            }
        }
        return ResultMap.error("请求失败！请联系管理员！");
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void videoCachePrefetchQuartz() {
        RedisLock redisLock = new RedisLock(RedisKeyConstant.VIDEO_CACHE_PREFETCH, RedisKeyConstant.VIDEO_CACHE_PREFETCH_TIME);
        if (redisLock.lock()) {
            //视频预热
            ResultMap map = videoCachePrefetch();
            log.info("视频预热定时器启动结束："+JSON.toJSONString(map));
        }
    }

}

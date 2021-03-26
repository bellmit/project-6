package com.miguan.recommend.service.es.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.VideoInitDto;
import com.miguan.recommend.common.config.EsConfig;
import com.miguan.recommend.common.constants.RabbitMqConstants;
import com.miguan.recommend.common.es.EsDao;
import com.miguan.recommend.common.es.EsEntity;
import com.miguan.recommend.entity.es.HotspotVideos;
import com.miguan.recommend.mapper.FirstVideosMapper;
import com.miguan.recommend.service.es.EsVideoInitService;
import com.miguan.recommend.vo.RecVideosVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EsVideoInitServiceImpl implements EsVideoInitService {

    @Resource
    private FirstVideosMapper firstVideosMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private EsConfig esConfig;
    @Resource
    private EsDao esDao;

    @Override
    public void sendVideoTitleInitToMQ() {
        // 统计视频数量
        int count = firstVideosMapper.count();
        if (count == 0) {
            return;
        }
        log.debug("共计{}条视频标题需要初始化", count);
        int loop = 0;
        int size = 5000;
        while (true) {
            VideoInitDto dto = new VideoInitDto();
            dto.setType(VideoInitDto.es_video_title_init);
            dto.setSkip(loop);
            dto.setSize(size);
            rabbitTemplate.convertAndSend(RabbitMqConstants.HOTSPOST_INIT_EXCHANGE, RabbitMqConstants.HOTSPOST_INIT_RUTE_KEY, JSONObject.toJSONString(dto));
            if (loop >= count) {
                break;
            }
            loop = loop + size;
        }
    }

    @Override
    public void doVideoTitleInitToMQ(VideoInitDto initDto) {
        log.info("开始初始化视频标题：skip>>{}, size>>{}", initDto.getSkip(), initDto.getSize());
        List<RecVideosVo> recVideosVoList = firstVideosMapper.findInPage(initDto.getSkip(), initDto.getSize());
        this.batchVideoTitleInit(recVideosVoList);
    }

    @Override
    public void batchVideoTitleInitById(List<String> videoIds) {
        List<RecVideosVo> recVideosVoList = firstVideosMapper.findByIds(videoIds.stream().map(Integer::new).collect(Collectors.toList()));
        this.batchVideoTitleInit(recVideosVoList);
    }

    @Override
    public void batchVideoTitleInit(List<RecVideosVo> recVideosVoList) {
        if (CollectionUtils.isEmpty(recVideosVoList)) {
            return;
        }
        recVideosVoList.forEach(e -> {
            this.videoTitleInit(e);
        });
    }

    @Override
    public void videoTitleInit(RecVideosVo recVideosVo) {
        esDao.deleteByQuery(esConfig.getVideo_title(), new TermQueryBuilder("id", recVideosVo.getId().toString()));
        if (recVideosVo.getState() == 1) {
            log.debug("Es视频标题初始化 新增视频：{}", recVideosVo.getId());
            esDao.insertOrUpdateOne(esConfig.getVideo_title(), this.copyProptitys(recVideosVo));
        }
    }

    private EsEntity<HotspotVideos> copyProptitys(RecVideosVo recVideosVo) {
        EsEntity<HotspotVideos> esEntity = new EsEntity<>();
        esEntity.setId(recVideosVo.getId().toString());
        esEntity.setData(new HotspotVideos(recVideosVo.getId().toString(), recVideosVo.getTitle()));
        return esEntity;
    }
}

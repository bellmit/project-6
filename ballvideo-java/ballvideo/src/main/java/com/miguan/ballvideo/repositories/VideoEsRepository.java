package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.es.FirstVideoEsVo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Author shixh
 * @Date 2020/1/7
 **/
public interface VideoEsRepository extends ElasticsearchRepository<FirstVideoEsVo,Long> {

    List<FirstVideoEsVo> findByGatherIdOrderByTotalWeight(long gatherId);

    List<FirstVideoEsVo> findByGatherId(long gatherId);

    List<FirstVideoEsVo> findByCatIdAndIncentiveVideo(long gatherId,long incentiveVideo);

    List<FirstVideoEsVo> findByCreateDateLessThanEqual(long seconds);

    int countByGatherIdAndIncentiveVideo(Long gatherId, Long incentiveVideo);

    void deleteByGatherId(Long gatherId);
}

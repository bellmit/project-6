package com.miguan.recommend.service.recommend;

import com.miguan.recommend.bo.PublicInfo;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;

import com.miguan.recommend.bo.VideoQueryDto;

import java.util.List;

public interface VideoHotService<T> {


    public List<T> findAndFilterOfTheLastOnline(VideoQueryDto videoQueryDto, List<T> hasQuery);

    public List<T> findAndFilter(VideoQueryDto videoQueryDto, List<T> hasQuery);

    public List<T> findAndFilter(String uuid, List<Integer> includeCatid, Integer catid, Integer sensitive, int getNum, List<T> hasQuery);
}

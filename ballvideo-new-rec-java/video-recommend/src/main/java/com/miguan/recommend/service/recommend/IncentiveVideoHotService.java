package com.miguan.recommend.service.recommend;

import java.util.List;

public interface IncentiveVideoHotService<T> {

    public List<T> findAndFilter(String uuid, Integer sensitive, int getNum);

    public List<T> findAndFilter(String uuid, Integer catid, Integer sensitive, int getNum);

    public List<T> findAndFilter(String uuid, List<Integer> excludeCatids, Integer sensitive, int getNum);

    public List<T> findAndFilter(String uuid, Integer catid, List<Integer> excludeCatids, Integer sensitive, List<String> excludeSource, int getNum);
}

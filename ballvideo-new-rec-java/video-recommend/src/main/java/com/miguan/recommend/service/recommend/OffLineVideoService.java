package com.miguan.recommend.service.recommend;

import java.util.List;

public interface OffLineVideoService<T> {

    public List<T> find(String uuid, int getNum, List<Integer> excludeCatIds);

}

package com.miguan.recommend.service.xy;

import java.util.List;

public interface ClInterestLabelService {

    /**
     * 查询用户选择的标签，所对应的分类
     * @param uuid
     * @return
     */
    List<Integer> findCatIdOfUserChoose(String uuid);
}

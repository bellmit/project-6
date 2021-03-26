package com.miguan.advert.domain.service;

/**
 * @Description 代码位自动排序service
 * @Author zhangbinglin
 * @Date 2020/11/12 11:04
 **/
public interface AdAutoSortService {

    /**
     * 代码位自动排序
     */
    void adAutoSort();

    /**
     * 实时监测未满足阈值的代码位id，在下次重新排序前展现量是否达到了阈值。如果达到了阀值，将该代码位id回退至昨日排名，否则该代码位id的排序保持不变
     */
    void updateSortWhenGtThreshold();
}

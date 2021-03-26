package com.miguan.advert.domain.vo.interactive;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AbFlowDistribution {
    private Integer id;
    private Integer exp_id;
    private Map<Integer,Integer> group_maps ; //流量分配  <分组id,流量配比>
    private Integer app_id;
    //ratio的数值, 1：总.2：实验组,3:对照组
    private List<AbTraffic> ratio;  //流量配比 (切量比例)
}

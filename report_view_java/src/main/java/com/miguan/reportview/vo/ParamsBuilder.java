package com.miguan.reportview.vo;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
public class ParamsBuilder {

    private final Map<String, Object> param;

    public static ParamsBuilder builder(int size){
        return new ParamsBuilder(size);
    }

    private ParamsBuilder(int size) {
        this.param = Maps.newHashMapWithExpectedSize(size);

    }

    public ParamsBuilder put(String k, Object v){
        this.param.put(k, v);
        return this;
    }

    public Map<String, Object> get() {
        return param;
    }
}

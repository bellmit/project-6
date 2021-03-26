package com.miguan.xuanyuan.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 关联的应用信息
 * @Date 2021/3/1
 **/
@Data
public class RelateAppInfo {

    private Long appId;
    private List<RelateAppInfoVo> sourceAppInfos;
}

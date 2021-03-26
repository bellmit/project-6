package com.miguan.xuanyuan.vo.sdk;

import lombok.Data;

import java.util.List;

@Data
public class ConfigureInfoVo {
    private String appKey;
    private List<SourceAppInfoVo> sourceAppInfos;
    private int cacheTime; //缓存时间（秒）
}

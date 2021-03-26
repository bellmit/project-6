package com.miguan.ballvideo.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class AutoPushInfo {
    private String appPackage; // 马甲包
    private Long videoId;
    private Set<String> distinctIds;
    private Map<String,List<String>> connTokenDistinct;
    private Map<String, List<String>> tokens;
}

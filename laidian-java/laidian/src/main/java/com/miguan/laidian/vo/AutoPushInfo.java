package com.miguan.laidian.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class AutoPushInfo {
    private String appPackage; // 马甲包
    private Long videoId;
    private List<String> distinctIds;
    private Map<String,List<String>> deviceIdMap;
    private Map<String,List<String>> connTokenDistinct;
    private Map<String, List<String>> tokens;
}

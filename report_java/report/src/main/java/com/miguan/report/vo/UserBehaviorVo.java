
package com.miguan.report.vo;

import com.miguan.report.common.type.Alias;
import lombok.Data;

@Data
@SuppressWarnings("unused")
public class UserBehaviorVo {

    private Group _id;
    private long total;
    @Alias("视频开始广告")
    private long total0;
    @Alias("视频底部banner广告")
    private long total1;
    @Alias("视频结束广告")
    private long total2;
    @Alias("视频中间广告")
    private long total3;
    @Alias("首页列表")
    private long total4;
    @Alias("首页视频详情")
    private long total5;
    @Alias("搜索结果广告")
    private long total6;
    @Alias("搜索页广告")
    private long total7;
    @Alias("锁屏原生广告")
    private long total8;
    @Data
    @SuppressWarnings("unused")
    public static class Group {
        private String app_package;
        private String system_version;
    }
}

package com.miguan.recommend.bo;

import lombok.Data;

import java.util.List;

@Data
public class VideoQueryDto<T> {

    /**
     * 用户ID
     */
    private String uuid;
    /**
     * 视频ID
     */
    private List<String> videoIds;
    /**
     * 分类ID
     */
    private Integer catid;
    /**
     * 包含的分类ID
     */
    private List<Integer> includeCatid;
    /**
     * 需要排除的分类ID
     */
    private List<Integer> excludedCatid;
    /**
     * 需要排除的视频来源
     */
    private List<String> excludedSource;
    /**
     * 敏感
     */
    private Integer sensitive;
    /**
     * 是否低于平均值
     */
    private Integer isLowAvg;
    /**
     * 获取视频的个数
     */
    private Integer getNum;

    public VideoQueryDto(BaseDto baseDto, int getNum) {
        this.uuid = baseDto.getUuid();
        this.isLowAvg = baseDto.getIsLowAvg();
        this.getNum = getNum;
    }

    public VideoQueryDto(String userTag, Integer catid, int getNum) {
        this.uuid = userTag;
        this.catid = catid;
        this.sensitive = 0;
        this.getNum = getNum;
    }

    public VideoQueryDto(BaseDto baseDto, Integer catid, Integer sensitive, int getNum) {
        this.uuid = baseDto.getUuid();
        this.catid = catid;
        this.sensitive = sensitive;
        this.isLowAvg = baseDto.getIsLowAvg();
        this.getNum = getNum;
    }

    public VideoQueryDto(BaseDto baseDto, List<Integer> includeCatid, Integer catid, Integer sensitive, int getNum) {
        this.uuid = baseDto.getUuid();
        this.includeCatid = includeCatid;
        this.catid = catid;
        this.sensitive = sensitive;
        this.isLowAvg = baseDto.getIsLowAvg();
        this.getNum = getNum;
    }
}

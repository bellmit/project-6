package com.miguan.recommend.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@ApiModel(value = "用户特征实体")
@Data
@Slf4j
@NoArgsConstructor
public class UserFeature {
    @ApiModelProperty(value = "活跃天数", hidden = true)
    private int activeDay;
    @ApiModelProperty(value = "用户兴趣分类池", hidden = true)
    private List<String> catPoolList;
    @ApiModelProperty(value = "用户兴趣分类池转化", hidden = true)
    private List<String> catPoolListT;
    @ApiModelProperty(value = "用户兴趣场景池", hidden = true)
    private List<String> scenePoolList;
    @ApiModelProperty(value = "用户兴趣场景池转化", hidden = true)
    private List<String> scenePoolListT;
    @ApiModelProperty(value = "用户最近播放视频池", hidden = true)
    private List<String> videoPoolList;
    @ApiModelProperty(value = "用户分类兴趣度", hidden = true)
    private Map<Integer, Double> catFav;
    @ApiModelProperty(value = "用户分类兴趣度转化", hidden = true)
    private Map<Integer, Double> catFavT;
    @ApiModelProperty(value = "用户场景兴趣度", hidden = true)
    private Map<Integer, Double> sceneFav;
    @ApiModelProperty(value = "用户场景兴趣度转化", hidden = true)
    private Map<Integer, Double> sceneFavT;
    @ApiModelProperty(value = "用户所在城市", hidden = true)
    private String city;
    @ApiModelProperty(value = "用户近14日曝光数", hidden = true)
    private Integer showCount;
    @ApiModelProperty(value = "用户近14日播放数", hidden = true)
    private Integer playCount;

    public UserFeature(int activeDay, List<String> catPoolList, List<String> catPoolListT, List<String> scenePoolList,
                       List<String> scenePoolListT, List<String> videoPoolList, Map<Integer, Double> catFav,
                       Map<Integer, Double> sceneFav, String city, Map<Integer, Double> catFavT, Map<Integer, Double> sceneFavT,
                       Integer showCount, Integer playCount) {
        this.activeDay = activeDay;
        this.catPoolList = catPoolList;
        this.catPoolListT = catPoolListT;
        this.scenePoolList = scenePoolList;
        this.scenePoolListT = scenePoolListT;
        this.videoPoolList = videoPoolList;
        this.catFav = catFav;
        this.catFavT = catFavT;
        this.sceneFav = sceneFav;
        this.sceneFavT = sceneFavT;
        this.city = city;
        this.showCount = showCount;
        this.playCount = playCount;
    }
}

package com.miguan.reportview.dto;

import com.cgcg.context.util.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * 内容质量数据报表
 */
@Setter
@Getter
@ApiModel
public class ContentQualityDto {

    @ApiModelProperty(value = "应用", position = 10)
    private String packageName;

    @ApiModelProperty(value = "分类id", position = 11)
    private Long catId;

    @ApiModelProperty(value = "分类", position = 20)
    private String catName;

    @ApiModelProperty(value = "内容源", position = 30)
    private String videosSource;

    @ApiModelProperty(value = "总视频数", position = 40)
    private Long videoCount;

    @ApiModelProperty(value = "进文量", position = 50)
    private Long catJwCount;

    @ApiModelProperty(value = "上线量", position = 60)
    private Long catOnlineCount;

    @ApiModelProperty(value = "曝光数", position = 70)
    private Long showCount;

    @ApiModelProperty(value = "曝光量占比", position = 80)
    private Double showRate;

    @ApiModelProperty(value = "有效曝光率占比", position = 90)
    private Double vshowRate;

    @ApiModelProperty(value = "播放数", position = 100)
    private Long playCount;

    @ApiModelProperty(value = "平均播放率（来源）", position = 110)
    private Double playRate;

    @ApiModelProperty(value = "有效播放数", position = 120)
    private Long vplayCount;

    @ApiModelProperty(value = "平均有效播放率（来源）", position = 130)
    private Double vplayRate;

    @ApiModelProperty(value = "完播数", position = 140)
    private Long allPlayCount;

    @ApiModelProperty(value = "平均完播率（来源）", position = 150)
    private Double allPlayRate;

    @ApiModelProperty(value = "高于平均播放率的视频占比", position = 160)
    private Double gtPlayRate;

    @ApiModelProperty(value = "高于平均播放率的视频id(多个逗号分隔)", position = 161)
    private String gtPlayVideoId;

    @ApiModelProperty(value = "低于平均播放率的视频占比", position = 170)
    private Double ltPlayRate;

    @ApiModelProperty(value = "低于平均播放率的视频id(多个逗号分隔)", position = 171)
    private String ltPlayVideoId;

    @ApiModelProperty(value = "低于平均有效播放率的视频占比", position = 180)
    private Double ltVplayRate;

    @ApiModelProperty(value = "低于平均有效播放率的视频id(多个逗号分隔)", position = 181)
    private String ltVplayVideoId;

    @ApiModelProperty(value = "低于平均完播率的视频占比", position = 190)
    private Double ltAllPlayRate;

    @ApiModelProperty(value = "低于平均完播率的视频id(多个逗号分隔)", position = 191)
    private String ltAllPlayVideoId;

    @ApiModelProperty(value = "平均每曝光播放时长（分钟）", position = 200)
    private Double preShowTime;

    public void setGtPlayVideoId(String gtPlayVideoId) {
        if(StringUtils.isNotBlank(gtPlayVideoId)) {
            gtPlayVideoId = gtPlayVideoId.replace("[","").replace("]","").replace("'", "").replace(",", ",");
        } else {
            return;
        }
        List<String> list = Arrays.asList(gtPlayVideoId.split(","));
        if(list.size() >=5000) {
            list = list.subList(0, 5000);
        }
        this.gtPlayVideoId = String.join(",", list);
    }

    public void setLtPlayVideoId(String ltPlayVideoId) {
        if(StringUtils.isNotBlank(ltPlayVideoId)) {
            ltPlayVideoId = ltPlayVideoId.replace("[","").replace("]","").replace("'", "").replace(",", ",");
        } else {
            return;
        }
        List<String> list = Arrays.asList(ltPlayVideoId.split(","));
        if(list.size() >=5000) {
            list = list.subList(0, 5000);
        }
        this.ltPlayVideoId = String.join(",", list);
    }

    public void setLtVplayVideoId(String ltVplayVideoId) {
        if(StringUtils.isNotBlank(ltVplayVideoId)) {
            ltVplayVideoId = ltPlayVideoId.replace("[","").replace("]","").replace("'", "").replace(",", ",");
        } else {
            return;
        }
        List<String> list = Arrays.asList(ltVplayVideoId.split(","));
        if(list.size() >=5000) {
            list = list.subList(0, 5000);
        }
        this.ltVplayVideoId = String.join(",", list);
    }

    public void setLtAllPlayVideoId(String ltAllPlayVideoId) {
        if(StringUtils.isNotBlank(ltAllPlayVideoId)) {
            ltAllPlayVideoId = ltAllPlayVideoId.replace("[","").replace("]","").replace("'", "").replace(",", ",");
        } else {
            return;
        }
        List<String> list = Arrays.asList(ltAllPlayVideoId.split(","));
        if(list.size() >=5000) {
            list = list.subList(0, 5000);
        }
        this.ltAllPlayVideoId = String.join(",", list);
    }
}

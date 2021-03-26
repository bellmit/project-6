package com.miguan.recommend.entity.mongo;

import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.common.util.DateUtil;
import com.miguan.recommend.common.util.TimeUtil;
import com.miguan.recommend.vo.RecVideosVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 视频权重表
 *
 * @author zhongli
 * @date 2020-08-11
 */
@Setter
@Getter
@Document(MongoConstants.video_hotspot)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class VideoHotspotVo {

    @ApiModelProperty(value = "视频ID", hidden = true)
    public String video_id;
    @ApiModelProperty(value = "分类ID", hidden = true)
    public Integer catid;
    @ApiModelProperty(value = "状态 0 = 禁用 1 = 启用", hidden = true)
    public Integer state;
    @ApiModelProperty(value = "合集id", hidden = true)
    public Integer collection_id;
    @ApiModelProperty(value = "专辑id", hidden = true)
    public Integer album_id;
    @ApiModelProperty(value = "是否敏感 -2非敏感", hidden = true)
    public Integer sensitive;
    @ApiModelProperty(value = "权重", hidden = true)
    public Double weights;
    @ApiModelProperty(value = "权重1", hidden = true)
    public Double weights1;
    @ApiModelProperty(value = "上线时间", hidden = true)
    public String online_time;
    @ApiModelProperty(value = "视频时长", hidden = true)
    public Integer video_time;
    @ApiModelProperty(value = "是否头部视频： 0 否， 1是", hidden = true)
    public Integer is_score;
    @ApiModelProperty(value = "头部视频得分", hidden = true)
    public Double score;
    @ApiModelProperty(value = "视频来源", hidden = true)
    public String videos_source;
    @ApiModelProperty(value = "是否低于平均值(播放率、完播率、每播放播放时长其一)", hidden = true)
    public Integer is_low_avg;
    @ApiModelProperty(value = "百度飞桨置信度前5 的标签", hidden = true)
    public List<FullLable> top5_ids;
    @ApiModelProperty(value = "视频地址", hidden = true)
    public String video_url;
    @ApiModelProperty(value = "创建时间", hidden = true)
    public Date create_at;
    @ApiModelProperty(value = "更新时间", hidden = true)
    public Date update_at;

    public VideoHotspotVo(RecVideosVo recVideosVo, Integer albumId, Double weights, Double weights1, List<FullLable> top5_ids) {
        Date date = new Date();
        String onLinedate = DateUtil.dateStr4(recVideosVo.getOnlineDate());
        this.video_url = recVideosVo.getVideoUrl();
        this.video_id = recVideosVo.getId().toString();
        this.catid = recVideosVo.getCatId().intValue();
        this.state = recVideosVo.getState() == 1 ? 1 : 0;
        this.collection_id = recVideosVo.getGatherId().intValue();
        this.album_id = albumId;
        this.sensitive = recVideosVo.getSensitive();
        this.weights = weights;
        this.weights1 = weights1;
        this.online_time = onLinedate;
        this.video_time = TimeUtil.changeMinuteStringToSecond(recVideosVo.getVideoTime());
        this.is_score = 0;
        this.score = 0.0D;
        this.videos_source = recVideosVo.getVideosSource();
        this.is_low_avg = 0;
        this.top5_ids = top5_ids;
        this.create_at = date;
        this.update_at = date;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VideoHotspotVo) {
            VideoHotspotVo vo1 = (VideoHotspotVo) obj;
            return this.video_id.equals(vo1.getVideo_id());
        }
        return false;
    }
}

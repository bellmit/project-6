package com.miguan.reportview.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 视频权重表
 * @author zhongli
 * @date 2020-08-11 
 *
 */
@Setter
@Getter
@Document("video_hotspot")
@NoArgsConstructor
@AllArgsConstructor
public class VideoHotspotVo {



    private String video_id;
    private Integer catid;
    /**
     * 状态 0 = 禁用 1 = 启用
     */
    private Integer state;
    /**
     * 合集id
     */
    private Integer collection_id;
    /**
     * 权重
     */
    private Double weights;

    private String online_time;

    private Date create_at;
    private Date  update_at;
}

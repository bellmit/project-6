package com.miguan.ballvideo.vo.mongodb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/** 激励视频权重表
 * @author zhongli
 * @date 2020-08-11 
 *
 */
@Setter
@Getter
@Document("incentive_video_hotspot")
@NoArgsConstructor
@AllArgsConstructor
public class IncentiveVideoHotspot {


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
    private Double weights1;
    /**
     * 上线时间
     */
    private String online_time;
    /**
     * 视频时长
     */
    private Integer video_time;

    private Date create_at;
    private Date  update_at;


}

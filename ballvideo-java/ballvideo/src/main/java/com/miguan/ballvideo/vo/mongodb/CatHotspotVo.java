package com.miguan.ballvideo.vo.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author zhongli
 * @date 2020-08-11 
 *
 */
@Setter
@Getter
@Document("cat_hotspot")
public class CatHotspotVo {
    private int parent_catid;
    private int catid;

    /**
     * 权重
     */
    private double weights;
}

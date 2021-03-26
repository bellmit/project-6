package com.miguan.recommend.entity.ck;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DwsVideoDay {

    private Long videoId;
    /**
     * 曝光数
     */
    private Long show_count;
    /**
     * 播放数
     */
    private Long play_count;

    /**
     * 播放率
     */
    private Double play_rate;
}

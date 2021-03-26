package com.miguan.recommend.entity.mongo;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VideoScenarioSimilar {

    private Integer video_id;
    private List<Integer> sim_id_list;
    private List<SimDetail> sim_detail_list;
}

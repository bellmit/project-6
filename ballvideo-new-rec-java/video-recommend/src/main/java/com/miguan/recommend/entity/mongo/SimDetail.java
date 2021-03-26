package com.miguan.recommend.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimDetail {

    private Integer video_id;
    private Double similar;
    private String title;
    private List<String> label;
}

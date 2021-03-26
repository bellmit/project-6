package com.xiyou.speedvideo.entity.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/11/5 5:43 下午
 */
@Document(collection = "video_mca_result")
@Data
public class VideoMcaResult implements Serializable {

    @Id
    private String id;

    @Field(name = "video_id")
    private Integer videoId;

    private List results;

    @Field(name = "label_list")
    private List<String> labelList;

    @Field(name = "scenario_label")
    private List<String> scenarioLabel;

    @Field(name = "priority_label")
    private String priorityLabel;
}

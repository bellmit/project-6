package com.miguan.recommend.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VideoPaddleTag {

    @Field(value = "video_id")
    private Integer video_id;
    @Field(value = "title")
    private String title;
    @Field(value = "url")
    private String url;
    @Field(value = "simple_label")
    private List<String> simple_lable;
    @Field(value = "full_label")
    private List<FullLable> full_lable;
    @Field(value = "top3_ids")
    private List<Integer> top3_ids;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  VideoPaddleTag){
            VideoPaddleTag tag = (VideoPaddleTag) obj;
            return video_id.intValue() == tag.getVideo_id().intValue();
        }
        return false;
    }
}

package com.miguan.recommend.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Decimal128;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoMcaResult {

    private Object video_id;
    private String priority_label;

    public Integer getVideo_id() {
        if(video_id instanceof Decimal128){
            return ((Decimal128) video_id).bigDecimalValue().intValue();
        } else if(video_id instanceof Double){
            return ((Double) video_id).intValue();
        }
        return Integer.parseInt(video_id.toString());
    }
}

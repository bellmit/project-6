package com.miguan.recommend.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Decimal128;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioVideo {

    private String scenario;
    private Object num_id;
    private List<Object> video_ids;

    public Integer getNum_id() {
        if(num_id instanceof Decimal128){
            return ((Decimal128) num_id).bigDecimalValue().intValue();
        } else if(num_id instanceof Double){
            return ((Double) num_id).intValue();
        }
        return Integer.parseInt(num_id.toString());
    }

    public List<String> getVideo_ids() {
        List<String> videoIds = new ArrayList<String>(video_ids.size());
        video_ids.forEach(e ->{
            Integer videoId = null;
            if(e instanceof Decimal128){
                videoId =  ((Decimal128) e).bigDecimalValue().intValue();
            } else if(e instanceof Double){
                videoId =  ((Double) e).intValue();
            } else {
                videoId = Integer.parseInt(e.toString());
            }
            videoIds.add(videoId.toString());
        });
        return videoIds;
    }
}

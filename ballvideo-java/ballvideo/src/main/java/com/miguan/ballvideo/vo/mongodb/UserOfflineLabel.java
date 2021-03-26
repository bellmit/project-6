package com.miguan.ballvideo.vo.mongodb;

import lombok.Setter;
import org.bson.types.Decimal128;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Document("userOffline_label")
public class UserOfflineLabel {

    private String uuid;
    private Object active_day;
    private Integer catid;
    private Double cat_fav;

    public String getUuid() {
        return uuid;
    }

    public int getActive_day() {
        if(active_day instanceof Decimal128){
            return ((Decimal128) active_day).bigDecimalValue().intValue();
        } else if(active_day instanceof Double){
            return ((Double) active_day).intValue();
        }
        return Integer.parseInt(active_day.toString());
    }

    public Integer getCatid() {
        return catid;
    }

    public Double getCat_fav() {
        return cat_fav;
    }
}
package com.miguan.recommend.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Decimal128;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRawTags {

    private String uuid;
    private Integer tag_id;
    private Object tag_value;
    private Object weight;

    public Integer getTag_value() {
        if(tag_value instanceof Decimal128){
            return ((Decimal128) tag_value).bigDecimalValue().intValue();
        } else if(tag_value instanceof Double){
            return ((Double) tag_value).intValue();
        }
        return Integer.parseInt(tag_value.toString());
    }

    public Double getWeight() {
        if(weight instanceof Decimal128){
            return ((Decimal128) weight).bigDecimalValue().doubleValue();
        } else if(weight instanceof Double){
            return ((Double) weight).doubleValue();
        }
        return 0.0D;
    }
}

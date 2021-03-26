package com.miguan.recommend.entity.mongo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FullLable {

    @Field(value = "'class_id")
    private Integer class_id;
    @Field(value = "class_name")
    private String class_name;
    @Field(value = "probability")
    private Double probability;
}

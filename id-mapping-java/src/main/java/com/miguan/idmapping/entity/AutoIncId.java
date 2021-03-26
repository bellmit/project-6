package com.miguan.idmapping.entity;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author zhongli
 * @date 2020-09-02 
 *
 */
@Setter
@Getter
@Document("auto_inc_id")
public class AutoIncId {
    @Id
    private ObjectId id;
    private long incid;
    private String collectionName;
}

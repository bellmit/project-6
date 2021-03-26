package com.miguan.idmapping.entity;

import com.alibaba.fastjson.annotation.JSONField;
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
@Document("uuid_mapper")
public class UuidMapper {
    @Id
    @JSONField(serialize = false)
    private ObjectId id;
    private String uuid;
    private long mid;
    private String update_at;
}

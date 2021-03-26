package com.miguan.idmapping.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-09-02 
 *
 */
@Setter
@Getter
@TableName("uuid_mapper")
public class UuidMapperCH {
    @TableId
    private String uuid;
    private long mid;
    private String update_at;
}

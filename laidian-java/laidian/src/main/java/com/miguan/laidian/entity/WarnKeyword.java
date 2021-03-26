package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity(name = "warn_keyword")
@Getter
@Setter
@ApiModel("敏感词管理")
public class WarnKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("敏感词")
    @Column(name = "keyword")
    private String keyword;

    @ApiModelProperty("创建时间")
    @Column(name = "created_at")
    private Date createdAt;

    @ApiModelProperty("修改时间")
    @Column(name = "updated_at")
    private Date updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarnKeyword that = (WarnKeyword) o;
        return id.equals(that.id) &&
                keyword.equals(that.keyword) &&
                createdAt.equals(that.createdAt) &&
                updatedAt.equals(that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, keyword, createdAt, updatedAt);
    }
}
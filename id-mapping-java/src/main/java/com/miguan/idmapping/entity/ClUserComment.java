package com.miguan.idmapping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 评论点赞表
 * </p>
 *
 * @author jobob
 * @since 2020-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ClUserComment implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 评论id
     */
    private String commentId;

    /**
     * 数据类型
     */
    private Integer type;

    private Long videoId;


}

package com.miguan.idmapping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户视频关联表
 * </p>
 *
 * @author jobob
 * @since 2020-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ClUserVideos implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 视频id
     */
    private Long videoId;

    /**
     * 视频类型 10 首页视频 20小视频
     */
    private Integer videoType;

    /**
     * 收藏 0 未收藏 1收藏
     */
    private String collection;

    /**
     * 点赞 0 未点赞 1点赞
     */
    private String love;

    /**
     * 兴趣 0 感兴趣 1 不感兴趣
     */
    private String interest;

    /**
     * 分享 0 未分享 1分享
     */
    private String share;

    /**
     * 收藏时间
     */
    private LocalDateTime collectionTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}

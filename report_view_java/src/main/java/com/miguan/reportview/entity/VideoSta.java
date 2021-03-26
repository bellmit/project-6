package com.miguan.reportview.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 统计视频进文量和下线量(video_sta)实体类
 *
 * @author zhongli
 * @since 2020-08-07 20:35:51
 * @description 
 */
@Data
@NoArgsConstructor
@TableName("video_sta")
public class VideoSta implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
	private Long id;
    /**
     * 分类id
     */
    private Long catId;
    /**
     * 进文量/下线量
     */
    private Integer num;
    /**
     * 0=下线 1=进文
     */
    private Integer type;
    /**
     * 日期
     */
    private LocalDate date;

}
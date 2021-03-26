package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

/**
 * 热词表
 * @Author shixh
 * @Date 2020/1/9
 **/
@Entity(name="hot_word")
@Data
public class HotWord extends BaseModel{
    private String content;//热词内容
    private int state;//1-开启 0-关闭
    private int baseWeight;//权重
    private String editor;//操作者
}

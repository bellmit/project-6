package com.miguan.ballvideo.vo;

import lombok.Data;

/**
 * 热词表
 * @Author shixh
 * @Date 2020/1/9
 **/
@Data
public class HotWordVo {
    private String content;//热词内容
    private int state;//1-开启 0-关闭
    private int baseWeight;//权重
    private String editor;//操作者
}

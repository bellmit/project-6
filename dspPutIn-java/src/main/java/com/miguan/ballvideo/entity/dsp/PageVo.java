package com.miguan.ballvideo.entity.dsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: dspPutIn-java
 * @description: 前端入参分页
 * @author: suhj
 * @create: 2020-09-23 15:51
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVo {

    //当前页数
    private Integer page;
    //每页数量
    private Integer page_size;

}

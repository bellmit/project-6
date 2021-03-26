package com.miguan.ballvideo.entity.dsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * @program: dspPutIn-java
 * @description: 数据分页实体类
 * @author: suhj
 * @create: 2020-09-23 15:28
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    //数据总数
    private int total;
    //当前页数
    private int current_page;
    //每页数量
    private int per_page;
    //返回的数据
    List<Map<String,Object>> data;

}

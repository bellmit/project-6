package com.miguan.laidian.vo.iFlytek;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * Created by suhj on 2020/8/13.
 */
@ApiModel("词信息")
@Data
public class Word{

    @ApiModelProperty("关键词")
    private String w;

    @ApiModelProperty("角标，数组:1-最新、2-最热、3-最沸")
    private List<String> ics;

}
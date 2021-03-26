package com.miguan.laidian.vo.iFlytek;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by suhj on 2020/8/13.
 */
@Data
public class XFCommParamVo {

    @ApiModelProperty("页码")
    private String currentPage;

    @ApiModelProperty("单页数据数量")
    private String pageSize;

    @ApiModelProperty("运营商类型:1-中国移动;2-中国联通;2-中国联通;")
    private String operator;

}

package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PageResultVo {

    @ApiModelProperty("当前页面")
    private Integer current_page;

    @ApiModelProperty("第一页url")
    private String first_page_url;

    @ApiModelProperty("最后一页url")
    private String last_page_url;

    @ApiModelProperty("下一页url")
    private String next_page_url;

    @ApiModelProperty("上一页url")
    private String prev_page_url;

    @ApiModelProperty("url")
    private String path;

    @ApiModelProperty("一页多少条数据")
    private Integer per_page;

    @ApiModelProperty("最后页码")
    private Integer last_page;

    private Integer from;

    private Integer to;

    @ApiModelProperty("总共多少条数据")
    private Integer total;
}

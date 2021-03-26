package com.miguan.laidian.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * 用户视频关联表bean
 * @author hyl
 * @date 2019年11月4日19:41:31
 **/
@Data
@ApiModel("常见问题")
public class CommonQuestionVO {

    public static final String CLICK_NUM="clickNum";
    public static final String USEFUL_NUM="usefulNum";
    public static final String UNUSEFUL_NUM="unusefulNum";

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("点击类型 clickType  clickNum点击数 usefulNum有用数 unusefulNum没用数")
    private String clickType;


    @ApiModelProperty("手机品牌集合ID: 0-全部品牌")
    private String telBrandId;


    @ApiModelProperty("App类型")
    private String appType;
}

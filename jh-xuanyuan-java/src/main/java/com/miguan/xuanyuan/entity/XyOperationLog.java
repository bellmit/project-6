
package com.miguan.xuanyuan.entity;
import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * 接口请求日志记录
 * @author shixh
 *
 */
@Data
public class XyOperationLog extends BaseModel {

    @ApiModelProperty("操作账号")
    private Long userId;

    @ApiModelProperty("操作日期")
    private Date date;

    @ApiModelProperty("操作者ip地址")
    private String ip;

    @ApiModelProperty("路径名称")
    private String pathName;

    @ApiModelProperty("操作行为 1 增 2 删 3 改")
    private Integer type;

    @ApiModelProperty("操作事项")
    private String changeContent;

    @ApiModelProperty("操作平台 1 前台 2 后台")
    private Integer operationPlat;
}

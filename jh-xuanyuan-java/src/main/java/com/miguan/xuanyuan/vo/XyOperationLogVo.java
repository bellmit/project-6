
package com.miguan.xuanyuan.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class XyOperationLogVo extends BaseModel {
    @ApiModelProperty("操作日期")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date date;

    @ApiModelProperty("操作账号")
    private String operationUser;

    @ApiModelProperty("操作者ip地址")
    private String ip;

    @ApiModelProperty("操作路径")
    private String pathName;

    @ApiModelProperty("操作行为 1 增 2 删 3 改")
    private String typeName;

    @ApiModelProperty("操作事项")
    private String changeContent;

    @ApiModelProperty("操作平台 1 前台 2 后台")
    private Integer operationPlat;

    @ApiModelProperty("操作行为 1 增 2 删 3 改")
    private Integer type;
}

package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserListVo extends PageResultVo{

    @ApiModelProperty("用户列表信息")
    private List<UserDetailInfoVo> data;
}

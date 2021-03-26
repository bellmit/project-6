package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AccountListVo extends PageResultVo{

    @ApiModelProperty("账户列表信息")
    private List<AccountDetailInfoVo> data;
}

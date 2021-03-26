package com.miguan.xuanyuan.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreativeParamsDto {
    private String appKey;
    private String device;
    @NotNull(message = "必须传入轩辕的品牌id")
    private String appId;
    @NotNull(message = "必须传入广告位id")
    private Long positionId;
    @NotNull(message = "必须传入广告源代码")
    private String code;
}

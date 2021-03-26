package com.miguan.xuanyuan.vo;

import lombok.Data;

/**
 * @Author kangkunhuang
 * @Description 关联的应用信息
 * @Date 2021/3/1
 **/
@Data
public class RelateAppInfoVo {

    private Long id;
    private Long appId;
    private Long platId;
    private String platKey;
    private String platName;
    private String sourceAppId;
}

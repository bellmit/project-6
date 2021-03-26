package com.miguan.recommend.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 分类权重
 */
@Data
@AllArgsConstructor
public class CatWeightDto {

    private Integer catId;

    private Double weight;

}

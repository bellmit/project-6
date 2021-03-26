package com.miguan.recommend.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCatScoreDto {

    private Integer catId;
    private Integer score;
}

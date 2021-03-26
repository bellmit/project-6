package com.miguan.recommend.entity.es;

import lombok.Data;

/**
 * @Description ES用户向量
 **/
@Data
public class UserEmbeddingEs {
    //uuid
    private String user_id;

    //用户向量
    private String vector;
}

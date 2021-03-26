package com.miguan.idmapping.common.constants;


public interface RedisKeyConstant {

    String defalut = "ballVideos:";//公共前缀KEY
    //user token
    String USER_TOKEN = defalut + "token:user:";
    int USER_TOKEN_SECONDS = 60 * 60 * 24 * 30;
    //保存应用类别信息
    String APP_TYPE = "ballVideotask:appType:";

    int UUID_SECONDS = 60 * 60 * 12;
    String UUID_KEY = defalut + "uuid:";
}

package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.entity.PushArticleConfig;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 推送配置列表Mapper
 * @author zx.chen
 * @date 2020-08-17
 **/
public interface PushArticleConfigMapper {

    @Select("SELECT * FROM push_article_config WHERE push_channel = #{pushChannel} AND mobile_type = #{mobileType} AND app_package = #{appPackage}")
    PushArticleConfig findByPushChannelAndMobileTypeAndAppPackage(@Param("pushChannel") String pushChannel, @Param("mobileType") String mobileType, @Param("appPackage") String appPackage);
}

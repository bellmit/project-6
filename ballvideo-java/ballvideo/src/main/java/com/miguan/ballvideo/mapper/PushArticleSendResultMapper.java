package com.miguan.ballvideo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * 推送报表Mapper
 * @author zx.chen
 * @date 2020-08-17
 **/
public interface PushArticleSendResultMapper {

    @Insert("INSERT INTO push_article_send_result(business_id, push_article_id, push_channel, app_package, created_at, click_app_start, click_open_detail) " +
            "VALUE(#{businessId}, ${pushArticleId}, #{pushChannel}, #{appPackage}, NOW(), 0, 0)")
    void insertPushArticleSendResult (@Param("businessId")String businessId, @Param("pushArticleId")Long pushArticleId,
                                      @Param("pushChannel")String pushChannel, @Param("appPackage")String appPackage);
}

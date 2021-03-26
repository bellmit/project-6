package com.miguan.idmapping.common.utils;

import com.miguan.idmapping.common.enums.UserFromEnums;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author zhongli
 * @date 2020-07-21 
 *
 */
public class DbCollectionUtils {

    public static String getMongoCollection(UserFromEnums userFromEnums) {
        String colName;
        switch (userFromEnums) {
            case ANDROID: {
                colName = "android_user_idmap";
                break;
            }
            case IOS: {
                colName = "ios_user_idmap";
                break;
            }
            case H5: {
                colName = "h5_user_idmap";
                break;
            }
            case WX: {
                colName = "wx_user_idmap";
                break;
            }
            case XCX: {
                colName = "xcx_user_idmap";
                break;
            }
            case WEB: {
                colName = "web_user_idmap";
                break;
            }
            default:
                throw new NullPointerException("未找到mongo集合");
        }
        return colName;
    }

    public static MongoCollection<Document> getMongoCollection(MongoTemplate mongoTemplate, UserFromEnums userFromEnums) {

        return mongoTemplate.getCollection(getMongoCollection(userFromEnums));
    }

    public static String getDeviceMongoCollection(UserFromEnums userFromEnums) {
        String colName;
        switch (userFromEnums) {
            case ANDROID: {
                colName = "android_device_info";
                break;
            }
            case IOS: {
                colName = "ios_device_info";
                break;
            }
            case H5: {
                colName = "h5_device_info";
                break;
            }
            case WX: {
                colName = "wx_device_info";
                break;
            }
            case XCX: {
                colName = "xcx_device_info";
                break;
            }
            case WEB: {
                colName = "web_device_info";
                break;
            }
            default:
                throw new NullPointerException("未找到mongo集合");
        }
        return colName;
    }

    public static MongoCollection<Document> getDeviceMongoCollection(MongoTemplate mongoTemplate, UserFromEnums userFromEnums) {

        return mongoTemplate.getCollection(getDeviceMongoCollection(userFromEnums));
    }

    public static String getAppDeviceMongoCollection(UserFromEnums userFromEnums) {
        String colName;
        switch (userFromEnums) {
            case ANDROID: {
                colName = "android_app_device";
                break;
            }
            case IOS: {
                colName = "ios_app_device";
                break;
            }
            case H5: {
                colName = "h5_app_device";
                break;
            }
            case WX: {
                colName = "wx_app_device";
                break;
            }
            case XCX: {
                colName = "xcx_app_device";
                break;
            }
            case WEB: {
                colName = "web_app_device";
                break;
            }
            default:
                throw new NullPointerException("未找到mongo集合");
        }
        return colName;
    }

    public static MongoCollection<Document> getAppDeviceMongoCollection(MongoTemplate mongoTemplate, UserFromEnums userFromEnums) {

        return mongoTemplate.getCollection(getAppDeviceMongoCollection(userFromEnums));
    }
}

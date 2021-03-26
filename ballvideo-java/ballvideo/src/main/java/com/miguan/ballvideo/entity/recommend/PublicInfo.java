package com.miguan.ballvideo.entity.recommend;

import com.cgcg.context.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Data
public class PublicInfo {
//distinct_id::channel::package_name::manufacturer::model::os::os_version::screen_height::screen_width::carrier::network_type::is_new::is_new_app::first_visit_time ::imei::oaid::idfa::change_channel::is_login::uuid::creat_time::app_version::last_view::view::openid::longitude::latitude
    private String[] publicInfoArray;

    public PublicInfo(String publicInfoStr) {
        initPublicInfo(publicInfoStr);
        initColumnValue();
    }

    private void initPublicInfo(String publicInfoStr){
        byte[] bytes = Base64Utils.decodeFromString(publicInfoStr);
        publicInfoStr = new String(bytes, StandardCharsets.UTF_8);
        this.publicInfoArray = publicInfoStr.split("::");
    }

    private void initColumnValue(){
        this.distinctId = getValueByIndex(0);
        this.channel = getValueByIndex(1);
        this.packageName = getValueByIndex(2);
        this.manufacturer = getValueByIndex(3);
        this.model = getValueByIndex(4);
        this.os = getValueByIndex(5);
        this.osVersion = getValueByIndex(6);
        this.screenHeight = Integer.parseInt(getValueByIndex(7));
        this.screenWeight = Integer.parseInt(getValueByIndex(8));
        this.carrier = getValueByIndex(9);
        this.networkType = getValueByIndex(10);
        this.isNew = Boolean.parseBoolean(getValueByIndex(11));
        this.isNewApp = Boolean.parseBoolean(getValueByIndex(12));
        // is_login::uuid::creat_time::app_version::last_view::view::openid::longitude::latitude
        this.firstVisitTime = getValueByIndex(13);
        this.imei = getValueByIndex(14);
        this.oaid = getValueByIndex(15);
        this.idfa = getValueByIndex(16);
        this.changeChannel = getValueByIndex(17);
        this.isLogin = Boolean.parseBoolean(getValueByIndex(18));
        this.uuid = getValueByIndex(19);
        this.createTime = getValueByIndex(20);
        this.appVersion = getValueByIndex(21);
        this.lastView = getValueByIndex(22);
        this.view = getValueByIndex(23);
        try{
            this.openid = getValueByIndex(24);
            this.longtitude = Double.parseDouble(getValueByIndex(25,"0"));
            this.latitude = Double.parseDouble(getValueByIndex(26,"0"));
            this.gpscountry = getValueByIndex(27);
            this.gpsprovince = getValueByIndex(28);
            this.gpscity = getValueByIndex(29);
        } catch (Exception e){
            //log.error("请求头解析错误>>{}", JSONObject.toJSONString(publicInfoArray));
        }
    }

    private String getValueByIndex(int idx,String defaultValue){
        if(idx >= publicInfoArray.length){
            return defaultValue;
        }
        String tmp =publicInfoArray[idx];
        if(StringUtils.equals(tmp, "null") || StringUtils.equals(tmp, "(null)")){
            tmp = defaultValue;
        }
        return tmp;
    }

    private String getValueByIndex(int idx){
        return getValueByIndex(idx,"");
    }

    private String distinctId;
    /**
     * 初始渠道
     */
    private String channel;
    /**
     * 应用包名
     */
    private String packageName;
    /**
     * 设备制造商
     */
    private String manufacturer;
    /**
     * 机型
     */
    private String model;
    /**
     * 操作系统，例如iOS
     */
    private String os;
    /**
     * 操作系统版本，例如8.1.1
     */
    private String osVersion;
    /**
     * 屏幕高度，例如1920
     */
    private int screenHeight;
    /**
     * 屏幕宽度，例如1080
     */
    private int screenWeight;
    /**
     * 运营商名称
     */
    private String carrier;
    /**
     * 网络类型，例如4G
     */
    private String networkType;
    /**
     * 是否新设备
     */
    private boolean isNew;
    /**
     * 是否该应用新设备
     */
    private boolean isNewApp;
    private String firstVisitTime;
    private String imei;
    private String oaid;
    private String idfa;
    private String changeChannel;
    private boolean isLogin;
    private String uuid;
    private String createTime;
    private String appVersion;
    private String lastView;
    private String view;
    private String openid;
    private double longtitude;
    private double latitude;
    private String gpscountry;
    private String gpsprovince;
    private String gpscity;
}

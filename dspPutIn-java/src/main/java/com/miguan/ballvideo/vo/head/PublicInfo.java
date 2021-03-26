package com.miguan.ballvideo.vo.head;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;
import tool.util.StringUtil;

import java.nio.charset.StandardCharsets;

@Slf4j
@Data
public class PublicInfo {
    //distinct_id::channel::package_name::manufacturer::model::os::os_version::screen_height::screen_width::carrier::network_type::is_new::is_new_app::first_visit_time ::imei::oaid::idfa::change_channel::is_login::uuid::creat_time::app_version::last_view::view::openid::longitude::latitude
    private String[] publicInfoArray;

    public PublicInfo(String publicInfoStr) {
        if(StringUtil.isNotBlank(publicInfoStr)) {
            initPublicInfo(publicInfoStr);
            initColumnValue();
        }
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
//        this.isLogin = Boolean.parseBoolean(getValueByIndex(18));
//        this.uuid = getValueByIndex(19);
//        this.createTime = getValueByIndex(20);
//        this.appVersion = getValueByIndex(21);
//        this.lastView = getValueByIndex(22);
//        this.view = getValueByIndex(23);
        try{
//            this.openid = getValueByIndex(24);
            this.longtitude = Double.parseDouble(getValueByIndex(18,"0"));
            this.latitude = Double.parseDouble(getValueByIndex(19,"0"));
            this.gpscountry = getValueByIndex(20);
            this.gpsprovince = getValueByIndex(21);
            this.gpscity = getValueByIndex(22);
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
    /**
     * 国家
     */
    private String gpscountry;
    /**
     * 省
     */
    private String gpsprovince;
    /**
     * 城市
     */
    private String gpscity;

    public static void main(String[] args) {
        String publicStr="edae0fb489ee1a1c05d95f249a9a0f41::xysp_yingyongbao::com.mg.xyvideo::vivo::vivo X20Plus A::Android::8.1.0::2034::1080::null::wifi::false::false::2020-11-30 15:47:11,2020-11-30 15:47:11::866197036324538::null::null::null::118.196141::24.489745::中国::福建省::厦门市";
//        String info = "ZWRhZTBmYjQ4OWVlMWExYzA1ZDk1ZjI0OWE5YTBmNDE6Onh5c3BfeWluZ3lvbmdiYW86OmNvbS5tZy54eXZpZGVvOjp2aXZvOjp2aXZvIFgyMFBsdXMgQTo6QW5kcm9pZDo6OC4xLjA6OjIwMzQ6OjEwODA6Om51bGw6OndpZmk6OmZhbHNlOjpmYWxzZTo6MjAyMC0xMS0zMCAxNTo0NzoxMSwyMDIwLTExLTMwIDE1OjQ3OjExOjo4NjYxOTcwMzYzMjQ1Mzg6Om51bGw6Om51bGw6Om51bGw6OjExOC4xOTYxNDE6OjI0LjQ4OTc0NTo65Lit5Zu9Ojrnpo/lu7rnnIE6OuWOpumXqOW4gg==";
        String info = Base64Utils.encodeToString(publicStr.getBytes());
        System.out.println("加密后：" + info);
//        byte[] bytes = Base64Utils.decodeFromString(info);
//        String infoa = new String(bytes, StandardCharsets.UTF_8);
//        System.out.println(infoa);
        PublicInfo publicInfo = new PublicInfo(info);
        System.out.println(publicInfo);

        String publicStr2 = "NDM3NmVjM2VhMmY0OTBjNTZjYjc2Nzc2NzU1OWEyNTc6Onh5c3BfeWluZ3lvbmdiYW86OmNvbS5tZy54eXZpZGVvOjpIVUFXRUk6OkhXSS1BTDAwOjpBbmRyb2lkOjo5OjoyMDQwOjoxMDgwOjpudWxsOjp3aWZpOjpmYWxzZTo6ZmFsc2U6OjIwMjAtMTEtMzAgMTU6NTc6MzgsMjAyMC0xMS0yNiAxNzoxMToxMjo6ODY4MjI3MDMxMDgwMzg1OjpmZWJhYmZmZi1kNzdiLTRiZmMtYzNmNS1kM2ViOTc3MmU5MjI6Om51bGw6Om51bGw6OjExOC4xOTYyNDI6OjI0LjQ4OTc2OjrkuK3lm706Ouemj+W7uuecgTo65Y6m6Zeo5biC";
        byte[] bytes = Base64Utils.decodeFromString(publicStr2);
        String infoa = new String(bytes, StandardCharsets.UTF_8);
        String[] array = infoa.split("::");
        System.out.println("解密后：" + infoa);
    }
}

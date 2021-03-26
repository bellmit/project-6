package com.miguan.recommend.bo;

import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.util.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

@ApiModel(value = "公共信息实体")
@Slf4j
@Data
@NoArgsConstructor
public class PublicInfo {

    @ApiModelProperty(value = "公共信息数组", hidden = true)
    private String[] publicInfoArray;

    public PublicInfo(String publicInfoStr) {
        initPublicInfo(publicInfoStr);
        initColumnValue();
    }

    public static void main(String[] args) {
        String info = "MWI3MThlZGM0NDUwMGUzMzc1OTkxNGUyYzE2Mzk3NjY6OmRxc3BfaGoyX2dkdDAyOjpjb20ubWcuZHF2aWRlbzo6c2Ftc3VuZzo6U00tQzUwMTA6OkFuZHJvaWQ6OjguMC4wOjoxOTIwOjoxMDgwOjpudWxsOjp3aWZpOjp0cnVlOjp0cnVlOjoyMDIxLTAzLTEwIDE3OjMzOjE3LDIwMjEtMDMtMTAgMTc6MzM6MTc6OjM1NTA4NTA4MjI1ODkxNjo6bnVsbDo6bnVsbDo6ZHFzcF9oajJfZ2R0MDI6OmZhbHNlOjo1ZDBlYTVlYmU4ZTc0ZjQxYTI1NGExMmFmZmE1YjY2ZDo6MjAyMS0wMy0xMCAxNzozNToxNi40NDg6OjMuMi41NDo66aaW6aG1LeaOqOiNkDo655+t6KeG6aKR6K+m5oOF6aG1OjoxMTYuMzUyNzY4OjozOS44ODIxMDg6OuS4reWbvTo65YyX5Lqs5biCOjrljJfkuqzluII=";
        System.out.println(new String(Base64Utils.decodeFromString(info), StandardCharsets.UTF_8));
//        String testStr = "d22fd57f7e642cd38bb864e35f6b093a::ggsp_ad_gdt02::com.mg.ggvideo::HUAWEI::ASK-AL00x::Android::9::1411::720::CMCC::4G::false::false::2020-11-26 13:48:41,2020-11-26 13:48:41::864223044249269::bdb8fbfc-7fff-6cc2-f67b-e7f653b66816::null::ggsp_ad_gdt02::false::test::2020-11-27 18:17:59.018::3.2.13::首页-推荐::锁屏::120.27588026258681::30.29899169921875::null::null::null";
//        System.out.println(Base64Utils.encodeToString(testStr.getBytes()));
    }

    private void initPublicInfo(String publicInfoStr) {
        publicInfoStr = publicInfoStr.replace(" ", "+");
        byte[] bytes = Base64Utils.decodeFromString(publicInfoStr);
        publicInfoStr = new String(bytes, StandardCharsets.UTF_8);
        this.publicInfoArray = publicInfoStr.split("::");
    }

    private void initColumnValue() {
        this.distinctId = getValueByIndex(0);
        this.channel = getValueByIndex(1);
        this.packageName = getValueByIndex(2);
        this.manufacturer = getValueByIndex(3);
        this.model = getValueByIndex(4);
        this.os = getValueByIndex(5);
        this.osVersion = getValueByIndex(6);
        try{
            this.screenHeight = Integer.parseInt(getValueByIndex(7));
            this.screenWeight = Integer.parseInt(getValueByIndex(8));
        } catch (Exception e){
            log.warn("请求头解析错误, 屏高宽异常>>{}", JSONObject.toJSONString(publicInfoArray));
        }
        this.carrier = getValueByIndex(9);
        this.networkType = getValueByIndex(10);
        this.isNew = Boolean.parseBoolean(getValueByIndex(11));
        this.isNewApp = Boolean.parseBoolean(getValueByIndex(12));
//        this.isNewApp = true;
        this.firstVisitTime = getValueByIndex(13);
        this.imei = getValueByIndex(14);
        this.oaid = getValueByIndex(15);
        this.idfa = getValueByIndex(16);
        this.changeChannel = getValueByIndex(17);
        this.isLogin = Boolean.parseBoolean(getValueByIndex(18));
        this.uuid = getValueByIndex(19);
//        this.uuid = "9e0a0273e17c478a9e3743ba3a7fc533";
        this.createTime = getValueByIndex(20);
        this.appVersion = getValueByIndex(21);
        this.lastView = getValueByIndex(22);
        this.view = getValueByIndex(23);
        try {
//            this.openid = getValueByIndex(24);
            this.longtitude = Double.parseDouble(getValueByIndex(24, "0"));
            this.latitude = Double.parseDouble(getValueByIndex(25, "0"));
            this.gpscountry = getValueByIndex(26);
            this.gpsprovince = getValueByIndex(27);
            this.gpscity = getValueByIndex(28);
        } catch (Exception e) {
            log.error("请求头解析错误>>{}", JSONObject.toJSONString(publicInfoArray));
        }
    }

    private String getValueByIndex(int idx, String defaultValue) {
        if (idx >= publicInfoArray.length) {
            return defaultValue;
        }
        String tmp = publicInfoArray[idx];
        if (StringUtils.equals(tmp, "null") || StringUtils.equals(tmp, "(null)")) {
            tmp = defaultValue;
        }
        return tmp;
    }

    private String getValueByIndex(int idx) {
        return getValueByIndex(idx, "");
    }

    /**
     * 获取当前渠道
     * 如果changeChannel为空，直接返回channel的值
     * @return
     */
    public String getCurrentChannel(){
        if(StringUtils.isEmpty(this.changeChannel)){
            return this.channel;
        }
        return this.changeChannel;
    }

    @ApiModelProperty(value = "设备ID", hidden = true)
    private String distinctId;
    @ApiModelProperty(value = "初始渠道", hidden = true)
    private String channel;
    @ApiModelProperty(value = "应用包名", hidden = true)
    private String packageName;
    @ApiModelProperty(value = "设备制造商", hidden = true)
    private String manufacturer;
    @ApiModelProperty(value = "机型", hidden = true)
    private String model;
    @ApiModelProperty(value = "操作系统，例如iOS", hidden = true)
    private String os;
    @ApiModelProperty(value = "操作系统版本，例如8.1.1", hidden = true)
    private String osVersion;
    @ApiModelProperty(value = "屏幕高度，例如1920", hidden = true)
    private int screenHeight;
    @ApiModelProperty(value = "屏幕宽度，例如1080", hidden = true)
    private int screenWeight;
    @ApiModelProperty(value = "运营商名称", hidden = true)
    private String carrier;
    @ApiModelProperty(value = "网络类型，例如4G", hidden = true)
    private String networkType;
    @ApiModelProperty(value = "是否新设备", hidden = true)
    private boolean isNew;
    @ApiModelProperty(value = "是否该应用新设备", hidden = true)
    private boolean isNewApp;
    @ApiModelProperty(value = "设备在该马甲包首次访问时间", hidden = true)
    private String firstVisitTime;
    @ApiModelProperty(value = "imei", hidden = true)
    private String imei;
    @ApiModelProperty(value = "oaid", hidden = true)
    private String oaid;
    @ApiModelProperty(value = "idfa", hidden = true)
    private String idfa;
    @ApiModelProperty(value = "更新渠道", hidden = true)
    private String changeChannel;
    @ApiModelProperty(value = "是否登录", hidden = true)
    private boolean isLogin;
    @ApiModelProperty(value = "用户唯一标识", hidden = true)
    private String uuid;
    @ApiModelProperty(value = "创建时间", hidden = true)
    private String createTime;
    @ApiModelProperty(value = "应用的版本", hidden = true)
    private String appVersion;
    @ApiModelProperty(value = "上一页", hidden = true)
    private String lastView;
    @ApiModelProperty(value = "当前页", hidden = true)
    private String view;
    @ApiModelProperty(value = "微信openid", hidden = true)
    private String openid;
    @ApiModelProperty(value = "客户端GPS定位：经度", hidden = true)
    private double longtitude;
    @ApiModelProperty(value = "客户端GPS定位：纬度", hidden = true)
    private double latitude;
    @ApiModelProperty(value = "客户端GPS定位：国家", hidden = true)
    private String gpscountry;
    @ApiModelProperty(value = "客户端GPS定位：省", hidden = true)
    private String gpsprovince;
    @ApiModelProperty(value = "客户端GPS定位：城市", hidden = true)
    private String gpscity;
}

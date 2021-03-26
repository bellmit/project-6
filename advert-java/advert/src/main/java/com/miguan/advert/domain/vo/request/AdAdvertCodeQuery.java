package com.miguan.advert.domain.vo.request;


import com.cgcg.context.util.StringUtils;
import lombok.Data;

import java.util.Date;

@Data
public class AdAdvertCodeQuery {
    private String app_package;
    private String ad_id;
    private String plat_key;
    private String type_key;
    private String position_id_str;
    private Integer put_in;

    private String[] app_package_arr;
    private String[] ad_id_arr;
    private String[] plat_key_arr;
    private String[] type_key_arr;
    private String[] position_id_strs;

    public String getPlat_key() {
        return plat_key;
    }

    public void setPlat_key(String plat_key) {
        if(StringUtils.isEmpty(plat_key)){
            this.plat_key = plat_key;
        } else {
            this.plat_key = plat_key;
            this.plat_key_arr = plat_key.split(",");
        }
    }

    public String getApp_package() {
        return app_package;
    }

    public void setApp_package(String app_package) {
        if(StringUtils.isNotEmpty(app_package)){
            this.app_package_arr = app_package.split(",");
        }
        this.app_package = app_package;
    }

    public String getAd_id() {
        return ad_id;
    }

    public void setAd_id(String ad_id) {
        if(StringUtils.isNotEmpty(ad_id)){
            this.ad_id_arr = ad_id.split(",");
        }
        this.ad_id = ad_id;
    }

    public String getType_key() {
        return type_key;
    }

    public void setType_key(String type_key) {
        if(StringUtils.isNotEmpty(type_key)){
            this.type_key_arr = type_key.split(",");
        }
        this.type_key = type_key;
    }

    public String getPosition_id_str() {
        return position_id_str;
    }

    public void setPosition_id_str(String position_id_str) {
        if(StringUtils.isNotEmpty(position_id_str)){
            this.position_id_strs = position_id_str.split(",");
        }
        this.position_id_str = position_id_str;
    }
}

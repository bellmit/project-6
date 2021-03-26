package com.miguan.ballvideo.entity.recommend;

import lombok.Data;

@Data
public class Feature {
//    long is_new = StringUtils.getLongValueFromMapObject(map, "is_new");
//    long package_name = StringUtils.stringToDec(StringUtils.getValueFromMapObject(map, "package_name"));
//    long os = StringUtils.stringToDec(StringUtils.getValueFromMapObject(map, "os"));
//    long channel = StringUtils.stringToDec(StringUtils.getValueFromMapObject(map, "channel"));
    private long isNew;
    private String packageName;
    private String os;
    private String channel;
    //    long hour = StringUtils.getLongValueFromMapObject(map, "hour");
//    long week = StringUtils.getLongValueFromMapObject(map, "week");
    private short hour;
    private short week;

    //    long active_day = StringUtils.getLongValueFromMapObject(map, "active_day");
    private short activeDay;
    //    long city = StringUtils.stringToDec(StringUtils.getValueFromMapObject(map, "city"));
    private String city;

//    long ckh_model_show =  StringUtils.getLongValueFromMapObject(map, "model_show");
//    long ckh_model_play = StringUtils.getLongValueFromMapObject(map, "model_play");
//    long ckh_city_show = StringUtils.getLongValueFromMapObject(map, "city_show");
//    long ckh_city_play = StringUtils.getLongValueFromMapObject(map, "city_play");
//    long ckh_package_show = StringUtils.getLongValueFromMapObject(map, "package_show");
//    long ckh_package_play = StringUtils.getLongValueFromMapObject(map, "package_play");
//    long ckh_channel_show = StringUtils.getLongValueFromMapObject(map, "channel_show");
//    long ckh_channel_play = StringUtils.getLongValueFromMapObject(map, "channel_play");
//    long ckh_os_show = StringUtils.getLongValueFromMapObject(map, "os_show");
//    long ckh_os_play = StringUtils.getLongValueFromMapObject(map, "os_play");
//    long ckh_active_show = StringUtils.getLongValueFromMapObject(map, "active_show");
//    long ckh_active_play = StringUtils.getLongValueFromMapObject(map, "active_play");
//    long ckh_isnew_old_show = StringUtils.getLongValueFromMapObject(map, "isnew_old_show");
//    long ckh_isnew_old_play = StringUtils.getLongValueFromMapObject(map, "isnew_old_play");
    private long modelShow;
    private long modelPlay;

    private long cityShow;
    private long cityPlay;

    private long packageShow;
    private long packagePlay;

    private long channelShow;
    private long channelPlay;

    private long osShow;
    private long osPlay;

    private long activeShow;
    private long activePlay;

    private long isnewOldShow;
    private long isnewOldPlay;

    //    long cat_id = catList.indexOf(StringUtils.getLongValueFromMapObject(map, "cat_id"));
    //    double ckh_off_catFav = StringUtils.getDoubleValueFromMapObject(map, "off_catfav");
//    double real_catFav = StringUtils.getDoubleValueFromMapObject(map, "real_catfav");
    private long catId;
    private double offCatfav;
    private double realCatfav;

//    long clkSumSum= StringUtils.getLongValueFromMapObject(map,"clk_sum");
//    long showSumSum= StringUtils.getLongValueFromMapObject(map,"show_sum");
}

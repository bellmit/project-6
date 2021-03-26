package com.miguan.advert.common.util.adv;

import com.miguan.advert.common.constants.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;

/**
 * @Author shixh
 * @Date 2020/4/22
 **/
@Slf4j
public class AdvSQLUtils {

    public static final String ERROR_KEY = "AdvertErrorLog_";

    //代码位字段
    public static final String adCode_fields = "acode.id,acode.ad_id as adId,acode.material_key as material,acode.plat_key as plat,acode.render_key as render,acode.sdk_key as sdk,acode.type_key as adType,acode.permission as permission,CASE acode.ladder WHEN ladder=1 then acode.ladder_price else 0 end ladderPrice,";
    //广告位字段
    public static final String aPosition_fields= "aposition.id as positionId,aposition.position_type as positionType,aposition.mobile_type as mobileType,aposition.first_load_position as firstLoadPosition,aposition.second_load_position as secondLoadPosition,aposition.max_show_num as maxShowNum,";
    //广告配置代码位字段
    public static final String aConfigCode_fields= "aconfigcode.option_value as optionValue,";
    //广告配置字段
    public static final String aConfig_fields= "aconfig.computer as computer,";
    //98创意广告字段
    public static final String iAdvert_fields= "idea.title,idea.type_url AS url,idea.type AS linkType,idea.image_url AS imgPath,";

    public static final String position_type_fields= "aposition.id as positionId,aposition.position_type as positionType,";
    /**
     * type=0,查询所有的
     * type=1,只查询广告位字段
     * @param type
     * @return
     */
    public static StringBuffer getAdvFields(int type) {
        StringBuffer sb = new StringBuffer("");
        switch (type){
            case 1: sb.append(adCode_fields).append(aPosition_fields).append(aConfigCode_fields).append(aConfig_fields).append(iAdvert_fields);
                break;
            case 2: sb.append(position_type_fields);
                break;
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb;
    }

    public static StringBuffer getSqlString(int fieldType) {
        StringBuffer nativeSql = new StringBuffer("select ");
        nativeSql.append(getAdvFields(fieldType)).append(" ");
        nativeSql.append("from ad_advert_position aposition ");
        nativeSql.append("left join ad_advert_config aconfig on aconfig.position_id = aposition.id ");
        nativeSql.append("left join ad_advert_config_code aconfigcode on aconfigcode.config_id = aconfig.id ");
        nativeSql.append("left join ad_advert_code acode on acode.id = aconfigcode.code_id ");
        nativeSql.append("left join idea_initiative_advert idea on idea.ad_code = acode.ad_id ");
        nativeSql.append("and idea.status = ").append(Constant.open).append(" ");
        nativeSql.append("where aconfig.state = ").append(Constant.open).append(" ");
        return nativeSql;
    }

    public static String getAdverByParams(Map<String, Object> param,int fieldType) {

        //查询语句组装
        StringBuffer nativeSql = new StringBuffer();

        if("flow".equals(MapUtils.getString(param,"queryType"))){
            String appId = MapUtils.getString(param, "appId");
            String secretkey = MapUtils.getString(param, "secretkey");
            String poskey = MapUtils.getString(param, "poskey");
            String os = MapUtils.getString(param, "os");
            nativeSql.append("select ");
            nativeSql.append(getAdvFields(fieldType)).append(" ");
            nativeSql.append("from ad_advert_position aposition ");
            nativeSql.append("left join ad_advert_config aconfig on aconfig.position_id = aposition.id ");
            nativeSql.append("left join ad_app app on app.package_name = aposition.app_package ");
            nativeSql.append("left join ad_account acc on acc.id = app.account_id ");
            nativeSql.append("left join ad_advert_config_code aconfigcode on aconfigcode.config_id = aconfig.id ");
            nativeSql.append("left join ad_advert_code acode on acode.id = aconfigcode.code_id ");
            nativeSql.append("left join idea_initiative_advert idea on idea.ad_code = acode.ad_id ");
            nativeSql.append("and idea.status = ").append(Constant.open).append(" ");
            nativeSql.append("where aconfig.state = ").append(Constant.open).append(" ");
            nativeSql.append("and aposition.status = ").append(Constant.open).append(" ");
            nativeSql.append("and app.status = ").append(Constant.open).append(" ");
            nativeSql.append("and acc.status = ").append(Constant.open).append(" ");
            nativeSql.append("and app.dock_type = 1 ");//只允许API接入
            if (StringUtils.isNotEmpty(poskey)) {
                nativeSql.append(" and aposition.position_type = '").append(poskey).append("' ");
            }
            if (StringUtils.isNotEmpty(appId)) {
                nativeSql.append(" and app.app_id = '").append(appId).append("' ");
            }
            if (StringUtils.isNotEmpty(secretkey)) {
                nativeSql.append(" and app.secret_key = '").append(secretkey).append("' ");
            }
            if("ios".equals(os)){
                os = "1";
            }else if("android".equals(os)){
                os = "2";
            }
            nativeSql.append(" and aposition.mobile_type = '").append(os).append("' ");
        }else{
            String positionType = MapUtils.getString(param, "positionType");
            String mobileType = MapUtils.getString(param, "mobileType");
            String channelId = MapUtils.getString(param, "channelId");
            String permission = MapUtils.getString(param, "permission");
            String appVersion = MapUtils.getString(param, "appVersion");
            String appPackage = MapUtils.getString(param, "appPackage");
            String lockScreen = MapUtils.getString(param, "lockScreen");
            String positionTypes = MapUtils.getString(param, "positionTypes");
            String game = MapUtils.getString(param, "game");
            nativeSql = getSqlString(fieldType);
            nativeSql.append(" and concat(lpad(SUBSTRING_INDEX('").append(appVersion).append("','.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX('").append(appVersion).append("','.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX('").append(appVersion).append("','.',-1), 3, '0')) + 0");
            nativeSql.append(" BETWEEN ");
            nativeSql.append(" concat(lpad(SUBSTRING_INDEX(version1,'.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(version1,'.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX(version1,'.',-1), 3, '0')) + 0");
            nativeSql.append(" AND ");
            nativeSql.append(" concat(lpad(SUBSTRING_INDEX(version2,'.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(version2,'.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX(version2,'.',-1), 3, '0')) + 0");
            if ("0".equals(permission)) {
                nativeSql.append(" and acode.permission = '").append(permission).append("' ");
            }
            if (StringUtils.isNotEmpty(positionType)) {
                nativeSql.append(" and aposition.position_type = '").append(positionType).append("' ");
            }
            if (StringUtils.isNotEmpty(positionTypes)) {
                nativeSql.append(" and aposition.position_type in (").append(positionTypes).append(") ");
            }
            if (StringUtils.isNotEmpty(lockScreen)) {
                nativeSql.append(" and aposition.position_type in ('lockScreenDeblocking','lockH5ScreenDeblocking','lockAppScreenDeblocking')");
            }
            if (StringUtils.isNotEmpty(game)) {
                nativeSql.append(" and aposition.position_type like concat('Game','%')");
            }
            nativeSql.append(" and aconfig.app_package = '").append(appPackage).append("' ");
            nativeSql.append(" and aposition.mobile_type = '").append(mobileType).append("' ");
            nativeSql.append(" and if(acode.channel_type=2,locate('").append(channelId + ",").append("', concat(acode.channel_ids, ','))>0,1=1 )");
            nativeSql.append(" and if(acode.channel_type=3,locate('").append(channelId + ",").append("', concat(acode.channel_ids, ','))=0,1=1 )");
        }




        return nativeSql.toString();
    }


}

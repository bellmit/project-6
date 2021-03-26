package com.miguan.laidian.common.util.adv;

import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.entity.AdvertErrorLog;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author laiyd
 * @Date 2020/5/7
 **/
public class AdvSQLUtils {

    //代码位字段
    public static final String adCode_fields = "acode.id,acode.ad_id as adId,acode.material_key as material,acode.plat_key as plat,acode.render_key as render,acode.sdk_key as sdk,acode.type_key as adType,acode.permission as permission,CASE acode.ladder WHEN ladder=1 then acode.ladder_price else 0 end ladderPrice,";
    //广告位字段
    public static final String aPosition_fields = "aposition.id as positionId,aposition.position_type as positionType,aposition.mobile_type as mobileType,aposition.first_load_position as firstLoadPosition,aposition.second_load_position as secondLoadPosition,aposition.max_show_num as maxShowNum,";
    //广告配置代码位字段
    public static final String aConfigCode_fields= "CASE WHEN test.computer = 1 THEN rela.matching WHEN test.computer in (2,3) THEN rela.order_num ELSE 1 END optionValue, rela.number as sortNumber, ";
    //广告配置字段
    public static final String aConfig_fields= "test.computer as computer, flow.test_state as testFlag,";
    //98创意广告字段
    public static final String iAdvert_fields= "'' AS title,'' AS url,'' AS linkType,'' AS imgPath,";

    public static final String position_type_fields = "aposition.id as positionId,aposition.position_type as positionType,";

    /**
     * type=0,查询所有的
     * type=1,只查询广告位字段
     *
     * @param type
     * @return
     */
    public static StringBuffer getAdvFields(int type) {
        StringBuffer sb = new StringBuffer("");
        switch (type) {
            case 1:
                sb.append(adCode_fields).append(aPosition_fields).append(aConfigCode_fields).append(aConfig_fields);
                break;
            case 2:
                sb.append(position_type_fields);
                break;
            case 3:
                sb.append(aPosition_fields);
                break;
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb;
    }

    public static StringBuffer getSqlString(int fieldType) {
        StringBuffer nativeSql = new StringBuffer("select ");
        nativeSql.append(getAdvFields(fieldType)).append(" ");
        nativeSql.append("from ad_advert_position aposition ");
        nativeSql.append("LEFT JOIN ad_advert_flow_config flow ON flow.position_id = aposition.id ");
        nativeSql.append("LEFT JOIN ad_advert_test_config test on flow.id = test.flow_id ");
        nativeSql.append("LEFT JOIN ad_test_code_relation rela on test.id = rela.config_id ");
        nativeSql.append("left join ad_advert_code acode on acode.id = rela.code_id ");
        nativeSql.append("where aposition.status = ").append(Constant.open).append(" ");
        return nativeSql;
    }

    public static String getAdverByParams(Map<String, Object> param, int fieldType) {
        String postitionType = MapUtils.getString(param, "postitionType");
        String mobileType = MapUtils.getString(param, "mobileType");
        String channelId = MapUtils.getString(param, "channelId");
        String permission = MapUtils.getString(param, "adPermission");
        String appVersion = MapUtils.getString(param, "appVersion");
        String appPackage = MapUtils.getString(param, "appType");
        //查询语句组装
        StringBuffer nativeSql = getSqlString(fieldType);
        nativeSql.append(" and flow.type = 1 ");
        nativeSql.append(" and flow.test_state = 0 ");
        nativeSql.append(" and concat(lpad(SUBSTRING_INDEX('").append(appVersion).append("','.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX('").append(appVersion).append("','.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX('").append(appVersion).append("','.',-1), 3, '0')) + 0");
        nativeSql.append(" BETWEEN ");
        nativeSql.append(" concat(lpad(SUBSTRING_INDEX(version1,'.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(version1,'.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX(version1,'.',-1), 3, '0')) + 0");
        nativeSql.append(" AND ");
        nativeSql.append(" concat(lpad(SUBSTRING_INDEX(version2,'.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(version2,'.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX(version2,'.',-1), 3, '0')) + 0");
        if ("0".equals(permission)) {
            nativeSql.append(" and acode.permission = '").append(permission).append("' ");
        }
        if (StringUtils.isNotEmpty(postitionType)) {
            nativeSql.append(" and aposition.position_type = '").append(postitionType).append("' ");
        }
        nativeSql.append(" and aposition.app_package = '").append(appPackage).append("' ");
        nativeSql.append(" and aposition.mobile_type = '").append(mobileType).append("' ");
        nativeSql.append(" and if(acode.channel_type=2,locate('").append(channelId + ",").append("', concat(acode.channel_ids, ','))>0,1=1 )");
        nativeSql.append(" and if(acode.channel_type=3,locate('").append(channelId + ",").append("', concat(acode.channel_ids, ','))=0,1=1 )");
        return nativeSql.toString();
    }

    public static String getAdverPositionByParams(Map<String, Object> param, int fieldType) {
        String appPackage = MapUtils.getString(param, "appType");
        String mobileType = MapUtils.getString(param, "mobileType");
        //查询语句组装
        StringBuffer nativeSql = new StringBuffer("select ");
        nativeSql.append(getAdvFields(fieldType)).append(" ");
        nativeSql.append("from ad_advert_position aposition ");
        nativeSql.append(" where aposition.app_package = '").append(appPackage).append("' ");
        nativeSql.append(" and aposition.mobile_type = '").append(mobileType).append("' ");
        return nativeSql.toString();
    }

    public static String splicingSQL(List<AdvertErrorLog> collect) {
        StringBuffer sb = new StringBuffer("INSERT INTO `ad_error` (`ad_id`, `ad_error`, `type_key`, `plat_key`,`app_package`,`app_version`,`device_id`,`mobile_type`,`creat_time`,`position_id`,`channel_id`,`render`,`sdk`,`app_time`) VALUES ");
        for (AdvertErrorLog adError : collect) {
            if(adError.getAppTime()==null){
                adError.setAppTime(adError.getCreatTime().getTime());
            }
            sb.append("(");
            sb.append("'" + adError.getAdId() + "'" + ",");
            sb.append("'" + adError.getAdError() + "'" + ",");
            sb.append("'" + adError.getTypeKey() + "'" + ",");
            sb.append("'" + adError.getPlatKey() + "'" + ",");
            sb.append("'" + adError.getAppPackage() + "'" + ",");
            sb.append("'" + adError.getAppVersion() + "'" + ",");
            sb.append("'" + adError.getDeviceId() + "'" + ",");
            sb.append("'" + adError.getMobileType() + "'" + ",");
            sb.append("'" + DateUtil.parseDateToStr(adError.getCreatTime(), "yyyy-MM-dd HH:mm:ss") + "'" + ",");
            sb.append("'" + adError.getPositionId() + "',");
            sb.append("'" + adError.getChannelId() + "',");
            sb.append("'" + adError.getRender() + "',");
            sb.append("'" + adError.getSdk() + "'," );
            sb.append(adError.getAppTime());
            sb.append(")");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");
        return sb.toString();
    }

}

package com.miguan.ballvideo.common.util.adv;

import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.entity.AdvertErrorLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
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
    public static final String aPosition_fields= "aposition.id as positionId,aposition.position_type as positionType,aposition.mobile_type as mobileType,aposition.first_load_position as firstLoadPosition,aposition.second_load_position as secondLoadPosition,aposition.max_show_num as maxShowNum," +
            "aposition.custom_rule1 as customRule1,aposition.custom_rule1_name as customRule1Name,aposition.custom_rule2 as customRule2,aposition.custom_rule2_name as customRule2Name,aposition.custom_rule3 as customRule3,aposition.custom_rule3_name as customRule3Name,";
    //广告配置代码位字段
    public static final String aConfigCode_fields= "CASE WHEN test.computer = 1 THEN rela.matching WHEN test.computer in (2,3) THEN rela.order_num ELSE 1 END optionValue, rela.number as sortNumber, ";
    //广告配置字段
    public static final String aConfig_fields= "test.computer as computer, flow.test_state as testFlag,";
    //98创意广告字段
    public static final String iAdvert_fields= "'' AS title,'' AS url,'' AS linkType,'' AS imgPath,";

    public static final String position_type_fields= "aposition.id as positionId,aposition.position_type as positionType,";
    public static String splicingSQL(List<AdvertErrorLog> collect) {
        StringBuffer sb = new StringBuffer("INSERT INTO `ad_error_1` (`ad_id`, `ad_error`, `type_key`, `plat_key`,`app_package`,`app_version`,`device_id`,`mobile_type`,`creat_time`,`render`,`channel_id`,`position_id`,`sdk`,`app_time`) VALUES ");
        for (AdvertErrorLog adError : collect) {
            if(StringUtils.isNotEmpty(adError.getAdError()) && adError.getAdError().contains("java.net.ProtocolException")){
                log.error(ERROR_KEY+"乱码不保存："+adError.getAdError());
                continue;
            }
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
            sb.append("'" + adError.getRender() + "'" + ",");
            sb.append("'" + adError.getChannelId() + "'" + ",");
            sb.append(StringUtils.isBlank(adError.getPositionId())?0:adError.getPositionId()).append(",");//广告位置ID为空，默认0
            sb.append("'" + adError.getSdk() + "'" + ",");
            sb.append(adError.getAppTime());
            sb.append(")");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");
        return sb.toString();
    }

    /**
     * type=0,查询所有的
     * type=1,只查询广告位字段
     * @param type
     * @return
     */
    public static StringBuffer getAdvFields(int type) {
        StringBuffer sb = new StringBuffer("");
        switch (type){
            case 1: sb.append(adCode_fields).append(aPosition_fields).append(aConfigCode_fields).append(aConfig_fields);
                break;
            case 2: sb.append(position_type_fields);
                break;
            case 3: sb.append(aPosition_fields);
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

    public static StringBuffer getSqlStringPosition(int fieldType) {
        StringBuffer nativeSql = new StringBuffer("select ");
        nativeSql.append(getAdvFields(fieldType)).append(" ");
        nativeSql.append("from ad_advert_position aposition ");
        nativeSql.append("where ");
        return nativeSql;
    }


    public static String getAdverByParams(Map<String, Object> param,int fieldType) {
        //查询语句组装
        StringBuffer nativeSql = new StringBuffer();
        if(Constant.position.equals(MapUtils.getString(param,Constant.queryType))) {
            //查询广告位置信息
            String positionType = MapUtils.getString(param, "positionType");
            String mobileType = MapUtils.getString(param, "mobileType");
            String appPackage = MapUtils.getString(param, "appPackage");
            nativeSql = getSqlStringPosition(fieldType);
            nativeSql.append(" aposition.app_package = '").append(appPackage).append("' ");
            nativeSql.append(" and aposition.mobile_type = '").append(mobileType).append("' ");
            if (StringUtils.isNotEmpty(positionType)) {
                nativeSql.append(" and aposition.position_type = '").append(positionType).append("' ");
            }
        } else if(Constant.abTestAdv.equals(MapUtils.getString(param,Constant.queryType))) {
            String positionType = MapUtils.getString(param, "positionType");
            String mobileType = MapUtils.getString(param, "mobileType");
            String channelId = MapUtils.getString(param, "channelId");
            String permission = MapUtils.getString(param, "permission");
            String appPackage = MapUtils.getString(param, "appPackage");
            String lockScreen = MapUtils.getString(param, "lockScreen");
            String positionTypes = MapUtils.getString(param, "positionTypes");
            String game = MapUtils.getString(param, "game");
            String abTestId = MapUtils.getString(param, "abTestId");
            nativeSql = getSqlString(fieldType);
            nativeSql.append(" and flow.test_state = 1 ");
            nativeSql.append(" and test.ab_test_id = ").append(abTestId);
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
            nativeSql.append(" and aposition.app_package = '").append(appPackage).append("' ");
            nativeSql.append(" and aposition.mobile_type = '").append(mobileType).append("' ");
            nativeSql.append(" and if(acode.channel_type=2,locate('").append(channelId + ",").append("', concat(acode.channel_ids, ','))>0,1=1 )");
            nativeSql.append(" and if(acode.channel_type=3,locate('").append(channelId + ",").append("', concat(acode.channel_ids, ','))=0,1=1 )");
        }else if(Constant.flow.equals(MapUtils.getString(param,"queryType"))){
            String appPackage = MapUtils.getString(param, "appPackage");
            String secretkey = MapUtils.getString(param, "secretkey");
            String poskey = MapUtils.getString(param, "poskey");
            String mobileType = MapUtils.getString(param, "mobileType");
            String abTestId = MapUtils.getString(param, "abTestId");
            nativeSql.append("select ");
            nativeSql.append(getAdvFields(fieldType)).append(" ");
            nativeSql.append("from ad_advert_position aposition ");
            nativeSql.append("left join ad_app app on app.package_name = aposition.app_package ");
            nativeSql.append("left join ad_account acc on acc.id = app.account_id ");
            nativeSql.append("LEFT JOIN ad_advert_flow_config flow ON flow.position_id = aposition.id ");
            nativeSql.append("LEFT JOIN ad_advert_test_config test on flow.id = test.flow_id ");
            nativeSql.append("LEFT JOIN ad_test_code_relation rela on test.id = rela.config_id ");
            nativeSql.append("left join ad_advert_code acode on acode.id = rela.code_id ");
            nativeSql.append("where 1 = 1 ");
            nativeSql.append("and aposition.status = ").append(Constant.open).append(" ");
            nativeSql.append("and app.status = ").append(Constant.open).append(" ");
            nativeSql.append("and acc.status = ").append(Constant.open).append(" ");
            nativeSql.append(" and flow.test_state = 1 ");
            nativeSql.append(" and test.ab_test_id = ").append(abTestId);
            //nativeSql.append("and app.dock_type = 1 ");//只允许API接入
            if (StringUtils.isNotEmpty(poskey)) {
                nativeSql.append(" and aposition.position_type = '").append(poskey).append("' ");
            }
            if (StringUtils.isNotEmpty(appPackage)) {
                nativeSql.append(" and app.package_name = '").append(appPackage).append("' ");
            }
            if (StringUtils.isNotEmpty(secretkey)) {
                nativeSql.append(" and app.secret_key = '").append(secretkey).append("' ");
            }
            if (StringUtils.isNotEmpty(mobileType)) {
                nativeSql.append(" and aposition.mobile_type = '").append(mobileType).append("' ");
            }

        }else{
            String appPackage = MapUtils.getString(param, "appPackage");
            String secretkey = MapUtils.getString(param, "secretkey");
            String channelId = MapUtils.getString(param, "channelId");
            String poskey = MapUtils.getString(param, "poskey");
            String mobileType = MapUtils.getString(param, "mobileType");
            String abTestId = MapUtils.getString(param, "abTestId");
            nativeSql.append("select ");
            nativeSql.append(getAdvFields(fieldType)).append(" ");
            nativeSql.append("from ad_advert_position aposition ");
            nativeSql.append("left join ad_app app on app.package_name = aposition.app_package ");
            nativeSql.append("left join ad_account acc on acc.id = app.account_id ");
            nativeSql.append("LEFT JOIN ad_advert_flow_config flow ON flow.position_id = aposition.id ");
            nativeSql.append("LEFT JOIN ad_advert_test_config test on flow.id = test.flow_id ");
            nativeSql.append("LEFT JOIN ad_test_code_relation rela on test.id = rela.config_id ");
            nativeSql.append("left join ad_advert_code acode on acode.id = rela.code_id ");
            nativeSql.append("where 1 = 1 ");
            nativeSql.append("and aposition.status = ").append(Constant.open).append(" ");
            nativeSql.append("and app.status = ").append(Constant.open).append(" ");
            nativeSql.append("and acc.status = ").append(Constant.open).append(" ");
            nativeSql.append(" and flow.type = 1 ");
            nativeSql.append(" and flow.test_state = 0 ");
            //nativeSql.append("and app.dock_type = 1 ");//只允许API接入
            if (StringUtils.isNotEmpty(poskey)) {
                nativeSql.append(" and aposition.position_type = '").append(poskey).append("' ");
            }
            if (StringUtils.isNotEmpty(appPackage)) {
                nativeSql.append(" and app.package_name = '").append(appPackage).append("' ");
            }
            if (StringUtils.isNotEmpty(secretkey)) {
                nativeSql.append(" and app.secret_key = '").append(secretkey).append("' ");
            }
            if (StringUtils.isNotEmpty(mobileType)) {
                nativeSql.append(" and aposition.mobile_type = '").append(mobileType).append("' ");
            }
            nativeSql.append(" and if(acode.channel_type=2,locate('").append(channelId + ",").append("', concat(acode.channel_ids, ','))>0,1=1 )");
            nativeSql.append(" and if(acode.channel_type=3,locate('").append(channelId + ",").append("', concat(acode.channel_ids, ','))=0,1=1 )");
        }
        return nativeSql.toString();
    }


}

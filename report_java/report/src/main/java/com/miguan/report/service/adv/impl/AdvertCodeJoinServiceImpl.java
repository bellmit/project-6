package com.miguan.report.service.adv.impl;

import com.miguan.report.common.constant.CommonConstant;
import com.miguan.report.common.dynamicquery.DynamicQuery4Adv;
import com.miguan.report.dto.AdIdAndNameDto;
import com.miguan.report.dto.AdvertCodeJoinDto;
import com.miguan.report.service.adv.AdvertCodeJoinService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdvertCodeJoinServiceImpl implements AdvertCodeJoinService {

    @Resource
    private DynamicQuery4Adv dynamicQuery4Adv;

    @Override
    public List<AdvertCodeJoinDto> advertCodeQuery(List<Integer> mobileType, List<String> packageName, List<String> totalName, List<Integer> optionValues) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT tmpl.app_package, tmpl.mobile_type, tmpl.total_name, adcc.option_value,aac.ad_id, aac.channel_type, aac.ladder_price ")
                .append("FROM ad_advert_config_code adcc ")
                .append("RIGHT JOIN ( ")
                .append("SELECT tmp.id tmp_id, tmp.app_package, tmp.mobile_type, tmp.total_name,adcf.id adcf_id, adcf.computer ")
                .append("FROM ad_advert_config adcf ")
                .append("RIGHT JOIN ( ")
                .append("SELECT id, app_package, mobile_type, total_name FROM ad_advert_position ");

        if (mobileType != null || !CollectionUtils.isEmpty(totalName) || !CollectionUtils.isEmpty(packageName)) {
            String whereSql = "where ";
            if (mobileType != null) {
                for (int i = 0; i < mobileType.size(); i++) {
                    mobileType.set(i, mobileType.get(i) == 1 ? 2 : 1);
                }
                whereSql = addWhereSqlByParamList(whereSql, "mobile_type", mobileType);
            }

            if (!CollectionUtils.isEmpty(totalName)) {
                if (whereSql.contains("mobile_type")) {
                    whereSql = whereSql + "And ";
                }
                whereSql = addWhereSqlByParamList(whereSql, "total_name", totalName);
            }

            if (!CollectionUtils.isEmpty(packageName)) {
                if (whereSql.contains("mobile_type") || whereSql.contains("total_name")) {
                    whereSql = whereSql + "And ";
                }
                whereSql = addWhereSqlByParamList(whereSql, "app_package", packageName);
            }
            sql.append(whereSql);
        }
        sql.append(") tmp ON adcf.position_id = tmp.id ");
        sql.append("WHERE adcf.state = 1 ")
                .append(") tmpl ON adcc.config_id = tmpl.adcf_id ")
                .append("LEFT JOIN ( ")
                .append("SELECT id, app_package, ad_id, channel_type, ladder_price ")
                .append("FROM ad_advert_code ")
                .append("WHERE put_in = 1 ");
        if (!CollectionUtils.isEmpty(packageName)) {
            String whereSql = addWhereInSqlByParamList("AND ", "app_package", packageName);
            sql.append(whereSql);
        }
        sql.append(") aac ON adcc.code_id = aac.id ");
        if (!CollectionUtils.isEmpty(optionValues)) {
            String whereSql = addWhereSqlByParamList("where ", "adcc.option_value", optionValues);
            sql.append(whereSql);
        }
        sql.append("ORDER BY tmpl.app_package DESC,tmpl.mobile_type ASC,tmpl.total_name ASC,adcc.option_value ASC");
        return dynamicQuery4Adv.nativeQueryList(AdvertCodeJoinDto.class, sql.toString());
    }

    @Override
    public List<AdvertCodeJoinDto> queryTaskData() {
        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT adp.app_package, adp.mobile_type, adp.name, adp.total_name, adco.ad_id, adco.channel_type, adco.ladder_price, adcc.option_value, adcf.computer ")
//           .append("FROM ad_advert_position adp ")
//           .append("LEFT JOIN ad_advert_config adcf ON adp.id = adcf.position_id ")
//           .append("LEFT JOIN ( ")
//           .append(" SELECT c.config_id, c.code_id, c.option_value ")
//           .append(" FROM ad_advert_config_code c ")
//           .append(" WHERE updated_at = ( ")
//           .append("  SELECT max(updated_at) FROM ad_advert_config_code c1 WHERE c.code_id = c1.code_id ")
//           .append(" ) ")
//           .append(") adcc ON adcf.id = adcc.config_id ")
//           .append("LEFT JOIN ad_advert_code adco ON adcc.code_id = adco.id ")
//           .append("WHERE adcf.state = 1 AND adp.total_name IS NOT NULL ")
//           .append("ORDER BY adp.app_package DESC, adp.mobile_type ASC, adcc.option_value ASC");

//        sql.append("SELECT ")
//                .append("adcc.config_id, adcc.option_value, adcc.code_id, ")
//                .append("adc.app_package, adc.ad_id, adc.channel_type, adc.ladder_price, ")
//                .append("adcf.computer, ")
//                .append("adp.mobile_type, adp.total_name, adp.name ")
//                .append("FROM ")
//                .append("ad_advert_config_code adcc ")
//                .append("JOIN ad_advert_code adc ON adcc.code_id = adc.id ")
//                .append("JOIN ad_advert_config adcf ON adcc.config_id = adcf.id ")
//                .append("RIGHT JOIN ad_advert_position adp ON adcf.position_id = adp.id ")
//                .append("WHERE adcf.state = 1 AND adp.total_name IS NOT NULL ");

        sql.append("SELECT    ")
                .append("(case when flow.type=1 then '默认分组' when flow.type=2 and test.type=1 then '对照组' else '测试组' end) as group_name,flow.name as group_exp_name,  ")
                .append("case when flow.type=1 then 3000+order_num when flow.type=2 and test.type=1 then 2000+order_num else 1000+order_num end as sort_value,  ")
                .append("test.ab_test_id,  ")
                .append("if(test.computer=1,rela.matching,rela.order_num) as option_value, rela.code_id,   ")
                .append("adc.app_package, adc.ad_id, adc.channel_type, adc.ladder_price,  ")
                .append("test.computer, adp.mobile_type, adp.total_name, adp.name  ")
                .append("FROM ad_advert_position adp ")
                .append("INNER JOIN ad_advert_flow_config flow ON flow.position_id = adp.id ")
                .append("INNER JOIN ad_advert_test_config test on flow.id = test.flow_id ")
                .append("INNER JOIN ad_test_code_relation rela on test.id = rela.config_id ")
                .append("INNER JOIN ad_advert_code adc ON adc.id = rela.code_id ")
                .append("where  flow.state = 1 and test.state = 1 and rela.state =1  ")
                ;
        return dynamicQuery4Adv.nativeQueryList(AdvertCodeJoinDto.class, sql.toString());
    }

    @Override
    public Map<String, AdIdAndNameDto> queryAdIdAndName(int appType) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT ")
                .append("adc.ad_id, adp.name, adp.total_name ")
                .append("FROM ad_advert_code adc ")
                .append("LEFT JOIN ad_advert_config_code adcc ON adc.id = adcc.code_id ")
                .append("LEFT JOIN ad_advert_config adcf ON adcc.config_id = adcf.id ")
                .append("LEFT JOIN ad_advert_position adp ON adcf.position_id = adp.id ")
                .append("WHERE name IS NOT NULL and adp.total_name is not NULL ");
        if (CommonConstant.VIDEO_APP_TYPE == appType) {
            sql.append("and adp.app_package != 'xld' ");
        } else {
            sql.append("and adp.app_package = 'xld' ");
        }
        List<AdIdAndNameDto> list = dynamicQuery4Adv.nativeQueryList(AdIdAndNameDto.class, sql.toString());

        // 1.获取到的代码位与广告位对应结果，可能存在多对多的关系
        // 2.按照代码位进行分组
        // 3.按照大数据部分析给的，广告位的优先顺序，代码位最终对应的广告位，选取优先级最高
        Map<String, List<AdIdAndNameDto>> groupMapByAdid = list.stream().collect(Collectors.groupingBy(AdIdAndNameDto::getAdId));
        Map<String, AdIdAndNameDto> resultMap = new HashMap<String, AdIdAndNameDto>();
        if (!CollectionUtils.isEmpty(groupMapByAdid)) {

            final String[] videoSort = new String[]{"启动页", "首页列表", "锁屏原生广告", "视频底部banner广告",
                    "视频开始广告", "视频中间广告", "视频结束广告", "首页视频详情", "搜索页广告", "搜索结果广告", "小视频列表",
                    "小视频详情", "激励视频解锁", "详情页底部", "金币弹窗", "金币弹窗翻倍", "补签激励", "看视频赚金币",
                    "小游戏列表信息流广告", "小游戏插屏广告", "小游戏列表插屏广告", "小游戏全屏广告位", "小游戏激励视频广告位"};

            final String[] callSort = new String[]{"启动页", "来电列表", "详情页间隔广告位", "锁屏广告",
                    "试听铃声", "来电分类解锁", "来电详情-退出-激励", "来电详情-解锁", "退出页", "来电详情-弹窗",
                    "来电详情-设置全屏", "通话结束", "来电强制解锁", "设置来电秀成功弹窗", "通话结束关闭弹窗", "来电详情",
                    "立即领取", "铃声设置成功", "铃声分类解锁", "关闭通话结束弹窗", "活动中奖弹窗", "个人来电秀", "小视频列表",
                    "获取华为P40抽奖次数", "锁屏壁纸解锁", "来电详情-退出-全屏", "获取抽奖次数", "签到成功弹窗", "小视频详情"};

            Set<String> adids = groupMapByAdid.keySet();
            for (String adid : adids) {
                List<AdIdAndNameDto> adidNames = groupMapByAdid.get(adid);
                if (adidNames.size() == 1) {
                    resultMap.put(adid, adidNames.get(0));
                } else {
                    if (CommonConstant.VIDEO_APP_TYPE == appType) {
                        resultMap.put(adid, getFirstName(videoSort, adidNames));
                    } else {
                        resultMap.put(adid, getFirstName(callSort, adidNames));
                    }
                }
            }
        }
        return resultMap;
    }

    private AdIdAndNameDto getFirstName(String[] sort, List<AdIdAndNameDto> adidNames) {
        for (AdIdAndNameDto dto : adidNames) {
            for (String name : sort){
                if(name.equals(dto.getTotalName())){
                    return dto;
                }
            }
        }
        return null;
    }

    private String addWhereSqlByParamList(String sql, String cloum, List params) {
        if (!CollectionUtils.isEmpty(params)) {
            if (params.size() == 1) {
                sql = sql + cloum + " = '" + params.get(0).toString() + "' ";
            } else {
                sql = addWhereInSqlByParamList(sql, cloum, params);
            }
        }
        return sql;
    }

    private String addWhereInSqlByParamList(String sql, String cloum, List params) {
        sql = sql + cloum + " in ( ";
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (i == params.size() - 1) {
                sql = addQuotationMark(sql, param);
            } else {
                sql = addQuotationMark(sql, param) + ",";
            }
        }
        sql = sql + ") ";
        return sql;
    }

    private String addQuotationMark(String sql, Object object) {
        if (object instanceof String) {
            sql = sql + "'" + object.toString() + "' ";
        } else {
            sql = sql + object.toString();
        }
        return sql;
    }
}

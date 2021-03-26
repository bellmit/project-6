package com.miguan.advert.common.constants;

import com.cgcg.context.util.StringUtils;
import com.google.common.collect.Maps;

import java.util.Map;

public class TableInfoConstant {
    public static final Map<String, Map<String,String>> TableInfoMap = Maps.newConcurrentMap();
    //table
    public static final String AD_ADVERT_FLOW_CONFIG = "ad_advert_flow_config";
    public static final String AD_ADVERT_TEST_CONFIG = "ad_advert_test_config";
    public static final String AD_TEST_CODE_RELATION = "ad_test_code_relation";
    public static final String AD_ADVERT_CODE = "ad_advert_code";

    //column  name = #{name}, ab_flow_id
    public static final String NAME = "name";
    public static final String AB_FLOW_ID = "ab_flow_id";

    public static final String TEST_STATE = "test_state";
    public static final String TYPE = "type";
    public static final String COMPUTER = "computer";
    public static final String PUT_IN = "put_in";

    //common
    public static final String COMPUTER_COMMON = "算法：1-手动配比，2-手动排序";

    static {
        Map<String,String> adTestConfigTable = Maps.newHashMap();
        adTestConfigTable.put(COMPUTER,COMPUTER_COMMON);
        TableInfoMap.put(AD_ADVERT_TEST_CONFIG,adTestConfigTable);
    }

    public static String getCommon(String common,String tableName,String columnName){
        if(StringUtils.isNotEmpty(common)) return common;
        return getCommon(tableName,columnName);
    }

    public static String getCommon(String tableName,String columnName){
        Map<String, String> tableInfo = TableInfoMap.get(tableName);
        return tableInfo == null ? "" : tableInfo.get(columnName);
    }
}

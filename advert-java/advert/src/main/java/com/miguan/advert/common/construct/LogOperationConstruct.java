package com.miguan.advert.common.construct;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.advert.domain.mapper.AdOperationLogMapper;
import com.miguan.advert.domain.service.TableInfoService;
import com.miguan.advert.domain.vo.TableInfo;
import com.miguan.advert.domain.vo.log.ColumnChange;
import com.miguan.advert.domain.vo.log.ColumnLog;
import com.miguan.advert.domain.vo.result.AdOperationLogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class LogOperationConstruct {

    public static final String PATH_SAVE_SET = "广告管理/广告配置管理/配置/保存配置";
    public static final String PATH_ADD_FLOW_ID = "广告管理/广告配置管理/配置/添加实验id";
    public static final String PATH_ADD_FLOW_GROUP = "广告管理/广告配置管理/配置/添加流量分组";
    public static final String PATH_DELETE_FLOW_GROUP = "广告管理/广告配置管理/配置/删除流量分组";
    public static final String PATH_SET_FLOW_GROUP = "广告管理/广告配置管理/配置/实验组设置";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<ColumnLog> columnLogs ;

    private TableInfoService tableInfoService;
    private AdOperationLogMapper adOperationLogMapper;

    public LogOperationConstruct(TableInfoService tableInfoService, AdOperationLogMapper adOperationLogMapper){
        this.tableInfoService = tableInfoService;
        this.adOperationLogMapper = adOperationLogMapper;
        this.columnLogs = Lists.newArrayList();
    }


    public LogOperationConstruct(TableInfoService tableInfoService, AdOperationLogMapper adOperationLogMapper,List<ColumnLog> columnLogs){
        this.tableInfoService = tableInfoService;
        this.adOperationLogMapper = adOperationLogMapper;
        this.columnLogs = columnLogs;
    }

    /**
     * 日志修改
     */
    public void logUpdate(String tableName,String columnName,String before,String after){
        String common = tableInfoService.findTableColumnCommon(tableName, columnName);
        String ncommon = common == null ? "未备注的列：" + columnName  : common;
        ColumnChange change = new ColumnChange(before,after);
        columnLogs.add(new ColumnLog(tableName + ":" +ncommon,change,columnName));
    }

    /**
     * 日志修改
     */
    public void logUpdateList(String tableName,String columnName,List<?> before,String after){
        String common = tableInfoService.findTableColumnCommon(tableName, columnName);
        String ncommon = common == null ? "未备注的列：" + columnName  : common;
        before.forEach(obj -> {
            try{
                Class clazz = obj.getClass();
                Field field = clazz.getDeclaredField(columnName);
                if(field != null){
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if(value != null && StringUtils.isNotEmpty(value.toString())){
                        //获得参数值
                        String val = value.toString();
                        ColumnChange change = new ColumnChange(val,after);
                        columnLogs.add(new ColumnLog(tableName + ":" +ncommon,change,columnName));
                    }
                }
            } catch (Exception e){
                log.info("in logUpdateList has error" + e.getMessage());
            }
        });
    }

    /**
     * 修改日志 （该功能。仅使用在特定方法上）
     */
    public void logUpdateMap(String tableName, String columnName, List<Map<String, Object>> advertCodeMap, Map<String, String> dataMap) {
        if(CollectionUtils.isEmpty(advertCodeMap)){
            return ;
        }
        String comment = tableInfoService.findTableColumnCommon(tableName,columnName);
        advertCodeMap.forEach(adCode -> {
            Long code = (Long)adCode.get("id");
            String putIn = dataMap.get(code+"");
            columnLogs.add(new ColumnLog(tableName+":"+comment,new ColumnChange(adCode.get("put_in") == null ? "" : adCode.get("put_in").toString(),putIn),columnName));
        });
    }

    /**
     * 添加日志
     */
    public void logInsert(String tableName, Object obj) {
        List<TableInfo> tableInfos = tableInfoService.findTableInfo(tableName);
        if(CollectionUtils.isEmpty(tableInfos)){
            return ;
        }
        tableInfos.forEach(tableInfo -> {
            try{
                String columnName =  tableInfo.getColumnName();
                Class clazz = obj.getClass();
                Field field = clazz.getDeclaredField(columnName);
                if(field != null){
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if(value != null && StringUtils.isNotEmpty(value.toString())){
                        //获得参数值
                        String val = "";
                        if(value instanceof Date){
                            val = sdf.format(value);
                        } else {
                            val = value.toString();
                        }
                        columnLogs.add(new ColumnLog(tableName+":"+tableInfo.getColumnComment(),new ColumnChange("",val),columnName));
                    }
                }
            } catch (Exception e){
                log.info("in logInsertList has error" + e.getMessage());
            }
        });
    }

    public void logDeleteList(String tableName, List<?> before) {
        List<TableInfo> tableInfos = tableInfoService.findTableInfo(tableName);
        if(CollectionUtils.isEmpty(tableInfos)){
            return ;
        }
        before.forEach(obj -> {
            logDelete(tableName, tableInfos, obj);
        });
    }

    public void logDelete(String tableName, Object obj) {
        List<TableInfo> tableInfos = tableInfoService.findTableInfo(tableName);
        if(CollectionUtils.isEmpty(tableInfos)){
            return;
        }
        logDelete(tableName, tableInfos, obj);
    }

    private void logDelete(String tableName, List<TableInfo> tableInfos, Object obj) {
        tableInfos.forEach(tableInfo -> {
            try{
                String columnName =  tableInfo.getColumnName();
                Class clazz = obj.getClass();
                Field field = clazz.getDeclaredField(columnName);
                if(field != null){
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if(value != null && StringUtils.isNotEmpty(value.toString())){
                        //获得参数值
                        String val = "";
                        if(value instanceof Date){
                            val = sdf.format(value);
                        } else {
                            val = value.toString();
                        }
                        columnLogs.add(new ColumnLog(tableName+":"+tableInfo.getColumnComment(),new ColumnChange(val,""),columnName));
                    }
                }
            } catch (Exception e){
                log.info("in logDelete has error" + e.getMessage());
            }
        });
    }


    /**
     * 保存日志
     */
    public void saveLog(String path,String user) {
        saveLog(path,user,3);
    }

    /**
     * 保存日志
     */
    public void saveLog(String path,String user,int type) {
        if(CollectionUtils.isEmpty(columnLogs)){
            return ;
        }
        String content = JSON.toJSON(columnLogs).toString();
        AdOperationLogVo logVo = new AdOperationLogVo();
        logVo.setC_content(content);
        logVo.setOperation_user(user);
        logVo.setPath_name(path);
        logVo.setType(type);
        adOperationLogMapper.add(logVo);
    }

}

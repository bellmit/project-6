package com.miguan.xuanyuan.common.construct;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.miguan.xuanyuan.common.factory.RepositoryFactory;
import com.miguan.xuanyuan.common.log.LogStorage;
import com.miguan.xuanyuan.common.log.pojo.ColumnChange;
import com.miguan.xuanyuan.common.log.pojo.ColumnLog;
import com.miguan.xuanyuan.common.util.ReflectUtil;
import com.miguan.xuanyuan.common.util.StringUtil;
import com.miguan.xuanyuan.service.TableInfoService;
import com.miguan.xuanyuan.vo.common.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class LogConstruct {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String INSERT = "insert";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    public static final String SAVE = "save";
    private static final String UPDATE_BY_ID = "updateById";
    private static final String FIND_BY_ID = "findById";
    private static final String FIND_ALL = "findAll";
    private static final String SELECT_BY_ID = "selectById";
    public static final int DIRE_BEFORE = 1;    //修改前
    public static final int DIRE_AFTER = 2;     //修改后

    private TableInfoService tableInfoService;

    public LogConstruct(TableInfoService tableInfoService){
        this.tableInfoService = tableInfoService;
    }

    /**
     * @Author kangkunhuang
     * @Description save用扫表的方式
     * @Date 2021/3/17
     **/
    public Object findChangeInfo(Object obj, Object[] after,String methodName,String opera,int direct) throws Exception{
        if(methodName != null && methodName.startsWith(INSERT) && direct == DIRE_BEFORE){
            return null;
        } else if (INSERT.equals(methodName)){
            return Lists.newArrayList(after);
        }
        if(UPDATE.equals(methodName) || UPDATE_BY_ID.equals(methodName)){
            return changeById(obj,after,opera,direct);
        } else if(SAVE.equals(opera)){
            return findAllInfo(obj);
        } else {
            //参数
            List<Class> findClazzList = Lists.newArrayList();
            List<Class> selectClazzList = Lists.newArrayList();
            for (int i = 0; i < after.length; i++) {
                findClassList(after, findClazzList, i);
                selectClazzList(after, selectClazzList, i);
            }
            Method method = RepositoryFactory.getMethod(obj.getClass(), methodName.replace(opera,"find"), findClazzList.toArray(new Class[findClazzList.size()]));
            if(method == null){
                method = RepositoryFactory.getMethod(obj.getClass(), methodName.replace(opera,"select"), selectClazzList.toArray(new Class[selectClazzList.size()]));
            }
            if(method == null){
                return null;
            }
            return RepositoryFactory.invoke(method, obj, after);
        }
    }


    private void selectClazzList(Object[] after, List<Class> selectClazzList, int i) {
        if(after[i] instanceof Collection){
            selectClazzList.add(Collection.class);
        } else if (after[i] instanceof Serializable){
            selectClazzList.add(Serializable.class);
        } else {
            selectClazzList.add(after[i].getClass());
        }
    }

    private void findClassList(Object[] after, List<Class> findClazzList, int i) {
        if(after[i] instanceof List){
            findClazzList.add(List.class);
        } else {
            findClazzList.add(after[i].getClass());
        }
    }

    private Object findAllInfo(Object obj) {
        //参数
        List<Object> olist = Lists.newArrayList();
        //参数
        Method method = RepositoryFactory.getMethod(obj.getClass(), FIND_ALL);
        if(method == null){
            return null;
        }
        Object result = RepositoryFactory.invoke(method, obj);
        if(result instanceof Collection){
            olist.addAll((Collection)result);
        } else {
            olist.add(result);
        }
        return olist;
    }

    private Object changeById(Object obj, Object[] after, String opera, int direct) throws Exception {
        //参数
        List<Object> olist = Lists.newArrayList();
        for (int i = 0; i < after.length; i++) {
            Object o = after[i];
            if(o instanceof Collection){
                Collection<Object> oList = (Collection)o;
                oList.forEach(ob -> {
                    try {
                        changeById(obj,ob, olist,opera,direct);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                changeById(obj,o, olist,opera,direct);
            }

        }
        return olist;
    }

    private void changeById(Object obj, Object ob, List<Object> olist, String opera, int direct) throws Exception {
        Object id = ReflectUtil.getValue(ob, "id");
        if(id == null){
            if((INSERT.equals(opera) || SAVE.equals(opera) ) && DIRE_BEFORE == direct){
                return ;
            } else {
                olist.add(ob);
                return ;
            }
        }
        Method method = RepositoryFactory.getMethod(obj.getClass(), FIND_BY_ID, id.getClass());
        if(method == null){
            method = RepositoryFactory.getMethod(obj.getClass(), SELECT_BY_ID, Serializable.class);
        }
        if(method == null){
            return;
        }
        Object result = RepositoryFactory.invoke(method, obj, id);
        if(result instanceof Collection){
            olist.addAll((Collection)result);
        } else {
            olist.add(result);
        }
    }

    public void fillLog(String tableName, Object before, Object after, List<String> ignoreColumns) throws Exception{
        if(StringUtils.isEmpty(tableName)){
            return ;
        }
        List<TableInfo> tableInfos = tableInfoService.findTableInfo(tableName);
        if(CollectionUtils.isEmpty(tableInfos)){
            return ;
        }
        if(before instanceof Collection || after instanceof Collection){
            Collection<Object> beforeList = null;
            Collection<Object> afterList = null;
            if(before instanceof Collection){
                beforeList = (Collection)before;
            }
            if(after instanceof Collection){
                afterList = (Collection)after;
            }
            changeInfoList(tableName,tableInfos,beforeList,afterList,ignoreColumns);
        } else {
            changeInfo(tableName,tableInfos,before,after,ignoreColumns);
        }
    }

    public void changeInfoList(String tableName,List<TableInfo> tableInfos, Collection beforeList, Collection afterList, List<String> ignoreColumns) throws Exception {
        if(CollectionUtils.isEmpty(beforeList) && CollectionUtils.isEmpty(afterList)){
            return ;
        }
        if(CollectionUtils.isEmpty(beforeList)){
            afterList.forEach(after ->{
                try {
                    changeInfo(tableName,tableInfos,null,after,ignoreColumns);
                } catch (Exception e) {
                    return ;
                }
            });
            return ;
        }
        if(CollectionUtils.isEmpty(afterList)){
            beforeList.forEach(before ->{
                try {
                    changeInfo(tableName,tableInfos,before,null,ignoreColumns);
                } catch (Exception e) {
                    return ;
                }
            });
            return ;
        }
        //三种情况  1:before 存在 after 不存在; 2:before 存在 after 存在; 3:before 不存在 after 存在
        List<Object> idList = sameIds(beforeList,afterList);

        //删除
        beforeList.forEach(before ->{
            Object bid = null;
            try {
                bid = ReflectUtil.getValue(before, "id");
                //匹配id
                if(bid == null || !idList.contains(bid)){
                    changeInfo(tableName,tableInfos,before,null,ignoreColumns);
                    return ;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //修改
        if(CollectionUtils.isNotEmpty(idList)){
            idList.forEach(id -> {
                try {
                    Optional beforeOption = beforeList.stream().filter(b -> {
                        try {
                            Object fid = ReflectUtil.getValue(b, "id");
                            if(fid != null && fid.equals(id)){
                                return true;
                            }
                            return false;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }).findFirst();
                    Optional afterOption = afterList.stream().filter(f -> {
                        try {
                            Object fid = ReflectUtil.getValue(f, "id");
                            if(fid != null && fid.equals(id)){
                                return true;
                            }
                            return false;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }).findFirst();
                    Object before = beforeOption.get();
                    Object after = afterOption.get();
                    changeInfo(tableName,tableInfos,before,after,ignoreColumns);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        //新增
        afterList.forEach(after ->{
            Object fid = null;
            try {
                fid = ReflectUtil.getValue(after, "id");
                //匹配id
                if(fid == null || !idList.contains(fid)){
                    changeInfo(tableName,tableInfos,null,after,ignoreColumns);
                    return ;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private List<Object> sameIds(Collection beforeList, Collection afterList) {
        if(CollectionUtils.isEmpty(beforeList) || CollectionUtils.isEmpty(afterList)){
            return new ArrayList<>();
        }
        List<Object> idList = Lists.newArrayList();
        beforeList.forEach(before ->{
            try{
                Object bid = ReflectUtil.getValue(before, "id");
                //匹配id
                if(bid == null){
                    return ;
                }
                Optional first = afterList.stream().filter(after -> {
                    try {
                        Object fid = ReflectUtil.getValue(after, "id");
                        if(fid != null && fid.equals(bid)){
                            return true;
                        }
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }).findFirst();
                if(first.isPresent()){
                    idList.add(bid);
                }
            } catch (Exception e){
                e.printStackTrace();
                return;
            }
        });
        return idList;
    }

    private void changeInfo(String tableName,List<TableInfo> tableInfos, Object before, Object after, List<String> ignoreColumns) throws Exception{
        if(before == null && after == null){
            return ;
        }
        AtomicReference<ColumnLog> idLog = new AtomicReference();
        AtomicInteger flag = new AtomicInteger();
        AtomicInteger index = new AtomicInteger();
        tableInfos.forEach(tableInfo ->{
            try{
                String columeName = StringUtil.lineToHump(tableInfo.getColumnName());
                if(CollectionUtils.isNotEmpty(ignoreColumns) && ignoreColumns.contains(columeName)){
                    return;
                }
                String beforeValue = "";
                String afterValue = "";
                if(before != null){
                    beforeValue = getRefValue(before,columeName);
                }
                if(after != null){
                    afterValue = getRefValue(after,columeName);
                }
                if(beforeValue.equals(afterValue) && !"id".equals(columeName)){
                    return;
                }
                //after == null 是删除 仅保留id
                if(after == null){
                    if("id".equals(columeName)){
                        storageLog(tableName, tableInfo, columeName, beforeValue, afterValue);
                    }
                } else {
                    if("id".equals(columeName)){
                        index.set(LogStorage.getLastIndex());
                        idLog.set(getFirstLog(tableName, tableInfo, columeName, beforeValue, afterValue));
                    } else {
                        flag.set(1);
                        storageLog(tableName, tableInfo, columeName, beforeValue, afterValue);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                return ;
            }
        });
        if(flag.get() == 1 && index != null){
            ColumnLog columnLog = idLog.get();
            storageIndexLog(index.get(),columnLog);
        }

    }
    private ColumnLog getFirstLog(String tableName, TableInfo tableInfo, String columeName, String beforeValue, String afterValue) {
        String ncommon = StringUtils.isEmpty(tableInfo.getColumnComment()) ? columeName  : tableInfo.getColumnComment();
        ColumnChange change = new ColumnChange(beforeValue,afterValue);
        return new ColumnLog(tableName + ":" + ncommon,change,columeName);
    }
    private void storageLog(String tableName, TableInfo tableInfo, String columeName, String beforeValue, String afterValue) {
        String ncommon = StringUtils.isEmpty(tableInfo.getColumnComment()) ? columeName  : tableInfo.getColumnComment();
        ColumnChange change = new ColumnChange(beforeValue,afterValue);
        LogStorage.addInfo(new ColumnLog(tableName + ":" + ncommon,change,columeName));
    }
    private void storageIndexLog(int index,ColumnLog columnLog) {
        LogStorage.addIndexLog(index,columnLog);
    }

    private String getRefValue(Object tobj, String fieldName) throws Exception{
        Object obj = ReflectUtil.getValue(tobj,fieldName);
        if(obj == null){
            return "";
        } else if (obj instanceof Date) {
            return sdf.format(obj);
        } else {
            return obj.toString();
        }
    }
}

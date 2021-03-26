package com.miguan.xuanyuan.common.log;

import com.google.common.collect.Lists;
import com.miguan.xuanyuan.common.log.pojo.ColumnLog;

import java.util.List;

public class LogStorage {
    private static ThreadLocal<List<ColumnLog>> threadLocal = new ThreadLocal<>();

    public static void init(){
        threadLocal.set(Lists.newArrayList());
    }

    public static void addInfo(ColumnLog columnLog){
        List<ColumnLog> columnLogs = getAll();
        columnLogs.add(columnLog);
        threadLocal.set(columnLogs);
    }
    public static int getLastIndex(){
        List<ColumnLog> columnLogs = getAll();
        return columnLogs.size();
    }

    public static List<ColumnLog> getAll(){
        return threadLocal.get();
    }

    public static boolean isEmpty(){
        return threadLocal.get() == null ? true : false;
    }

    public static void close(){
        if(threadLocal == null || threadLocal.get() == null){
            return;
        }
        threadLocal.remove();
    }

    public static void addIndexLog(int index, ColumnLog columnLog) {
        List<ColumnLog> columnLogs = getAll();
        columnLogs.add(index,columnLog);
        threadLocal.set(columnLogs);
    }
}

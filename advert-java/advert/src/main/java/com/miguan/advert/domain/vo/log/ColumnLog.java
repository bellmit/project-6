package com.miguan.advert.domain.vo.log;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ColumnLog {
    private String project;  //业务
    private List<ColumnChange> data = new ArrayList<>();  //数据  前和后
    private String key; //字段名

    public ColumnLog(String project, List<ColumnChange> data, String key) {
        this.project = project;
        this.data = data;
        this.key = key;
    }


    public ColumnLog(String project, ColumnChange change, String key) {
        if(change != null){
            this.data.add(change);
        }
        this.project = project;
        this.key = key;
    }
}

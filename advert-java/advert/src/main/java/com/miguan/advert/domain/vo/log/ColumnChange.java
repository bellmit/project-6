package com.miguan.advert.domain.vo.log;

import lombok.Data;

@Data
public class ColumnChange {
    private String before;
    private String after;

    public ColumnChange() {
    }

    public ColumnChange(String before, String after) {
        this.before = before;
        this.after = after;
    }

    public ColumnChange(Integer before, Integer after) {
        if(before == null){
            this.before = "";
        } else {
            this.before = before.toString();
        }
        if(after == null){
            this.after = "";
        } else {
            this.after = after.toString();
        }
    }

}

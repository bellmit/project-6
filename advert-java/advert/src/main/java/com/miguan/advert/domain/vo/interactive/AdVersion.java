package com.miguan.advert.domain.vo.interactive;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdVersion {
    private Integer id;
    private Integer is_original = 1;
    private String name ;
    private String description;
    private boolean mouseenter = false;
    private boolean inEdit = false;
    private List<Variable> variables = new ArrayList<>();

    public AdVersion() {
    }

    public AdVersion(String name, String description){
        this.name = name;
        this.description = description;
        createDefaultVariable();
    }


    public AdVersion(Integer id,String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
        createDefaultVariable();
    }

    /**
     * 添加默认参数 （该参数在广告里，无用）
     */
    private void createDefaultVariable() {
        variables.add(new Variable("1",2,"ad_exp"));
    }
}

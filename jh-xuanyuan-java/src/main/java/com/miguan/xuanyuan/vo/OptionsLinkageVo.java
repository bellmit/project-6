package com.miguan.xuanyuan.vo;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @Author kangkunhuang
 * @Description 适应前端的联动选择
 * @Date 2021/2/1
 **/
public class OptionsLinkageVo extends OptionsVo {

    private List<OptionsVo> children = Lists.newArrayList();

    public OptionsLinkageVo(String value, String label) {
        super(value, label);
    }

    public List<OptionsVo> getChildren() {
        return children;
    }

    public void setChildren(List<OptionsVo> children) {
        this.children = children;
    }
}

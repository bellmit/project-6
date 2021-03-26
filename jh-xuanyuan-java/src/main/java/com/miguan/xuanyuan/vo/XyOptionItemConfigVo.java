package com.miguan.xuanyuan.vo;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.miguan.xuanyuan.entity.XyOptionItemConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("选项配置项表")
public class XyOptionItemConfigVo {
    @ApiModelProperty("英文标识")
    private String configCode;

    @ApiModelProperty("输入项")
    private List<XyOptionItemConfig> items = Lists.newArrayList();

    public XyOptionItemConfigVo(List<XyOptionItemConfig> items, String codeConfig){
        this.items = items;
        configCode = codeConfig;
    }

    public XyOptionItemConfigVo(List<XyOptionItemConfig> items){
        this.items = items;
        if(CollectionUtils.isNotEmpty(items)){
            configCode = items.get(0).getConfigCode();
        }
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public List<XyOptionItemConfig> getItems() {
        return items;
    }

    public void setItems(List<XyOptionItemConfig> items) {
        this.items = items;
    }
}

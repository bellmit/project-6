package com.miguan.recommend.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "基本参数实体")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseDto {

    @ApiModelProperty(value = "设备标识：distinct_id")
    public String distincId;
    @ApiModelProperty(value = "用户标识：uuid")
    public String uuid;
    @ApiModelProperty(value = "实验分组信息")
    public String abExp;
    @ApiModelProperty(value = "实验隔离域信息")
    public String abIsol;
    @ApiModelProperty(value = "公共请求头对象", hidden = true)
    public PublicInfo publicInfo;
    @ApiModelProperty(value = "用户特征", hidden = true)
    public UserFeature userFeature;
    @ApiModelProperty(value = "是否是ABTest用户", hidden = true)
    public boolean ABTest = false;
    @ApiModelProperty(value = "是否是subABTest用户", hidden = true)
    public boolean subABTest = false;
    @ApiModelProperty(value = "是否是AB渠道", hidden = true)
    public boolean ABChannel = false;
    @ApiModelProperty(value = "是否是AB用户", hidden = true)
    public boolean ABUUid = false;
    @ApiModelProperty(value = "用户首页推荐刷新次数", hidden = true)
    public Long flushNum;
    @ApiModelProperty(value = "用户分类推荐刷新次数", hidden = true)
    public Long catFlushNum;
    @ApiModelProperty(value = "首刷推荐实验组", hidden = true)
    public String firstFlushGroup;
    @ApiModelProperty(value = "渠道一致推荐实验组", hidden = true)
    public String channelConsistencyGroup;
    @ApiModelProperty(value = "cvr分组", hidden = true)
    public String cvrGroup;
    @ApiModelProperty(value = "头部视频推荐逻辑调整分组", hidden = true)
    public Integer isLowAvg;
    @ApiModelProperty(value = "向量分组", hidden = true)
    public String embeddingGroup;
    @ApiModelProperty(value = "98度视频实验分组", hidden = true)
    public boolean video98Group;
    @ApiModelProperty(value = "ctr分组", hidden = true)
    public String ctrGroup;
    @ApiModelProperty(value = "即时相关推荐分组", hidden = true)
    public String relevantGroup;
    @ApiModelProperty(value = "新用户指定内容AB实验分组", hidden = true)
    public String appointVideoGroup;
    @ApiModelProperty(value = "新用户兴趣标签选择实验分组", hidden = true)
    public String interstLabelGroup;
    @ApiModelProperty(value = "专辑推荐实验分组", hidden = true)
    public String specialGroup;
    @ApiModelProperty(value = "老用户优化实验分组", hidden = true)
    public String oldUserOptimizeGroup;
//
//    /**
//     * 新用户前48刷精选视频
//     */
//    public String video48Group;
}

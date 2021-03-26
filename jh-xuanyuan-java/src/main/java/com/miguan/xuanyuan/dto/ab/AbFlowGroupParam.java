package com.miguan.xuanyuan.dto.ab;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.miguan.xuanyuan.common.constant.StrategyGroupConstant;
import com.miguan.xuanyuan.common.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Data
public class AbFlowGroupParam {

    @ApiModelProperty("app的id")
    private Integer app_id;

    @ApiModelProperty("app key")
    private String app_key;

    @ApiModelProperty(value = "app的名字")
    private String app_name;
    @ApiModelProperty(value = "实验标识")
    private String code;
    @ApiModelProperty(value = "app的马甲包 com.mg.xyvideo",required = true)
    private String app_type;
    @ApiModelProperty(value = "需求文档的地址",required = true)
    private String doc_url; //需求文档的地址
    @ApiModelProperty(value = "实验名称",required = true)
    private String name;   //实验名称
    @ApiModelProperty(value = "广告配置的id",required = true)
    private Integer position_id; //广告配置的id
    @ApiModelProperty(value = "广告配置名称",required = true)
    private String position_name; //广告配置的id
    @ApiModelProperty("分层的id")
    private Integer layer_id; //分层的id
    @ApiModelProperty("实验的id,修改时必传")
    private Integer experiment_id; //实验的id
    @ApiModelProperty("分层的name")
    private String layer_name; //分层的name
    @ApiModelProperty("流量配比 (切量比例) [{id:-1,name:对照组,traffic:12},{id:-1,name:实验组,traffic:52},{id:-1,name:实验组,traffic:82}]格式" +
            "=-a")
    private String ratioJson;  //流量配比 (切量比例)
    @ApiModelProperty("流量配比 (切量比例) 这个值不传")
    private List<AbTraffic> ratio;  //流量配比 (切量比例)

    @ApiModelProperty("渠道类型：1-全部 2-仅限 3-排除")
    private Integer channel_type;

    @ApiModelProperty("渠道操作类型")
    private String channelOperation;

    @ApiModelProperty("渠道ids")
    private String channel_ids;

    @ApiModelProperty("作用版本类型")
    private Integer version_type;

    @ApiModelProperty("作用版本操作类型")
    private String versionOperation;

    @ApiModelProperty("作用版本")
    private String version_ids;

    @ApiModelProperty("是否新客 0：全部,1:是,2:否")
    private Integer is_new;
    @ApiModelProperty("状态 : 0：不运行,1:运行")
    private Integer state; //1:开始运行
    @ApiModelProperty("是否使用默认组 ：false:不使用, true :使用")
    private Boolean use_default;//使用默认组的代码位
    @ApiModelProperty("发布时间 yyyy-MM-dd HH:mm:ss")
    private String pub_time;


    @ApiModelProperty("备份状态 : 0：不运行,1:运行")
    private Integer state_bak; //备份状态

    //对象封装
    //层级信息
    @JsonIgnore
    @ApiModelProperty("层级信息")
    private AbLayer abLayer = new AbLayer();
    //实验信息
    @JsonIgnore
    @ApiModelProperty("实验信息")
    private AbExperiment abExperiment = new AbExperiment();
    //流量分组信息
    @JsonIgnore
    @ApiModelProperty("流量分组信息")
    private AbFlowDistribution abFlowDistribution = new AbFlowDistribution();

    /**
     * 初次校验
     * @return
     */
    public String check() {
        //后续添加校验规则
        if(position_id == null){
            return "必须传入对应的广告配置id";
        }
        if(StringUtils.isEmpty(position_name)){
            return "必须传入对应的广告配置名称";
        }
        if(StringUtils.isEmpty(app_type)){
            return "必须传入马甲包";
        }
        if(StringUtils.isEmpty(name)){
            return "必须填写实验名称";
        }
        if(CollectionUtils.isEmpty(ratio) && StringUtils.isEmpty(ratioJson)){
            return "必须选择切量比例";
        }
        return null;
    }


    /**
     * 数据初始化
     * @return
     */
    public AbFlowGroupParam init(){
        if(StringUtils.isEmpty(layer_name)){
            layer_name = app_name + position_name + name;
        }
        if(StringUtils.isNotEmpty(ratioJson)) {
            ratio = JSON.parseArray(ratioJson,AbTraffic.class);
        }
        if(StringUtils.isEmpty(doc_url)){
            doc_url = "无";
        }
        if(use_default == null){
            use_default = true; // 使用默认
        }
        return this;
    }

    /**
     * 构建对象，进行ab操作
     */
    public void buildInfo(){
        buildLayer();
        buildExperiment();
        buildFlowDistribe();
    }

    private void buildLayer() {
        abLayer.setId(layer_id);
        abLayer.setName(layer_name);
    }

    private void buildExperiment() {
        abExperiment.setId(experiment_id);
        abExperiment.setDoc_url(doc_url);
        abExperiment.setName(name);

        //每个实验都要加上马甲包过滤 （给AB用。）
        Condition conditionApp = new Condition("app_package","in",app_type);
        abExperiment.getCondition().add(conditionApp);

        if(is_new != null && (is_new == 1 || is_new == 0)){
            createCondition("and");
            Condition conditions = new Condition("is_new","in", String.valueOf(is_new));
            abExperiment.getCondition().add(conditions);
        }

//        if (StrategyGroupConstant.CHANNEL_TYPE_ONLY == channel_type ){
//            createCondition("and");
//            createCondition("father_channel","in",channel_ids);
//        } else if (StrategyGroupConstant.CHANNEL_TYPE_EX == channel_type ){
//            createCondition("and");
//            createCondition("father_channel","not in",channel_ids);
//        }
//
//        if (StrategyGroupConstant.VERSION_TYPE_ONLY == version_type){
//            createCondition("and");
//            createCondition("app_version","in",version_ids);
//        } else if (StrategyGroupConstant.CHANNEL_TYPE_EX == version_type ){
//            createCondition("and");
//            createCondition("app_version","not in",version_ids);
//        }

        if (Condition.CHANNEL_OPERATION_SET.contains(channelOperation)) {
            createCondition("and");
            createCondition("father_channel",channelOperation, channel_ids);
        }

        if (Condition.VERSION_OPERATION_SET.contains(versionOperation)) {
            createCondition("and");
            createCondition("app_version",versionOperation, version_ids);
        }


        abExperiment.setDescription(name);
        if(StringUtils.isEmpty(code)){ //初始化默认标识
            abExperiment.setCode("ad_exp_"+position_id+"_0");
        } else {
            abExperiment.setCode(code);
        }

        Date now = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(now);
        calendar.add(calendar.DATE,15);

        abExperiment.getPeriod().add(now);
        abExperiment.getPeriod().add(calendar.getTime());
        abExperiment.createDefaultVersion(ratio);
    }

    private void createCondition(String connection) {
        Condition conditions = new Condition(connection);
        abExperiment.getCondition().add(conditions);

    }

    private void createCondition(String key, String operation, String value) {
        Condition conditions = new Condition(key,operation,value);
        abExperiment.getCondition().add(conditions);
    }

    private void buildFlowDistribe() {
        abFlowDistribution.setRatio(ratio);
    }

    public void fillAppId(Integer appId) {
        abLayer.setApp_id(appId);
        abExperiment.setApp_id(appId);
        abFlowDistribution.setApp_id(appId);
        this.app_id = appId;
    }
}

package com.miguan.xuanyuan.dto.ab;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ab实验
 */
@Data
public class AbExperiment {
    private Integer id;
    private String code;    //实验标识
    private String name;    //实验名称
    private List<Date> period = new ArrayList<>();  //发布日期
    private Integer layer_id;   //布局id
    private String position;    //位置
    private List<AdVersion> version_list = new ArrayList<>(); //添加或修改时的版本。
    private List<AdVersion> group_list = new ArrayList<>(); //获取数据时,复制到的版本
    private String exp_version; //版本
    private Integer bucket_type = 2; //存储桶类型  按设备
    private String description = ""; //描述
    private String is_analyze_push; //是否分析推送;0不分析1分析
    private String doc_url; //需求文档的地址
    private Integer app_id; //应用id
    private Integer state; //状态
    private List<Condition> condition = Lists.newArrayList();

    //默认定死的值
    private String isolation_id = ""; //隔离id
    private String depend_experiment_id = ""; //依赖实验id
    private String depend_group_id = ""; //依赖组id


    public void createDefaultVersion(List<AbTraffic> abTraffic){
        for (int i = 1 ; i < abTraffic.size() ; i ++ ) {
            Integer id = abTraffic.get(i).getId();
            String name = abTraffic.get(i).getName();
            if(i == 1){
                if(id == null || id == -1){
                    version_list.add(new AdVersion(name,"对照组的描述"));
                } else {
                    version_list.add(new AdVersion(id,name,"对照组的描述"));
                }
            } else {
                if(id == null || id == -1){
                    version_list.add(new AdVersion(name,name+"的描述"));
                } else {
                    version_list.add(new AdVersion(id,name,name+"的描述"));
                }
            }
        }
    }

    public void incrCode(){
        String seqStr = code.substring(code.lastIndexOf("_")+1);
        int seq = Integer.parseInt(seqStr) + 1;
        this.code = code.substring(0,code.lastIndexOf("_")+1) + seq;
    }
}

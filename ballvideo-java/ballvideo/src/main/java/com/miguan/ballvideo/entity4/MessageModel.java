package com.miguan.ballvideo.entity4;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

/**
 * @author daoyu
 */

@Data
@Entity(name = "message_model")
public class MessageModel extends BaseModel {

    @ApiModelProperty("项目id")
    @Column(name = "project_id")
    private Integer projectId;

    @ApiModelProperty("栏目id")
    @Column(name = "message_dictionary_id")
    private String messageDictionaryId;

    @ApiModelProperty("模板名称")
    @Column(name = "model_name")
    private String modelName;

    @ApiModelProperty("模板关键字")
    @Column(name = "key_word")
    private String keyWord;

    @ApiModelProperty("消息标题")
    @Column(name = "model_title")
    private String modelTitle;

    @ApiModelProperty("消息内容")
    @Column(name = "content")
    private String content;

    @ApiModelProperty("0 关闭 1开启")
    @Column(name = "state")
    private String state;

    @ApiModelProperty("新增时间")
    @Column(name = "created_time")
    private Date createdTime;

    @ApiModelProperty("更新时间")
    @Column(name = "updated_time")
    private Date updatedTime;

}

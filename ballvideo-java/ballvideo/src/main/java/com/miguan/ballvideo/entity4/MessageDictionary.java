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
@Entity(name = "message_dictionary")
public class MessageDictionary extends BaseModel {

    @ApiModelProperty("项目id")
    @Column(name = "project_id")
    private Integer projectId;

    @ApiModelProperty("栏目")
    @Column(name = "column_name")
    private String columnName;

    @ApiModelProperty("英文字段")
    @Column(name = "english_field")
    private String englishField;

    @ApiModelProperty("中文说明")
    @Column(name = "chinese_explain")
    private String chineseExplain;

    @ApiModelProperty("新增时间")
    @Column(name = "created_time")
    private Date createdTime;

    @ApiModelProperty("更新时间")
    @Column(name = "updated_time")
    private Date updatedTime;

}

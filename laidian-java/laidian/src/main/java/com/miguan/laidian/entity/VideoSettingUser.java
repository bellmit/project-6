package com.miguan.laidian.entity;

import com.miguan.laidian.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

@Entity(name = "video_setting_user")
@Data
@ApiModel("来电设置用户记录")
public class VideoSettingUser extends BaseModel {

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("视频Id")
    private Long videoId;

    @ApiModelProperty("设置类型：1来电秀，2锁屏，3壁纸，4微信/QQ皮肤")
    private Integer setType;

    public VideoSettingUser() {
    }

    public VideoSettingUser(Integer setType, Long videoId) {
        this.setType = setType;
        this.videoId = videoId;
    }
}

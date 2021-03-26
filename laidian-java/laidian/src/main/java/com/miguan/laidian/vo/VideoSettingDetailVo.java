package com.miguan.laidian.vo;

import com.miguan.laidian.entity.VideoSettingUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("历史记录详情列表")
public class VideoSettingDetailVo {

    @ApiModelProperty("设置类型：1来电秀，2锁屏，3壁纸，4微信/QQ皮肤")
    private int setType;

    @ApiModelProperty("用户历史记录列表")
    private List<VideoSettingUser> videoSettingUserList;
}

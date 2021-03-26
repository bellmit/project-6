package com.miguan.ballvideo.vo.video;

import com.miguan.ballvideo.entity.UserLabel;
import lombok.Data;

/**
 * 缓存用到
 * @Author shixh
 * @Date 2019/12/2
 **/
@Data
public class UserLabelVo {
    UserLabel userLabel;//用户标签
    String duty;//占比

    public UserLabelVo() {
    }
    public UserLabelVo(UserLabel userLabel, String duty) {
        this.userLabel = userLabel;
        this.duty = duty;
    }
}

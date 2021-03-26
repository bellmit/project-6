package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.entity.ClInterestLabel;

public interface ClInterestLabelMapper {

    /**
     * 保存用户兴趣标签信息
     * @param label
     * @return
     */
    int saveLabelInfo(ClInterestLabel label);
}

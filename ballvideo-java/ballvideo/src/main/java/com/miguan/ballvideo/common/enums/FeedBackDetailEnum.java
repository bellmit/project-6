package com.miguan.ballvideo.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频反馈--反馈明细
 */
public enum FeedBackDetailEnum {
    ContinuousLoading(1,"持续加载"),
    PlayTheCard(2,"播放卡顿"),
    FailedToPlay(3,"播放失败"),
    NoVideoCover(4,"无视频封面"),
    NoPicture(5,"有声音无画面"),
    NoSound(6,"有画面无声音");

    FeedBackDetailEnum(Integer code, String name){
        this.name = name;
        this.code = code;
    }

    /**
     * 返回list列表
     * @return
     */
    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for(FeedBackDetailEnum one : FeedBackDetailEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", one.code);
            map.put("name", one.name);
            list.add(map);
        }
        return list;
    }

    private String name;
    private Integer code;

}

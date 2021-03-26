package com.miguan.ballvideo.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频反馈--反馈类型
 */
public enum FeedBackTypeEnum {
    PlayingProblems(1,"播放问题"),
    TitleWithContent(2,"标题与内容不符"),
    Adver(3,"广告"),
    VulgarPornography(4,"低俗色情"),
    OutdatedNews(5,"过期旧闻"),
    FalseRumors(6,"虚假谣言"),
    IllegalReaction(7,"违法反动"),
    VideoInfringement(8,"视频侵权");

    FeedBackTypeEnum(Integer code, String name){
        this.name = name;
        this.code = code;
    }

    /**
     * 返回list列表
     * @return
     */
    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for(FeedBackTypeEnum one : FeedBackTypeEnum.values()) {
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

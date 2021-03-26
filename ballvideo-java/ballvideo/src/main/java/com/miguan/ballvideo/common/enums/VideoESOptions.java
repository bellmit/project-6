package com.miguan.ballvideo.common.enums;

/** 视频以及集合、专辑的操作标识*/
public enum VideoESOptions {
    /**
     *
     */
    videoAdd("videoAdd"),//视频新增
    videoUpdate("videoUpdate"),//视频更新
    directVideoAdd("directVideoAdd"),//视频直接上线(没有先走预上线流程)
    videoDelete("videoDelete"),//视频删除
    preVideoAdd("preVideoAdd"),//预上线视频新增
    preVideoDelete("videoDelete"),//预上线视频删除
    gatherAddOrDelete("gatherAddOrDelete"),//合集新增/删除视频
    gatherDeleteOrClose("gatherDeleteOrClose"),//合集删除/关闭视频
    deleteDueVideos("deleteDueVideos"),//删除过期视频
    initVideo("initVideo"),//初始化视频
    albumIdUpdate("albumIdUpdate");//更新是否专辑视频标识

    VideoESOptions(String code) {
        this.code = code;
    }

    private String code;

}

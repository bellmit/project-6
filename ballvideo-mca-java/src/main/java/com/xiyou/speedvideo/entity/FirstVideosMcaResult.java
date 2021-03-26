package com.xiyou.speedvideo.entity;

import lombok.Data;

import java.util.Date;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/10/15 6:00 下午
 */
@Data
public class FirstVideosMcaResult {

    /**
     * 待上传白山云
     */
    public static final Integer STATE_WAIT_UPLOAD = 0;

    /**
     * 待提交解析
     */
    public static final Integer STATE_WAIT_APPLY = 1;

    /**
     * 解析中
     */
    public static final Integer STATE_APPLYING = 2;

    /**
     * 解析完成
     */
    public static final Integer STATE_APPLY_COMPLETE = 3;


    private Long id;
    private Integer videoId;
    private String bsyUrl;
    private Integer state;
    private String localPath;
    private Integer speed;
    private Date createAt;
    private Date updateAt;
    private String result;

    public FirstVideosMcaResult() {
    }

    public FirstVideosMcaResult(Integer videoId, String bsyUrl, Integer state, String localPath, Integer speed,Date createAt) {
        this.videoId = videoId;
        this.bsyUrl = bsyUrl;
        this.state = state;
        this.localPath = localPath;
        this.speed = speed;
        this.createAt = createAt;
    }
}

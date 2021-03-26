package com.miguan.ballvideo.springTask.thread;

import com.miguan.ballvideo.common.util.file.AWSUtil4Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 上传视频线程
 */
public class UploadVideoThread implements Runnable {

    private MultipartFile videoFile;

    private String folder;

    private String userId;

    private String type;

    private Date currDate;

    public UploadVideoThread(MultipartFile videoFile, String userId, String folder, String type, Date currDate) {
        this.videoFile = videoFile;
        this.userId = userId;
        this.folder = folder;
        this.type = type;
        this.currDate = currDate;
    }

    @Override
    public void run() {
        //上传图片到白山云
        AWSUtil4Video.upload(videoFile, userId, folder, type, currDate);
    }

}

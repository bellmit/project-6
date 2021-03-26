package com.miguan.laidian.springTask.thread;

import com.miguan.laidian.common.util.file.AWSUtil;
import com.miguan.laidian.entity.Video;
import com.miguan.laidian.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 上传视频线程
 */
public class UploadVideosThread implements Runnable {

    public static final Logger logger = LoggerFactory.getLogger(UploadVideosThread.class);

    private MultipartFile videoFile;

    private VideoService videosService;

    private Video videosVo;

    private String folder;

    private Date currDate;

    private String filePath;

    public UploadVideosThread(MultipartFile videoFile, VideoService videosService, Video videosVo, String folder, Date currDate, String filePath) {
        this.videoFile = videoFile;
        this.videosService = videosService;
        this.videosVo = videosVo;
        this.folder = folder;
        this.currDate = currDate;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        //上传视频到白山云
        AWSUtil.uploadVideos(videoFile, String.valueOf(videosVo.getUserId()), folder, currDate, filePath);
    }

}

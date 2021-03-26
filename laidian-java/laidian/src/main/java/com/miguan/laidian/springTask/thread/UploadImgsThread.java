package com.miguan.laidian.springTask.thread;

import com.miguan.laidian.common.util.file.AWSUtil;
import com.miguan.laidian.entity.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 上传图片线程
 */
public class UploadImgsThread implements Runnable {

    public static final Logger logger = LoggerFactory.getLogger(UploadImgsThread.class);

    private MultipartFile imgFile;

    private Video videosVo;

    private String folder;

    private Date currDate;

    public UploadImgsThread(MultipartFile imgFile, Video videosVo, String folder, Date currDate) {
        this.imgFile = imgFile;
        this.videosVo = videosVo;
        this.folder = folder;
        this.currDate = currDate;
    }

    @Override
    public void run() {
        //上传视频到白山云
        AWSUtil.upload(imgFile, String.valueOf(videosVo.getUserId()), folder, "vedio", currDate);
    }

}

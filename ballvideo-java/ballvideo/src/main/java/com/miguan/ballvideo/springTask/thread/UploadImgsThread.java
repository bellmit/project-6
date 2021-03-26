package com.miguan.ballvideo.springTask.thread;

import com.miguan.ballvideo.common.util.file.AWSUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 上传图片线程
 */
public class UploadImgsThread implements Runnable{

	private MultipartFile imgFile;

	private String folder;

	private String userId;

	private String type;

	private Date currDate;

	public UploadImgsThread(MultipartFile imgFile, String userId, String folder, String type, Date currDate) {
		this.imgFile = imgFile;
		this.userId = userId;
		this.folder = folder;
		this.type = type;
		this.currDate = currDate;
    }

	@Override
	public void run() {
		//上传图片到白山云
		AWSUtil.upload(imgFile, userId, folder, type,currDate);
	}

}

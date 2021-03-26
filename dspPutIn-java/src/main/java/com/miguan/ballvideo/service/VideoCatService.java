package com.miguan.ballvideo.service;

import com.miguan.ballvideo.vo.response.VideoCatRes;

import java.util.List;

/**
 * 视频类型
*/
public interface VideoCatService {

   	 /**
   	  * 查询所有视频类型
   	  * @return
   	  * @throws Exception
   	  */
	 List<VideoCatRes> findAll();
}

package com.miguan.ballvideo.service.impl;


import com.miguan.ballvideo.mapper.AboutUsMapper;
import com.miguan.ballvideo.service.AboutUsService;
import com.miguan.ballvideo.vo.AboutUsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 关于我们ServiceImpl
 * @author xy.chen
 * @date 2019-08-23
 **/

@Service("aboutUsService")
public class AboutUsServiceImpl implements AboutUsService {

	@Resource
	private AboutUsMapper aboutUsMapper;

	@Override
	public List<AboutUsVo>  findAboutUsList(Map<String, Object> params){
		return aboutUsMapper.findAboutUsList(params);
	}


}
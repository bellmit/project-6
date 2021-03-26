package com.miguan.laidian.service.impl;

import com.miguan.laidian.mapper.AboutUsMapper;
import com.miguan.laidian.service.AboutUsService;
import com.miguan.laidian.vo.AboutUsVo;
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
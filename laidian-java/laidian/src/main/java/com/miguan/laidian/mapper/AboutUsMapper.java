package com.miguan.laidian.mapper;

import com.miguan.laidian.vo.AboutUsVo;
import java.util.List;
import java.util.Map;

/**
 * 关于我们Mapper
 * @author xy.chen
 * @date 2019-08-23
 **/

public interface AboutUsMapper{

	/**
	 * 
	 * 通过条件查询关于我们列表
	 * 
	 **/
	List<AboutUsVo>  findAboutUsList(Map<String, Object> params);

}
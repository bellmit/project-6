package com.miguan.ballvideo.dynamicquery;

import com.miguan.ballvideo.vo.AbTestRuleVo;
import com.miguan.ballvideo.vo.AdvertCodeVo;

import java.util.List;
import java.util.Map;

/**
 * JPA动态查询支持SQL查询(98运营广告后台)
 * @author shixh
 * */
public interface Dynamic2Query {

	public void save(Object entity);

	public void update(Object entity);

	public <T> void delete(Class<T> entityClass, Object entityid);

	public <T> void delete(Class<T> entityClass, Object[] entityids);
	
	<T> List<T> nativeQueryList(String nativeSql, Object... params);
	
	<T> List<T> nativeQueryListMap(String nativeSql, Object... params);

	<T> List<T> nativeQueryList(Class<T> resultClass, String nativeSql, Object... params);
	
	Object nativeQueryObject(String nativeSql, Object... params);

	Object[] nativeQueryArray(String nativeSql, Object... params);

	int nativeExecuteUpdate(String nativeSql, Object... params);


	//new
	List<AdvertCodeVo> getAdversWithCache(Map<String, Object> param, int fieldType);

	//获得ab实验数据
	List<AbTestRuleVo> getABTextAdversByRule(Map<String, Object> param, int fieldType);

	Integer queryOpenStatusByAbTestId(Map<String,Object> param);
}

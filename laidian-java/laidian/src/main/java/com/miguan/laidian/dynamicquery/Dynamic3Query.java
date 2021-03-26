package com.miguan.laidian.dynamicquery;

import com.miguan.laidian.vo.AdvertCodeVo;
import com.miguan.laidian.vo.AdvertPositionVo;

import java.util.List;
import java.util.Map;

/**
 * JPA动态查询支持SQL查询(98运营广告后台)
 * @author shixh
 * */
public interface Dynamic3Query {

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

	List<AdvertCodeVo> getAdversWithCache(Map<String, Object> param, int fieldType);

	List<AdvertPositionVo> getAdversPositionInfo(Map<String, Object> param, int fieldType);
}

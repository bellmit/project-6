package com.miguan.advert.config.dynamicquery;

import java.util.List;

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
}

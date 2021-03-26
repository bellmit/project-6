package com.miguan.ballvideo.dynamicquery;

import com.miguan.ballvideo.entity.dsp.AdvDspSqlInfo;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import org.springframework.transaction.annotation.Transactional;
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

	@Transactional(value = "transactionManager3")
	int nativeExecuteUpdate(String nativeSql, Object... params);


	//new
	List<AdvertCodeVo> getAdversWithCache(Map<String, Object> param, int fieldType);


	/**
	 * 根据sql查找对应的投放计划
	 * @param appId
	 * @param code
	 * @param appVersion
	 * @return
	 */
	List<AdvDspSqlInfo> getAdvInfoLstBySqlCache(String appId, String code, String appVersion);
}

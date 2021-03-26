package com.miguan.ballvideo.dynamicquery;

import com.miguan.ballvideo.common.util.adv.AdvSQLUtils;
import com.miguan.ballvideo.dynamicquery.former.MyResultTransformer;
import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * 动态jpql/nativesql查询的实现类（魔方数据库）
 * @author shixh
 */
@Slf4j
@Repository
public class Dynamic2QueryImpl implements Dynamic2Query {

	@PersistenceContext(unitName = "entityManagerFactory2")
	private EntityManager em;

	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public void save(Object entity) {
		em.persist(entity);
	}

	@Override
	public void update(Object entity) {
		em.merge(entity);
	}

	@Override
	public <T> void delete(Class<T> entityClass, Object entityid) {
		delete(entityClass, new Object[] { entityid });
	}

	@Override
	public <T> void delete(Class<T> entityClass, Object[] entityids) {
		for (Object id : entityids) {
			em.remove(em.getReference(entityClass, id));
		}
	}
	private Query createNativeQuery(String sql, Object... params) {
		try{
			Query q = em.createNativeQuery(sql);
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					q.setParameter(i + 1, params[i]);
				}
			}
			return q;
		}catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> nativeQueryList(String nativeSql, Object... params) {
		Query q = createNativeQuery(nativeSql, params);
		q.unwrap(SQLQuery.class).setResultTransformer(Transformers.TO_LIST);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> nativeQueryList(Class<T> resultClass,
									   String nativeSql, Object... params) {
		Query q = createNativeQuery(nativeSql, params);;
		q.unwrap(SQLQuery.class).setResultTransformer(new MyResultTransformer(resultClass));
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> nativeQueryListMap(String nativeSql, Object... params) {
		Query q = createNativeQuery(nativeSql, params);
		q.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return q.getResultList();
	}
	
	@Override
	public Object nativeQueryObject(String nativeSql, Object... params) {
		return createNativeQuery(nativeSql, params).getSingleResult();
	}
	@Override
	public int nativeExecuteUpdate(String nativeSql, Object... params) {
		return createNativeQuery(nativeSql, params).executeUpdate();
	}

	@Override
	public Object[] nativeQueryArray(String nativeSql, Object... params) {
		return (Object[]) createNativeQuery(nativeSql, params).getSingleResult();
	}

  /**
   * 广告查询
   * @param param
   * @param fieldType
   * @return
   */
  @Cacheable(value = CacheConstant.GET_ADVERSWITHCACHE, unless = "#result == null || #result.size()==0")
  @SuppressWarnings("unchecked")
  @Override
  public List<AdvertCodeVo> getAdversWithCache(Map<String, Object> param, int fieldType) {
		Query q = createNativeQuery(AdvSQLUtils.getAdverByParams(param,fieldType), null);
		q.unwrap(SQLQuery.class).setResultTransformer(new MyResultTransformer(AdvertCodeVo.class));
		return q.getResultList();
	}

}

package com.miguan.report.common.dynamicquery.impl;

import com.miguan.report.common.dynamicquery.DynamicQuery4Video;
import com.miguan.report.common.dynamicquery.former.MyResultTransformer;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * 动态jpql/nativesql查询的实现类
 *
 * @author shixh
 */
@Slf4j
@Repository
public class DynamicQuery4VideoImpl implements DynamicQuery4Video {

    @PersistenceContext(unitName = "entityManagerFactory4Video")
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
        delete(entityClass, new Object[]{entityid});
    }

    @Override
    public <T> void delete(Class<T> entityClass, Object[] entityids) {
        for (Object id : entityids) {
            em.remove(em.getReference(entityClass, id));
        }
    }

    private Query createNativeQuery(String sql, Object... params) {
        try {
            Query q = em.createNativeQuery(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    q.setParameter(i + 1, params[i]);
                }
            }
            return q;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public <T> List<T> nativeQueryList(String nativeSql, Object... params) {
        Query q = createNativeQuery(nativeSql, params);
        q.unwrap(SQLQuery.class).setResultTransformer(Transformers.TO_LIST);
        return q.getResultList();
    }

    @Override
    public <T> List<T> nativeQueryList(Class<T> resultClass,
                                       String nativeSql, Object... params) {
        Query q = createNativeQuery(nativeSql, params);
        ;
        q.unwrap(SQLQuery.class).setResultTransformer(new MyResultTransformer(resultClass));
        return q.getResultList();
    }

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

}

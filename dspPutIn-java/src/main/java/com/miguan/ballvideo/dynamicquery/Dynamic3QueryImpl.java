package com.miguan.ballvideo.dynamicquery;

import com.miguan.ballvideo.common.util.adv.AdvSQLUtils;
import com.miguan.ballvideo.dynamicquery.former.MyResultTransformer;
import com.miguan.ballvideo.entity.dsp.AdvDspSqlInfo;
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
public class Dynamic3QueryImpl implements Dynamic3Query {

	@PersistenceContext(unitName = "entityManagerFactory3")
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

	/**
	 * 根据sql查找对应的投放计划
	 * @param appId
	 * @param code
	 * @param appVersion
	 * @return
	 */
//	@Cacheable(value = CacheConstant.GET_ADV_INFO_LST_BY_SQL, unless = "#result == null || #result.size()==0")
	@SuppressWarnings("unchecked")
	@Override
	public List<AdvDspSqlInfo> getAdvInfoLstBySqlCache(String appId, String code, String appVersion){
		StringBuffer sqlPutStr = new StringBuffer("SELECT ");
		sqlPutStr.append(" DISTINCT plan.id plan_id, des.id design_id, cd.code_id code_id, usr.id usr_id, plan.cat_ids,plan.cat_type catType, ");
		sqlPutStr.append(" plan.price, plan.smooth_date,plan.start_date plan_start_date, plan.end_date plan_end_date, plan.put_in_type, app.id app_id, ");
		sqlPutStr.append(" plan.times_config, plan.time_setting, ");
		sqlPutStr.append(" des.name des_name, des.material_type, des.copy, des.button_copy, des.is_show_logo_product,des.logo_url,des.product_name, ");
		sqlPutStr.append(" des.put_in_method, des.put_in_value, dw.weight, des.material_url, ");
		sqlPutStr.append(" usr.name usr_name, usr.link_man, ");
		sqlPutStr.append(" cd.style_size, pr.position_key position_type, ");
		sqlPutStr.append(" plan.day_price, plan.total_price, ");
		//sqlPutStr.append(" acc.id acc_id, acc.remain_day_price, acc.remain_total_price, ");
		sqlPutStr.append(" phone.brand dev_name, phone.type dev_type, ");
		sqlPutStr.append(" area.area area_name, area.type area_type ");
		sqlPutStr.append(" FROM idea_advert_plan plan ");
//		sqlPutStr.append(" LEFT JOIN idea_advert_app app on app.id = plan.app_id ");
		sqlPutStr.append(" LEFT JOIN idea_advert_user usr on usr.id = plan.advert_user_id ");
		sqlPutStr.append(" LEFT JOIN idea_advert_area area on area.plan_id = plan.id ");
		sqlPutStr.append(" LEFT JOIN idea_advert_phone phone on phone.plan_id = plan.id ");
		//sqlPutStr.append(" LEFT JOIN idea_advert_account acc on acc.plan_id = plan.id ");
		sqlPutStr.append(" LEFT JOIN idea_adv_des_weight dw on dw.plan_id = plan.id ");
		sqlPutStr.append(" LEFT JOIN idea_advert_design des on des.id = dw.design_id  ");
		//sqlPutStr.append(" LEFT JOIN idea_code_design_relation dr on dr.design_id = des.id ");
		sqlPutStr.append(" LEFT JOIN idea_plan_code_relation pc on pc.plan_id = plan.id ");
		sqlPutStr.append(" LEFT JOIN idea_advert_code cd on cd.id = pc.code_id ");
		sqlPutStr.append(" LEFT JOIN idea_advert_app app on app.id = cd.app_id ");
//		sqlPutStr.append(" LEFT JOIN idea_advert_code cd1 ON cd.id = pc.code_id ");
		sqlPutStr.append(" LEFT JOIN idea_adv_position_relation pr on pr.id = cd.rela_id  ");
		sqlPutStr.append(" where plan.state = 1 and app.state = 1 and cd.state = 1 and des.state = 1 ");
		//判断应用id与代码位id是否匹配
		sqlPutStr.append(" and app.id = '").append(appId).append("' ").append(" and cd.code_id = '").append(code).append("' ");
		//素材下的代码位
//		sqlPutStr.append(" and cd1.code_id = '").append(code).append("' ");
		//素材规格
		//sqlPutStr.append(" and cd.style_size = ? ");
		//应用版本号
//		sqlPutStr.append(" and concat(lpad(SUBSTRING_INDEX('").append(appVersion).append("','.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX('").append(appVersion).append("','.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX('").append(appVersion).append("','.',-1), 3, '0')) + 0");
//		sqlPutStr.append(" BETWEEN ");
//		sqlPutStr.append(" concat(lpad(SUBSTRING_INDEX(plan.version1,'.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(plan.version1,'.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX(plan.version1,'.',-1), 3, '0')) + 0");
//		sqlPutStr.append(" AND ");
//		sqlPutStr.append(" concat(lpad(SUBSTRING_INDEX(plan.version2,'.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(plan.version2,'.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX(plan.version2,'.',-1), 3, '0')) + 0");
		//地域、投放计划时间段 单独过滤
		//sqlPutStr.append(" and now() BETWEEN plan.start_date and plan.end_date ");
		sqlPutStr.append(" order by plan.id  ");

		Query q = createNativeQuery(sqlPutStr.toString(), null);
		q.unwrap(SQLQuery.class).setResultTransformer(new MyResultTransformer(AdvDspSqlInfo.class));
		return q.getResultList();
	}

}

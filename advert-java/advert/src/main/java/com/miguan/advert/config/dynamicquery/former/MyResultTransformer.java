package com.miguan.advert.config.dynamicquery.former;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.Validate;
import org.hibernate.HibernateException;
import org.hibernate.transform.ResultTransformer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 修正返回自定义pojo类型时找不到属性的BUG
 * @author shixh
 */
public class MyResultTransformer implements ResultTransformer {

    private static final long serialVersionUID = -3779317531110592988L;

    private final Class<?> resultClass;
    private Field[] fields;
    private BeanUtilsBean beanUtilsBean;

    public MyResultTransformer(final Class<?> resultClass) {
        this.resultClass = resultClass;
//        this.fields = this.resultClass.getDeclaredFields();
        this.fields = getAllFields(resultClass);
        beanUtilsBean=BeanUtilsBean.getInstance();
    }

    /**
     * 获取全部属性
     * @param cls
     * @return
     */
    public static Field[] getAllFields(final Class<?> cls) {
        Validate.isTrue(cls != null, "The class must not be null");
        final List<Field> allFields = new ArrayList<Field>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            for (final Field field : declaredFields) {
                allFields.add(field);
            }
            currentClass = currentClass.getSuperclass();
        }
        return allFields.toArray(new Field[allFields.size()]);
    }

    /**
     * @param tuple 
     * @param aliases 别名
     */
    public Object transformTuple(final Object[] tuple, final String[] aliases) {
        Object result;
		try {
			result = this.resultClass.newInstance();
			for (int i = 0; i < aliases.length; i++) {
				for (Field field : this.fields) {
					String fieldName = field.getName();
					if (fieldName.equalsIgnoreCase(aliases[i].replaceAll("_", ""))) {
                        if(tuple[i] != null ){
                            beanUtilsBean.setProperty(result, fieldName, tuple[i]);
                        }
						break;
					}
				}
			}
		} catch (Exception e) {
			throw new HibernateException("Could not instantiate resultclass: " + this.resultClass.getName(), e);
		}
        return result;
    }

    @SuppressWarnings("rawtypes")
    public List transformList(final List collection) {
        return collection;
    }
}
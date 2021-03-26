/**
 * 
 */
package com.miguan.advert.common.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 普通工具类
 * 
 * @author suhj
 * 
 */
public class Utilities {
	final static Logger log = LoggerFactory.getLogger(Utilities.class);


	/**
	 * 从JSON对象中取得String对象
	 * 
	 * @param json
	 * @param key
	 */
	public static String getString(JSONObject json, String key) {
		if (json == null || !json.containsKey(key)) {// json对象为空或者不包含该key
			return null;
		}

		try {// 尝试取得值
			return json.getString(key);
		} catch (Exception e) {
			return null;
		}
	}
	
	/***

    
    /**
     * 这样做的前提是对象以及对象内部所有的引用到的对象都是可串行化的。
     * 否则，就需要仔细考察那些不可串行化的对象可否设成transient，从而将之排除在复制过程之外
     * 如下为深度复制源代码
     * @param from
     * @return
     * @since suhj, 20181207, 新增方法
     */
    public static Object  deepClone(Object from){
    	Object obj = null;
		ByteArrayOutputStream bo = null;
		ObjectOutputStream oo = null;
		ByteArrayInputStream bi = null;
		ObjectInputStream oi = null;
    	try {
    		bo = new ByteArrayOutputStream();
    		oo = new ObjectOutputStream(bo);
        	oo.writeObject(from);
        	bi = new ByteArrayInputStream(bo.toByteArray());
        	oi = new ObjectInputStream(bi);
        	obj = oi.readObject();
		} catch (OptionalDataException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				if(oi != null){
					oi.close();
				}
				if(bi != null){
					bi.close();
				}
				if(oo != null){
					oo.close();
				}
				if(bi != null){
					bo.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
    	return obj;
    }


	/**
	 * 实体类转换成map
	 * @param bean
	 * @return
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Map<String,Object> convertBeanToMap(Object bean) throws IntrospectionException,IllegalAccessException, InvocationTargetException {
		Class type = bean.getClass();
		Map<String,Object> returnMap = new HashMap<String, Object>();
		BeanInfo beanInfo = Introspector.getBeanInfo(type);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					returnMap.put(propertyName, result);
				} else {
					returnMap.put(propertyName, "");
				}
			}
		}
		return returnMap;
	}
}

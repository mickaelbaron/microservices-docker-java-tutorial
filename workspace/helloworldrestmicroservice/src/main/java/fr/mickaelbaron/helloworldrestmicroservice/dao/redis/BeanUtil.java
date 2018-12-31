package fr.mickaelbaron.helloworldrestmicroservice.dao.redis;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * @author Mickael BARON (baron.mickael@gmail.com)
 *
 * Based on: https://github.com/fjunior87/JedisCrud/blob/master/src/com/xicojunior/jediscrud/util/BeanUtil.java
 */
public class BeanUtil {

	public static Map<String, String> toMap(Object object) {
		Map<String, String> properties = new HashMap<String, String>();
		try {
			properties = BeanUtilsBean.getInstance().describe(object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return properties;
	}

	public static <T extends Object> T populate(Map<String, String> properties, T object) {
		try {
			BeanUtilsBean.getInstance().populate(object, properties);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return object;
	}

}
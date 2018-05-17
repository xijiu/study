package com.lkn;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.junit.Test;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author likangning
 * @since 2018/4/16 下午2:37
 */
public class CommonTools {

	@Test
	public void aaaa() {
		Map<String, Object> map = new CaseInsensitiveMap();
		map.put("id", 20L);
		map.put("name", "likangning");
		map.put("userrole1", "role");


		User user = new User();
		injectFromMap(user, map);
		System.out.println(user);
	}

	public static <T> void injectFromMap(T t, Map<String, Object> map) {
		if (t != null && map != null) {
			Class<?> clazz = t.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				if (map.containsKey(fieldName)) {
					field.setAccessible(true);
					try {
						field.set(t, map.get(fieldName));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Test
	public void transTest() {
		long time = System.currentTimeMillis();
		time = 1523548800000L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date(time);
		String format = sdf.format(date);
		System.out.println(format);
	}


	@Test
	public void transTest123() {
		String s = "=";
		System.out.println(s.toLowerCase());
	}
}

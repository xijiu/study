package com.lkn.spring.tools;

import com.lkn.spring.tools.bean.User;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import java.util.Map;


/**
 * 对象操作的工具类
 * @author likangning
 * @since 2018/6/21 上午11:01
 */
public class BeanToolsTest {

	private User user = new User(1L, "likangning", false);

	/**
	 * 通过工具类直接获取属性内容
	 */
	@Test
	public void getPropertyTest() throws Exception {
		// 注：如果该实体类是内部类，或者不被public修饰，那么将会扔出异常
		Object name = PropertyUtils.getProperty(user, "name");
		System.out.println(name.getClass().getName());
		System.out.println(name);

		PropertyUtils.setProperty(user, "name", "李康宁");
		System.out.println(user);
	}

	/**
	 * 将一个实体bean转换为map
	 */
	@Test
	public void beanToMapTest() {
		Map beanMap = new BeanMap(user);
		System.out.println(beanMap);
	}
}






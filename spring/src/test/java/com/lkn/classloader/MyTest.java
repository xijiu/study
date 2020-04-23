package com.lkn.classloader;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author likangning
 * @since 2020/4/22 下午3:43
 */
public class MyTest {
	@Getter
	@Setter
	public static class A {
		private Long id;
		private String name;
	}

	@Getter
	@Setter
	public static class B {
		private Long id;
		private String name;
	}

	public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
		A a = new A();
		a.id = 1L;
		a.name = "likangning";
		B b = new B();
		BeanUtils.copyProperties(b, a);
		System.out.println(b.name);
	}
}

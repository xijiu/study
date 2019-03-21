package com.lkn.classloader;

import org.junit.Test;

import java.net.URLClassLoader;

/**
 * @author likangning
 * @since 2019/3/19 上午10:36
 */
public class SimpleTest {

	@Test
	public void test() {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		System.out.println(classLoader);
		System.out.println(classLoader.getParent());
		System.out.println(SimpleTest.class.getClassLoader());
		System.out.println(SimpleTest.class.getClassLoader() instanceof URLClassLoader);
		// 系统默认的类加载器是 App类加载器 ---- sun.misc.Launcher$AppClassLoader@70dea4e
		System.out.println(ClassLoader.getSystemClassLoader());
	}
}

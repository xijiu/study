package com.lkn.classloader;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author likangning
 * @since 2019/1/7 下午4:58
 */
public class SimpleClassloaderTest {



	@Test
	public void classLoaderTest() {
		Splitter splitter = Splitter.on(":");
		String properties = System.getProperty("java.ext.dirs");
		List<String> list = splitter.splitToList(properties);
		System.out.println("ext dirs is :");
		list.forEach(System.out::println);


		System.out.println("\ncp dirs is :");
		String cps = System.getProperty("java.class.path");
		List<String> list2 = splitter.splitToList(cps);
		list2.forEach(System.out::println);

		System.out.println(SimpleClassloaderTest.class.getClassLoader());
	}

	@Test
	public void classLoaderTest2() throws Exception {
		MyClassLoader myClassLoader = new MyClassLoader();
		Class<?> aClass = myClassLoader.loadClass("com.lkn.classloader.SimpleClassloaderTest");
		Assert.assertTrue(aClass != null);

		Class<?> bClass = myClassLoader.loadClass("com.lkn.AppTest");
		Assert.assertTrue(bClass != null);
		System.out.println(bClass);
		Lists.newArrayList(bClass.getDeclaredMethods()).forEach(System.out::println);
	}

	@Test(expected = ClassNotFoundException.class)
	public void classLoaderTest3() throws Exception {
		Class<?> cClass = SimpleClassloaderTest.class.getClassLoader().loadClass("com.lkn.AppTest");
		System.out.println(cClass);
		Assert.assertTrue(cClass == null);
//		URLClassLoader
	}

	@Test
	public void abc() {
		try {
			throw new RuntimeException("aaa");
		}	catch (RuntimeException e1) {
			System.out.println(2);
		} catch (Exception e) {
			System.out.println(1);
		}
	}

}

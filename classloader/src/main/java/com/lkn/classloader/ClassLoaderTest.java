package com.lkn.classloader;

import com.lkn.classloader.classloader.ExtLevelClassLoader;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * main方法会被重复加载执行
 * 因为每次都会新建一个ExtLevelClassLoader类加载器，然后用此新建的类加载器加载ClassLoaderTest类，从而重新执行main方法
 * 由此可见类加载器的作用
 * @author likangning
 * @since 2019/3/19 下午5:20
 */
public class ClassLoaderTest {
	private static boolean flag = false;

	public static void main(String[] args) {
		ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
		System.out.println(classLoader);
		URL[] urLs = null;
		if (classLoader instanceof URLClassLoader) {
			urLs = ((URLClassLoader) classLoader).getURLs();
		}
		ExtLevelClassLoader extLevelClassLoader = new ExtLevelClassLoader(urLs);
		Thread.currentThread().setContextClassLoader(extLevelClassLoader);
		if (!flag) {
			flag = true;
			Thread thread = new Thread(() -> {
				try {
					Class<?> startClass = extLevelClassLoader.loadClass(ClassLoaderTest.class.getName());
					Method mainMethod = startClass.getMethod("main", String[].class);
					mainMethod.invoke(null, new Object[]{args});
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			thread.setContextClassLoader(extLevelClassLoader);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

package com.lkn.classloader.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * app层级的类加载器
 *
 * @author likangning
 * @since 2019/3/19 下午2:59
 */
public class AppLevelClassLoader extends URLClassLoader {

	public AppLevelClassLoader(URL[] urls) {
		super(urls, ClassLoader.getSystemClassLoader().getParent());
	}
}

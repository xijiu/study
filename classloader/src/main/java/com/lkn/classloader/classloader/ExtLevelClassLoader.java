package com.lkn.classloader.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * ext层级的类加载器
 *
 * @author likangning
 * @since 2019/3/19 下午2:59
 */
public class ExtLevelClassLoader extends URLClassLoader {

	public ExtLevelClassLoader(URL[] urls) {
		super(urls, null);
	}
}

package com.lkn.classloader;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author likangning
 * @since 2019/1/7 下午5:15
 */
public class MyClassLoader extends ClassLoader {
	private static final String url = "file:///Users/likangning/logs/myclasspath/";

	@Override
	public Class<?> findClass(String originName) {
		String name = originName;
		name = name.replace(".", "/");
		name = url + name + ".class";
		System.out.println(name);
		byte[] cLassBytes = null;
		Path path ;
		try {
			path = Paths.get(new URI(name));
			cLassBytes = Files.readAllBytes(path);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return defineClass(originName, cLassBytes, 0, cLassBytes.length);
	}

	@Test
	public void abc() {
		String abc = "1112345678";
		byte[] bytes = abc.getBytes();
		System.out.println(abc.toCharArray().length);
		System.out.println(bytes.length);

	}

}

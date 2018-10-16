package com.lkn;


import org.junit.Test;

import java.io.*;
import java.util.Properties;

/**
 * Unit test for simple App.
 */
public class AppTest {

	@Test
	public void test() {
		Properties properties = readProperties("system.properties");
		System.out.println(properties.getProperty("a"));
	}

	public static Properties readProperties(String proName) {
		Properties props = new Properties();
		String url = new AppTest().getClass().getClassLoader().getResource(
				proName).toString().substring(6);
		String empUrl = url.replace("%20", " ");// 如果你的文件路径中包含空格，是必定会报错的
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(empUrl));
			props.load(in);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return props;
	}
}

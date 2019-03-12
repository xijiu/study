package com.lkn.polardb.game;

import org.junit.Test;

import java.io.*;
import java.util.Arrays;

/**
 * @author likangning
 * @since 2019/1/16 下午4:28
 */
public class SimpleTest {

	private char[] items = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};


	@Test
	public void test() throws IOException {
		File DB_FILE = new File(System.getProperty("user.dir") + "/data.db");
		DB_FILE.createNewFile();
	}

	@Test
	public void test2() throws Exception {
		File file = new File("/Users/likangning/myTest/abc.txt");
		FileOutputStream outputStream = new FileOutputStream(file, false);
		Integer a = 1;
		outputStream.write("1234567890".getBytes());
	}

	@Test
	public void test3() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4096; i++) {
			sb.append("a");
		}
		System.out.println(sb.toString());
		String s = sb.toString();
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			byte[] bytes = s.getBytes();
		}
		long end = System.currentTimeMillis();
		System.out.println("耗时： " + (end - begin));
	}

	@Test
	public void test4() throws Exception {
		String str = "12345李康宁";
		Integer max = Integer.MAX_VALUE;
		System.out.println(str);
		System.out.println(str.getBytes().length);
		System.out.println(str.getBytes("unicode").length);
	}

	@Test
	public void b() {

		System.out.println(Math.ceil(2.5));
		System.out.println(Math.ceil(2.5) - 1);
//		int num = 1000000;
//		StringBuilder sb = new StringBuilder();
//		calc(num, sb);
//		System.out.println(sb.toString());
	}

	private void calc(int num, StringBuilder sb) {
		if (num < items.length) {
			sb.append(items[num]);
		} else {
			int originLevel = num / items.length - 1;
			int level = originLevel;
			while (level >= items.length) {
				level = level / items.length - 1;
			}
			sb.append(items[level]);
			calc(num - 26 * num / items.length, sb);
		}
	}

}

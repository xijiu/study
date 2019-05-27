package com.lkn;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.junit.Test;
import sun.security.provider.MD5;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Collections;

/**
 * Unit test for simple App.
 */
public class AppTest {
	@Test
	public void shouldAnswerWithTrue() {
		BigDecimal divide = BigDecimal.valueOf(100).divide(BigDecimal.valueOf(0), 2, RoundingMode.HALF_UP);

		System.out.println(divide);
	}

	@Test
	public void test() {
		String column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "Spring__Test");
		System.out.println(column);
	}

	private boolean isCollectionNullOrEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	@Test
	public void bbb() {
		String result = tryTest();
		System.out.println(result);
	}

	private String tryTest() {
		try {
			System.out.println("inner");
			int a = 1 / 0;
			return "123";
		} finally {
			System.out.println("i am finally");
		}
	}

	@Test
	public void ranTest() {
		System.out.println((long) (Math.random() * 100));
	}

	@Test
	public void test2() {
		int availProcessors = Runtime.getRuntime().availableProcessors();
		System.out.println("avail processors count: " + availProcessors);
	}

	@Test
	public void test3() {
		int a = 1;
		int b = 1;

		Integer a1 = 1;
		Integer b1 = 1;

		Integer a2 = new Integer(127);
		Integer b2 = new Integer(127);

		Integer a3 = new Integer(128);
		Integer b3 = new Integer(128);

		Integer a4 = 127;
		Integer b4 = 127;

		Integer a5 = 128;
		Integer b5 = 128;

		Integer a6 = new Integer(1);

		System.out.println("(a == b) -> " + (a == b));
		System.out.println("(a1 == b1) -> " + (a1 == b1));
		System.out.println("(a2 == b2) -> " + (a2 == b2));
		System.out.println("(a3 == b3) -> " + (a3 == b3));
		System.out.println("(a4 == b4) -> " + (a4 == b4));
		System.out.println("(a4 == b4) -> " + (a5 == b5));
		System.out.println("(a4 == a2) -> " + (a4 == a2));
		System.out.println("(a1 == a6) -> " + (a1 == a6));
	}

	@Test
	public void fileMD5Test() {
		File file = new File("/Users/likangning/test/b.log");
		String fileMD5 = getFileMD5(file);
		// 485f226d586ace8d2154b21ca25ff549
		// 485f226d586ace8d2154b21ca25ff549
		System.out.println(fileMD5);

	}

	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}

	@Test
	public void test23() {
		UserService userService = new UserServiceImpl();
		Field[] fields = userService.getClass().getDeclaredFields();
		for (Field field : fields) {
			System.out.println(field.getName());
			System.out.println(field.getType().getName());
		}
	}

	@Test
	public void abc() {
		System.out.println(207325176 % 1024);
		System.out.println(207334392 % 1024);
		System.out.println(207337464 % 1024);
		System.out.println(Long.valueOf(null));
	}

}

package com.lkn;


import org.junit.Test;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

	@Test
	public void ccc() throws InterruptedException {
		new Thread(this::abc1).start();
		Thread.sleep(1000);
		new Thread(this::abc2).start();
		Thread.sleep(5000);
	}

	public synchronized void abc1() {
		System.out.println("我是方法 abc1");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void abc2() {
		System.out.println("我是方法 abc2");
	}

	@Test
	public void aaaa() {
		int tps = 3510;
		int maxThreadNum = 200;
		int activeThreadNum = 195;
		Double maxDpsDouble = (double)tps / ((double)1000 / 1000D) * ((double)maxThreadNum / (double)activeThreadNum);
		int maxTps = maxDpsDouble.intValue();
		System.out.println(maxTps);
	}


	private ExecutorService exec = Executors.newFixedThreadPool(3);

	@Test
	public void test2() throws InterruptedException {
		int size = 100;
		for (int i = 0; i < size; i++) {
			Thread thread = new Thread(() -> {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			exec.submit(thread);
		}

		exec.shutdown();
		exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

		System.out.println("over");
	}

	private static final SortedSet<Integer> MSG_SET = Collections.synchronizedSortedSet(new TreeSet<Integer>());

	@Test
	public void aaa() {
		test4();
		test4();
		test4();

		test3();
		test3();
		test3();
	}

	/**
	 * 13414
	 * 13388
	 */
	@Test
	public void test3() {
		long begin = System.currentTimeMillis();
		Set<Integer> set = new TreeSet<>();
		for (int i = 0; i < 20000000; i++) {
			set.add(i);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}

	@Test
	public void test4() {
		long begin = System.currentTimeMillis();
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 20000000; i++) {
			list.add(i);
		}
		long end1 = System.currentTimeMillis();
		System.out.println("拼接list耗时： " + (end1 - begin));
		Set<Integer> set = new TreeSet<>(list);
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}

	@Test
	public void test5() {
		int size = 200000000;
		long val = Long.MAX_VALUE / 2;
		long begin = System.currentTimeMillis();
		BigDecimal bigDecimal = new BigDecimal(0);
		for (int i = 0; i < size; i++) {
			bigDecimal = bigDecimal.add(new BigDecimal(val));
		}
		System.out.println(bigDecimal.divide(new BigDecimal(size)));
		long end = System.currentTimeMillis();
		System.out.println("耗时： " + (end - begin));
	}

	@Test
	public void test6() {
		long val = Long.MAX_VALUE / 2;
		long begin = System.currentTimeMillis();
		int size = 100000000;
		long avg = 0;
		for (int i = 0; i < size; i++) {
			avg += (val - avg) / (i + 1);
		}
		System.out.println(avg);
		long end = System.currentTimeMillis();
		System.out.println("耗时： " + (end - begin));
	}

	@Test
	public void test7() {
		int[] num = {2, 2, 2, 2, 4, 4, 4, 8, 8};
		double avg = 0D;
		for (int i = 0; i < num.length; i++) {
			avg += (num[i] - avg) / (i + 1);
			System.out.println(avg);
		}
		System.out.println(avg);
	}

	@Test
	public void test8() {
		int sum = 9;
		Double avg = 0D;
		avg += 2 * ((float)4 / sum);
		avg += 4 * ((float)3 / sum);
		avg += 8 * ((float)2 / sum);
		System.out.println(avg);
	}

	@Test
	public void bbbbbb() {
		int small = 200;
		int medium = 450;
		int large = 650;
		double excess = small * 0.3;
		System.out.println(small * 0.7);
		System.out.println(medium + excess * ((double)medium / (medium + large)));
		System.out.println(large + excess * ((double)large / (medium + large)));
	}

	public static List<String> divide(String name, Integer id) {
		return null;
	}

	// List divide(String, Integer)

	@Test
	public void udfTest() {
		StringBuilder sb = new StringBuilder();
		Method[] methods = AppTest.class.getMethods();
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase("divide")) {
				sb.append(method.getReturnType().getSimpleName()).append(" ").append(method.getName());
				sb.append("(");
				Class<?>[] parameterTypes = method.getParameterTypes();
				for (Class<?> cls : parameterTypes) {
					sb.append(cls.getSimpleName()).append(", ");
				}
				sb.delete(sb.length() - 2, sb.length());
				sb.append(")");
				System.out.println(sb.toString());
			}
		}
	}

	@Test
	public void abc() {
		int numberOfCores = Runtime.getRuntime().availableProcessors();
		int poolSize = (int) (numberOfCores / (1 - 0.9));
		System.out.println(poolSize);
	}

	@Test
	public void a2() {
		List<Integer> list = new ArrayList<>();
		list.add(10);
		list.add(5);
		list.add(4);
		list.add(2);
		list.add(8);
		list.add(1);
		list.add(11);
		list.add(15);
		list.add(13);
		list.sort(Integer::compareTo);
		System.out.println(list);
//		System.out.println(binarySearch(list, 0, list.size() - 1, 5));
//		System.out.println(binarySearch(list, 0, list.size() - 1, 2));
//		System.out.println(binarySearch(list, 0, list.size() - 1, 1));
//		System.out.println(binarySearch(list, 0, list.size() - 1, 15));
//		System.out.println(binarySearch(list, 0, list.size() - 1, 3));
		Integer a = 5;
		Integer b = 3;
		System.out.println(a.compareTo(b));
	}

	private int binarySearch(List<Integer> list, int beginIndex, int endIndex, int target) {
		if (beginIndex == endIndex) {
			return beginIndex;
		} else if (endIndex - beginIndex == 1) {
			 if (list.get(endIndex) == target && list.get(beginIndex) != target) {
			 		return endIndex;
			 }
			 return beginIndex;
		}
		int middle = (endIndex + beginIndex) / 2;
		Integer middleVal = list.get(middle);
		if (target < middleVal) {
			return binarySearch(list, beginIndex, middle - 1, target);
		} else if (target > middleVal) {
			return binarySearch(list, middle + 1, endIndex, target);
		} else {
			return middle;
		}
	}


}

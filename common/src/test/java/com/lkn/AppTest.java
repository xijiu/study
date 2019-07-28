package com.lkn;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

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
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			System.out.println(new Date().toLocaleString());
		}
		long cost = System.currentTimeMillis() - begin;
		System.out.println(cost);
	}

	@Test
	public void aaaaa() {
		int a = 3233;
		System.out.println((float)a / (float)500 * (float)300);
	}

	private static class Provider {
		private String providerId;
		private int maxThreadNum;
		private int activeThreadNum;
		private int tps;

		public String toString() {
			return "providerId-" + providerId + ",maxThreadNum-" + maxThreadNum + ",activeThreadNum-" + activeThreadNum + ",tps-" + tps;
		}
	}

	@Test
	public void bbb3() throws IOException {
		System.out.println(new Date().toLocaleString());
	}

	@Test
	public void bbb4() throws Exception {
		String path = "/Users/likangning/Downloads/238038_738110_hCvg8u1ynVGp";
		String[] str = {"small", "medium", "large"};
		for (String aStr : str) {
			File file = new File(path + "/provider-" + aStr + "-logs/stdout.log");
			BufferedReader br = new BufferedReader(new FileReader(file));
			int num = 0;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.equals("开启限流")) {
					num++;
				}
			}
			br.close();
			System.out.println(aStr + " : " + num);
		}
	}

	/**
	 *
	 * 80
	 * small : 331725
	 * medium : 850993
	 * large : 1064324
	 *
	 * 80
	 * small : 333073
	 * medium : 814947
	 * large : 1278491
	 *
	 * 85
	 * small : 385764
	 * medium : 661073
	 * large : 1123310
	 * 38:66:112
	 *
	 * 90
	 * small : 360614
	 * medium : 771875
	 * large : 1070305
	 * 36:77:107
	 *
	 * 95
	 * small : 383648
	 * medium : 727157
	 * large : 1165387
	 * 38:72:116
	 *
	 */
	@Test
	public void test80() throws Exception {
		LongAdder longAdder = new LongAdder();
		for (int i = 0; i < 100; i++) {
			longAdder.increment();
		}
		System.out.println(longAdder.sum());
		System.out.println(longAdder.intValue());
	}



	@Test
	public void test90() throws Exception {
		String pre = "receive msg from server :";
		String score122 = "238501_742008_q01SDlNQbACY";
		String score120 = "242925_741272_FSU2rn1tu4c7";
		String score118_2 = "242925_741851_dWux1903I4vo";
		String score118 = "242925_741877_UGhD0rJRKj54";
		String score117 = "242925_741890_eBIpvm5srVR4";
		String[] str = {score122, score120, score118_2, score118, score117};
		for (int i = 0; i < str.length; i++) {
			Map<String, ProviderStatus> map = Maps.newHashMap();
			map.put("small", new ProviderStatus());
			map.put("medium", new ProviderStatus());
			map.put("large", new ProviderStatus());
			String path = "/Users/likangning/Downloads/" + str[i];
			File file = new File(path + "/consumer-logs/stdout.log");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(pre)) {
					line = line.substring(pre.length());
					String[] split = line.split(",");
					if (split.length == 5) {
						ProviderStatus status = map.get(split[0]);
						status.threadNum = status.threadNum + Integer.parseInt(split[2]);
						status.tps = status.tps + Integer.parseInt(split[3]);
					}
				}
			}
			br.close();
			System.out.println(map);
		}
	}

	private static class ProviderStatus {
		private String id;
		private int threadNum = 0;
		private int tps = 0;

		@Override
		public String toString() {
			return "threadNum is " + threadNum + ", tps is " + tps;
		}
	}

	private volatile boolean flag = true;

	@Test
	public void aaaaaa() throws InterruptedException {
		for (int i = 0; i < 2000; i++) {
			new Thread(() -> {
				while (flag) {
				}
			}).start();
		}

		Thread.sleep(1000000);
	}

	@Test
	public void deleteFileTest() {
		File file = new File("/alidata1/race2019/data/");
		File[] files = file.listFiles();
		for (File fileTemp : files) {
			System.out.println(fileTemp.getName());
			boolean result = fileTemp.delete();
			System.out.println("删除结果： " + result);
			System.out.println();
		}
	}

	private int times = 10000000;

	@Test
	public void sort1() {
		long begin = System.currentTimeMillis();
		Set<Integer> set = new TreeSet<>();
		for (int i = 0; i < times; i++) {
			set.add(ThreadLocalRandom.current().nextInt(times));
		}
		long end = System.currentTimeMillis();
		System.out.println("sort1 cost : " + (end - begin));
	}

	/**
	 * 14177
	 */
	@Test
	public void sort3() {
		long begin = System.currentTimeMillis();

		for (int i = 0; i < 50; i++) {
			sort2();
		}

		long end = System.currentTimeMillis();

		System.out.println("最终耗时 cost : " + (end - begin));
	}

	@Test
	public void sort2() {
//		List<Integer> list = new ArrayList<>();
		List<Integer> list = Collections.synchronizedList(new ArrayList<>(times));

		long begin = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			list.add(ThreadLocalRandom.current().nextInt(times));
		}
		System.out.println(list.size());
		long begin1 = System.currentTimeMillis();
//		Collections.sort(list);
		long end = System.currentTimeMillis();
		System.out.println("sort cost : " + (end - begin1));
		System.out.println("总耗时 cost : " + (end - begin));
	}

	private static volatile int[] arr = new int[1000000];

	@Test
	public void sort5() throws Exception {
		int threadNum = 10;
		AtomicInteger num = new AtomicInteger();
		ExecutorService executorService = Executors.newCachedThreadPool();
		for (int i = 0; i < threadNum; i++) {
			executorService.submit(() -> {
				for (int j = 0; j < arr.length / threadNum; j++) {
					int id = num.getAndIncrement();
					arr[id] = id;
				}
			});
		}
		executorService.shutdown();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
	}


}

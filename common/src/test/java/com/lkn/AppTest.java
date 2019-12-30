package com.lkn;

import com.google.common.base.CaseFormat;
import com.google.common.collect.*;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;
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
		System.out.println((float) a / (float) 500 * (float) 300);
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
	 * 80
	 * small : 331725
	 * medium : 850993
	 * large : 1064324
	 * <p>
	 * 80
	 * small : 333073
	 * medium : 814947
	 * large : 1278491
	 * <p>
	 * 85
	 * small : 385764
	 * medium : 661073
	 * large : 1123310
	 * 38:66:112
	 * <p>
	 * 90
	 * small : 360614
	 * medium : 771875
	 * large : 1070305
	 * 36:77:107
	 * <p>
	 * 95
	 * small : 383648
	 * medium : 727157
	 * large : 1165387
	 * 38:72:116
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

	@Test
	public void testt() {
		int size = 5000000;
		Long[] arr = new Long[size];

		long begin1 = System.currentTimeMillis();

		for (int i = 0; i < size; i++) {
			arr[i] = (long) i;
		}

		long end1 = System.currentTimeMillis();

		System.out.println("循环添加耗时 " + (end1 - begin1));


		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			List<Long> list = Arrays.asList(arr);
		}
		long end = System.currentTimeMillis();
		System.out.println("耗时： " + (end - begin));
	}

	@Test
	public void aaa() {
		long begin = System.currentTimeMillis();
		BigInteger bigInteger = BigInteger.valueOf(0);
		for (int i = 0; i < 1280000; i++) {
			bigInteger = bigInteger.add(BigInteger.valueOf(Long.MAX_VALUE));
		}
		System.out.println(bigInteger);
		System.out.println(bigInteger.divide(BigInteger.valueOf(1280000)));
		long end = System.currentTimeMillis();
		System.out.println("耗时： " + (end - begin));
	}

	@Test
	public void bbbb() {
		TreeSet<User> set = new TreeSet<>();
		set.add(new User(1, 1, "1"));
		set.add(new User(2, 2, "2"));
		set.add(new User(3, 3, "3"));
		set.add(new User(4, 4, "4"));
		set.add(new User(5, 5, "5"));

		User floor = set.floor(new User(1, -1));
		System.out.println(floor.getId() + " ::: " + floor.getType() + " ::: " + floor.name);
	}


	private static class User implements Comparable<User> {
		private int id;
		private int type;
		private String name;

		private User(int id, int type) {
			this.id = id;
			this.type = type;
		}

		private User(int id, int type, String name) {
			this.id = id;
			this.type = type;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		@Override
		public int compareTo(User o) {
			if (this.id > o.getId()) {
				return 1;
			} else if (this.id < o.getId()) {
				return -1;
			} else {
				return Long.compare(this.getType(), o.getType());
			}
		}
	}

	@Test
	public void cccc() throws InterruptedException, IOException {
		File file = new File("/Users/likangning/Downloads/new_blog/tester_log1.log");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		int maxDiffA = 0;
		int minDiffA = Integer.MAX_VALUE;
		long diffASum = 0;
		int totalMsg = 0;
		int minA = Integer.MAX_VALUE;
		int maxA = 0;
		int minT = Integer.MAX_VALUE;
		int maxT = 0;
		while ((line = br.readLine()) != null) {
			line = line.substring(10);
			String[] split = line.split(", ");
			if (split.length == 4) {
				totalMsg++;
				int beginA = Integer.parseInt(split[0]);
				int endA = Integer.parseInt(split[1]);
				int beginT = Integer.parseInt(split[2]);
				int endT = Integer.parseInt(split[3]);
				System.out.println(beginA + ", " + endA);
				int diffA = endA - beginA;
				maxDiffA = diffA > maxDiffA ? diffA : maxDiffA;
				minDiffA = diffA < minDiffA ? diffA : minDiffA;
				diffASum += diffA;
				minA = beginA < minA ? beginA : minA;
				maxA = endA > maxA ? endA : maxA;
				minT = beginT < minT ? beginT : minT;
				maxT = endT > maxT ? endT : maxT;
			}
		}
		br.close();
		System.out.println("maxDiffA is " + maxDiffA);
		System.out.println("minDiffA is " + minDiffA);
		System.out.println("avg A is " + (diffASum / totalMsg));
		System.out.println();
		System.out.println("min A is " + minA);
		System.out.println("max A is " + maxA);
		System.out.println("min T is " + minT);
		System.out.println("max T is " + maxT);
	}

	@Test
	public void dddd() {
		int math = 1;
		int language = 1 << 1;
		int chemistry = 1 << 2;
		int physics = 1 << 3;
		int all = math + language + chemistry + physics;

		int choose = math + physics;

		//判断小明所选科目
		if ((choose & math) == math) {
			System.out.println("小明选择了数学");
		}
		if ((choose & language) == language) {
			System.out.println("小明选择了语文");
		}
		if ((choose & chemistry) == chemistry) {
			System.out.println("小明选择了化学");
		}
		if ((choose & physics) == physics) {
			System.out.println("小明选择了物理");
		}
	}

	@Test
	public void ffff() {
		long begin = System.currentTimeMillis();
//		int[] arr = new int[]{1, 2, 5};
//		int input = 6;
//		for (int i = 0 ; i < 10000000; i++) {
//			int a = i + 30000000;
////			int a = i + 1;
//		}

		int a = 100;
		int b = 400;
		int result = a ^ b + (a & b) << 1;
		System.out.println(result);
		System.out.println(0 >> 1);


		long end1 = System.currentTimeMillis();
		System.out.println("循环添加耗时 " + (end1 - begin));
	}


	@Test
	public void cccc2() throws InterruptedException, IOException {
		Map<Integer, Long> map = new TreeMap<>();
		File file = new File("/Users/likangning/Downloads/new_blog/tester_log.log");
		BufferedReader br = new BufferedReader(new FileReader(file));
		int time_tmp = 0;
		int time = 0;
		int time2 = 0;
		int time3 = 0;
		int time4 = 0;
		int time5 = 0;
		int tDiffSum = 0;
		int aDiffSum = 0;
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("avg :")) {
				time_tmp++;
				String substring = line.substring(0, 5);
				int group = Integer.parseInt(substring.trim());
				int avgIndex = line.indexOf("avg") + 5;
				line = line.substring(avgIndex);
				line = line.trim();
				String[] split = line.split(",");
				int aMin = Integer.parseInt(split[0].trim());
				int aMax = Integer.parseInt(split[1].trim());
				int tMin = Integer.parseInt(split[2].trim());
				int tMax = Integer.parseInt(split[3].trim());
				int aDiff = aMax - aMin;
				int tDiff = tMax - tMin;
				tDiffSum += tDiff;
				aDiffSum += aDiff;
				if (aDiff < 4000) {
					time++;
				} else if (aDiff < 10000) {
					time2++;
				} else if (aDiff < 15000) {
					time3++;
				} else if (aDiff < 20000) {
					time4++;
				} else {
					System.out.println("aMin " + aMin + ", aMax " + aMax + ", tMin " + tMin + ", tMax " + tMax + " a diff is " + aDiff);
					time5++;
				}
//				System.out.println("[a diff is " + aDiff + "] ,  [t diff is " + tDiff + "]");
				Long sum = map.get(group);
				if (sum == null) {
					sum = 0L;
				}
				sum += tMax - tMin;
				map.put(group, sum);
			}
		}
//		for (Map.Entry<Integer, Long> entry : map.entrySet()) {
//			System.out.println((entry.getKey() + 1000) + ":" + entry.getValue());
//		}
		System.out.println("aDiff小于4000的数量为 " + time);
		System.out.println("4000-10000  数量为 " + time2);
		System.out.println("10000-15000 数量为 " + time3);
		System.out.println("15000-20000 数量为 " + time4);
		System.out.println("15000-200002 数量为 " + time5);
		System.out.println("总数量为 " + time_tmp);
		System.out.println(tDiffSum / time_tmp);
		System.out.println(aDiffSum / time_tmp);
		br.close();
	}

	@Test
	public void cc() {
		int a = 2;
		int b = 3;
		a = a ^ b;
		b = a ^ b;
		a = a ^ b;
		System.out.println("a=" + a + ", b=" + b);
	}

	@Test
	public void cc2() {
//		System.out.println(435 ^ 1);
//		System.out.println(432 ^ 0);
		int max = -1;
		int min = Integer.MAX_VALUE;

		for (int j = 1; j < 3000; j++) {
			int a = j;
			int xor = a ^ 1;
			max = xor > max ? xor : max;
			min = xor < min ? xor : min;
		}
		System.out.println("max is " + max);
		System.out.println("min is " + min);

		System.out.println(Long.MAX_VALUE / 100000000);


//		a = a ^ b;
//		b = a ^ b;
//		a = a ^ b;
//		System.out.println("a=" + a + ", b=" + b);
	}


	public static int shortToUnsigned(short data) {
		return data & 0xFFFF;
	}

	public static short recoverToShort(int data) {
		int diff = data - Short.MAX_VALUE;
		if (diff > 0) {
			return (short) (Short.MIN_VALUE + diff - 1);
		} else {
			return (short) data;
		}
	}

	private ExecutorService executorService = Executors.newCachedThreadPool();

	@Test
	public void runrun() throws InterruptedException {
		BitSet bitSet = new BitSet();
		BitSet bitSet1 = bitSet.get(1, 1);
		long begin = System.currentTimeMillis();
		for (int j = 0; j < 100000; j++) {
			executorService.submit(() -> {
				for (int i = 5; i < 200000000; i++) {
					invoke1((short) i);
//					invoke2(i);
				}
			});
		}
		executorService.shutdownNow();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		long end = System.currentTimeMillis();
		System.out.println("cost : " + (end - begin));
	}

	private void invoke1(short num) {
		int a = num + num;
	}

	private void invoke2(int num) {
		int a = num + num;
	}

	private static LongAdder longAdder = new LongAdder();

	@Test
	public void abc1() throws InterruptedException {
		long begin = System.currentTimeMillis();
		int size = 10;
		Thread[] threads = new Thread[size];
		for (int i = 0; i < size; i++) {
			threads[i] = new Thread(() -> {
				for (int j = 0; j < 3000; j++) {
					long begin1 = System.currentTimeMillis();
					long a = Long.MAX_VALUE / Integer.MAX_VALUE;
					long end = System.currentTimeMillis();
					longAdder.add(end - begin1);
				}
			});
		}

		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}


		long end = System.currentTimeMillis();
		System.out.println("longAdder is : " + longAdder.sum());
		System.out.println("cost : " + (end - begin));
	}


	private static ExecutorService executorService2 = Executors.newCachedThreadPool();

	private static int num;


	/**
	 * cost : 60245
	 */
	@Test
	public void abc2() throws InterruptedException {
		Map<Integer, Integer> map = new HashMap<>();
		map.put(500, 500);
		long begin = System.currentTimeMillis();

		for (int j = 0; j < 10; j++) {
			executorService2.submit(() -> {
				for (int i = 0; i < 2000000000; i++) {
//					arrSum1();
					int a1 = ThreadLocalRandom.current().nextInt(5);
					num = a1 + a1;
					map.get(500);
				}
			});
		}
		executorService2.shutdownNow();
		executorService2.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		long end = System.currentTimeMillis();
		System.out.println("cost : " + (end - begin));
		System.out.println(num);
	}

	private void arrSum1() {
		int[] arr = createArr();
		num = arr[0] + arr[1];
	}

	private int[] createArr() {
		int[] arr = new int[2];
		int a1 = ThreadLocalRandom.current().nextInt(5);
		arr[0] = a1;
		arr[1] = a1;
		return arr;
	}


	@Test
	public void cccc3() throws Exception {
		int num = 0;
		List<Bean> list = new ArrayList<>();
		File file = new File("/Users/likangning/Downloads/new_blog/tester_log.log");
		BufferedReader br = new BufferedReader(new FileReader(file));
		char[] msgArr = new char[42];
		while ((br.read(msgArr)) != -1) {
			String msg = new String(msgArr);
			msg = msg.replaceAll("},", "");
			msg = msg.replaceAll("\\{begin=", "");
			msg = msg.replaceAll("end=", "");
			String[] arr = msg.split(",");
//			System.out.println(msg);
			list.add(new Bean(Long.parseLong(arr[0].trim()), Long.parseLong(arr[1].trim())));
		}
		br.close();

		for (int i = 1; i < list.size(); i++) {
			Bean last = list.get(i - 1);
			Bean curr = list.get(i);
			if (curr.end > last.end) {
				num++;
				continue;
			} else {
				System.out.println();
				System.out.println("last is " + last);
				System.out.println("curr is " + curr);
			}
		}

		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}

		System.out.println(num);
	}


	private static class Bean {
		private long begin;
		private long end;

		private Bean(long begin, long end) {
			this.begin = begin;
			this.end = end;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("");
			sb.append("begin=").append(begin);
			sb.append(", end=").append(end);
			return sb.toString();
		}
	}


	private byte[] tmp;

	@Test
	public void cccc4() throws Exception {
		long begin = System.currentTimeMillis();

		ByteBuffer bf = ByteBuffer.allocate(2800);
		File file = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");
		FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.READ);
		long totalSize = fileChannel.size();
		for (int i = 0; i < 1000000; i++) {
			long position = ThreadLocalRandom.current().nextLong(totalSize);
			fileChannel.position(position);
			bf.clear();
			fileChannel.read(bf);
			bf.flip();
			if (bf.hasRemaining()) {
//				byte[] arr = new byte[bf.limit()];
//				bf.get(arr);
//				tmp = arr;
			}
		}

		System.out.println(tmp);
		fileChannel.close();
		long end = System.currentTimeMillis();
		System.out.println("cost : " + (end - begin));
	}

	@Test
	public void cccc5() throws Exception {
		long begin = System.currentTimeMillis();

		ByteBuffer bf = ByteBuffer.allocate(4096 * 10);
		File file = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");
		FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.READ);
		long totalSize = fileChannel.size();
		for (int i = 0; i < 1000000; i++) {
			long position = ThreadLocalRandom.current().nextLong(totalSize);
			position = position / 4096 * 4096;
			fileChannel.position(position);
			bf.clear();
			fileChannel.read(bf);
			bf.flip();
			if (bf.hasRemaining()) {
			}
		}

		System.out.println(tmp);
		fileChannel.close();
		long end = System.currentTimeMillis();
		System.out.println("cost : " + (end - begin));
	}

	@Test
	public void cccc6() throws Exception {
		int size = 1 * 1024 * 1024;
		int num = size / 4;
		long begin = System.currentTimeMillis();

		byte[] arrays = null;

		ByteBuffer bf = ByteBuffer.allocate(4096);
		File file = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");
		FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.READ);
		for (int i = 0; i < num; i++) {
			bf.clear();
			fileChannel.read(bf);
			bf.flip();
			arrays = bf.array();
		}
		fileChannel.close();
		long end = System.currentTimeMillis();
		System.out.println("cost : " + (end - begin));
	}

	@Test
	public void b() throws Exception {
		int size = 1 * 1024 * 1024 * 1024;
		int num = size / 4096;
		long begin = System.currentTimeMillis();

		byte[] arrays = null;

		byte[] bytes = new byte[4096];
		File file = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");
		FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.READ);
		MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);
		for (int i = 0; i < num; i++) {
			mappedByteBuffer.get(bytes);
			arrays = bytes;
		}
		fileChannel.close();
		long end = System.currentTimeMillis();
		System.out.println("cost : " + (end - begin));
	}

	@Test
	public void c() throws Exception {
		File file = new File("/Users/likangning/Downloads/mq_logs/mqrace2019_238038_238038_1566440042_log/tester_log.log");
		int lineNum = 0;
		long costHeadSum = 0;
		long costTailSum = 0;
		int tailNum = 0;
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.startsWith("==========> 消息长度")) {
				lineNum++;
				int index = line.indexOf("写入一次耗时");
				line = line.substring(index + 7);
				long cost = Long.parseLong(line.trim());
				if (lineNum <= 1644) {
					costHeadSum += cost;
				} else {
					tailNum++;
					costTailSum += cost;
				}
			}
		}
		System.out.println(costHeadSum / 1644);
		System.out.println(costTailSum / tailNum);
		br.close();
	}

	@Test
	public void d() {
		Map<Integer, String> map = new ConcurrentHashMap<>();
		String a = map.computeIfAbsent(1, k -> {
			System.out.println("aaaa");
			return new String("a");
		});
		String b = map.computeIfAbsent(1, k -> {
			System.out.println("bbbb");
			return new String("a");
		});
		System.out.println(a);
		System.out.println(b);
		BitSet bitSet = new BitSet();
	}

	@Test
	public void e() {
		Random random = new Random();

		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 10000000; i++) {
			int randomResult = random.nextInt(100000000);
			list.add(randomResult);
		}
		System.out.println("产生的随机数有");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		BitSet bitSet = new BitSet(100000000);
		for (int i = 0; i < 10000000; i++) {
			bitSet.set(list.get(i));
		}

		System.out.println("0~1亿不在上述随机数中有" + bitSet.size());
		for (int i = 0; i < 100000000; i++) {
			if (!bitSet.get(i)) {
				System.out.println(i);
			}
		}
	}


	@Test
	public void e1() {
		System.out.println(4096 % 9);
	}


	@Test
	public void b222() throws Exception {
		long begin = System.currentTimeMillis();

		byte[] arrays = null;

		byte[] bytes = new byte[4096];
		File file = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");
		ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
		FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.READ);
		fileChannel.position(2374);
		for (int i = 0; i < 10000000; i++) {
			fileChannel.read(byteBuffer, 0);
			bytes = byteBuffer.array();
		}
		fileChannel.close();
		long end = System.currentTimeMillis();
		System.out.println("cost : " + (end - begin));
	}

	@Test
	public void ccccc22() throws Exception {
		File file = new File("/Users/likangning/Downloads/lkn_for_test.index");
		if (!file.exists()) {
			file.createNewFile();
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
		FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.READ, StandardOpenOption.WRITE);
		for (int j = 0; j < 10; j++) {
			byteBuffer.clear();
			for (long i = 0; i < 4096 / 8; i++) {
				byteBuffer.putLong(i);
			}
			byteBuffer.flip();
			fileChannel.write(byteBuffer);
		}
		fileChannel.close();
	}

	/**
	 * 281474976710655
	 */
	@Test
	public void dddd3() {
//		Long num = 281474976710655L;
		Long num = 1099511627776L;
		String s = Long.toBinaryString(num);
		String pre = "";
		for (int i = 0; i < 64 - s.length(); i++) {
			pre += "0";
		}
		s = pre + s;
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			System.out.print(chars[i]);
			if ((i + 1) % 8 == 0) {
				System.out.print(" ");
			}
		}
	}

	@Test
	public void dddd4() {
		ConcurrentSkipListSet<Integer> skipListSet = new ConcurrentSkipListSet<>();
		skipListSet.add(100);
		skipListSet.add(5);
		skipListSet.add(22);
		skipListSet.add(34);
		skipListSet.add(2);
		skipListSet.add(3);
		skipListSet.add(8);
		System.out.println(skipListSet);
	}

	@Test
	public void aaaaa1() {
		float num1 = 200000000F;
		float num2 = 200000001F;
		float num3 = 200000002F;
		System.out.println(num1);
		System.out.println(num2);
		System.out.println(num3);
	}

	@Test
	public void ccc() {
		String str = "123-123";
		System.out.println(str.replaceAll("-", ""));
	}

	private void printFloat(float a) {
		int b = Float.floatToIntBits(a);
		String str = Integer.toBinaryString(b);
		char[] chars = str.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 32 - chars.length; i++) {
			sb.append("0");
		}
		sb.append(str);
		chars = sb.toString().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			System.out.print(chars[i]);
			if (i % 8 == 7) {
				System.out.print(" ");
			}
		}
		Map<String, String> map = null;
	}


	@Test
	public void ccccd() throws Exception {
		long begin = System.currentTimeMillis();
		for (int j = 0; j < 10; j++) {
			long readSize = 4096;
			ByteBuffer bf = ByteBuffer.allocate((int) readSize);
			File file = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");
			FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.READ);
			long totalSize = fileChannel.size();
			for (int i = 0; i < totalSize / readSize; i++) {
				bf.clear();
				fileChannel.read(bf);
				bf.flip();
			}
			fileChannel.close();
		}
		long end = System.currentTimeMillis();
		System.out.println("cost : " + (end - begin));
	}

	@Test
	public void cccce() throws Exception {
		long begin = System.currentTimeMillis();
		for (int j = 0; j < 10; j++) {
			long readSize = 4096;
			byte[] bytes = new byte[(int) readSize];
			File file = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");
			FileInputStream fileInputStream = new FileInputStream(file);
			while (fileInputStream.read(bytes) != -1) {
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("cost : " + (end - begin));
	}

	@Test
	public void aaabb() {
		Multimap<Long, Integer> multimap = ArrayListMultimap.create();
		multimap.put(1L, 1);
		multimap.put(1L, 2);
		multimap.put(1L, 3);
		multimap.put(2L, 4);
		multimap.put(2L, 5);
		System.out.println(multimap);

		ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<>();
		threadLocal.set(new HashMap<>());
		String test = "20191201";
		System.out.println(test.substring(0, 6));

	}



	class Foo {

		Object obj = new Object();


		public Foo() {

		}

		private volatile int flag = 1;

		public void first(Runnable printFirst) throws InterruptedException {

			// printFirst.run() outputs "first". Do not change or remove this line.
			printFirst.run();
			flag = 2;
		}

		public void second(Runnable printSecond) throws InterruptedException {
			while (flag < 2) {
				Thread.sleep(0, 100);
			}
			// printSecond.run() outputs "second". Do not change or remove this line.
			printSecond.run();
			flag = 3;
		}

		public void third(Runnable printThird) throws InterruptedException {
			while (flag < 3) {
			}
			// printThird.run() outputs "third". Do not change or remove this line.
			printThird.run();
		}
	}





}

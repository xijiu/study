package com.lkn;

import com.google.common.base.Strings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.security.provider.MD5;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author likangning
 * @since 2020/5/8 上午10:11
 */
public class FileChecksumTest {

	private long beginTime;

	@Before
	public void before() {
		beginTime = System.currentTimeMillis();
	}

	@After
	public void after() {
		System.out.println("耗时： " + (System.currentTimeMillis() - beginTime));
	}

	@Test
	public void aaaaa() throws Exception {
		String sb = "2_1_2_subpath_url";
		System.out.println(extractIndexByKey(sb));
	}

	private Integer extractIndexByKey(String key) {
		String[] split = key.split("_");
		String numStr = "";
		for (String ele : split) {
			char[] chars = ele.toCharArray();
			boolean isNum = true;
			for (char aChar : chars) {
				// 当前字符为非数字
				if (!Character.isDigit(aChar)) {
					isNum = false;
					break;
				}
			}
			if (isNum) {
				numStr += ele;
			} else {
				break;
			}
		}
		return Integer.parseInt(numStr);
	}

	/**
	 * c0bc243e017ef22ce16e1ca728eb98f5
	 * @throws Exception
	 */
	@Test
	public void genChecksum1() throws Exception {
		File file = new File("/Users/likangning/Desktop/lkn_test.data");
		MessageDigest messageDigest = MessageDigest.getInstance("md5");
		messageDigest.update(Files.readAllBytes(file.toPath()));
		byte[] digestBytes = messageDigest.digest();
		StringBuilder sb = new StringBuilder();
		for (byte b : digestBytes) {
			sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		System.out.println(sb.toString());
	}

	@Test
	public void genChecksum2() throws Exception {
		byte[] bytes = "123".getBytes();
		MessageDigest messageDigest = MessageDigest.getInstance("md5");
		byte[] digestBytes = messageDigest.digest(bytes);
		System.out.println(digestBytes.length);
		StringBuilder sb = new StringBuilder();
		for (byte b : digestBytes) {
			sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			System.out.println(sb.toString());
		}
		System.out.println(sb.toString());
		MD5 md5 = new MD5();
		System.out.println();
	}

	@Test
	public void genChecksum3() throws Exception {
		int size = 20;
		ExecutorService executorService = Executors.newFixedThreadPool(size);
//		Map<String, String> map = new HashMap<>();
		Map<String, String> map = new ConcurrentHashMap<>();
		for (int i = 0; i < size; i++) {
			final int index = i + 1;
			Thread thread = new Thread(() -> {
				String result1 = map.putIfAbsent("abc", "value_" + index);
				System.out.println(index + " : " + result1);
			});
			executorService.submit(thread);
		}
		executorService.shutdown();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
	}

	@Test
	public void genChecksum4() throws Exception {
		long begin = System.currentTimeMillis();
//		Map<String, String> map = new HashMap<>();
		Map<String, String> map = new ConcurrentHashMap<>();
		for (int i = 0; i < 2000000; i++) {
			map.put(String.valueOf(i), String.valueOf(i));
		}
		System.out.println(map.size());
		System.out.println("耗时： " + (System.currentTimeMillis() - begin));
	}

	@Test
	public void ccc() {
		int length = 0;
		String str = "1d37a8b17db8568b|1589285985482059|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207";
		for (int i = 0; i < 1000000; i++) {
			String[] split = str.split("\\|");
			length += split.length;
		}
		System.out.println(length);
	}

	@Test
	public void ccc2() throws InterruptedException {
		List<String> dataList = new ArrayList<>();
		dataList.add("a");
		dataList.add("b");
		dataList.add("c");
		String collect = dataList.stream().collect(Collectors.joining("\n"));
		System.out.println(collect);
		System.out.println(123);
	}


}

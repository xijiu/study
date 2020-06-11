package com.lkn;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * @author likangning
 * @since 2020/5/14 下午2:49
 */
public class ThreadTest2 {

	public static class P1 {
		private long b = 0;

		public void set1() {
			b = 0;
		}

		public void set2() {
			b = -1;
		}

		public void check() {
//			System.out.println(b);

			if (0 != b && -1 != b) {
				System.err.println("Error");
			}
//			if (b > 0) {
//				System.out.println(b);
//			}
		}
	}

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
	public void abc() throws InterruptedException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			String tableName = "idea_pro_";
			for (int j = 0; j < 4 - String.valueOf(i).length(); j++) {
				tableName += "0";
			}
			tableName += i;
			sb.append("select * from " + tableName + " \n")
					.append("where style_type in (155, 156, 157) and user_id != 15092483  \n")
					.append("union ALL \n");
		}
		System.out.println(sb.toString());
	}


	@Test
	public void bbb() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("/Users/likangning/Desktop/云原生比赛/trace1.data"));
		int count = 0;
		long sum = 0;
		String str = null;
		while ((str = br.readLine()) != null) {
			sum += str.length();
			count++;
		}
		System.out.println(sum);
		System.out.println(count);
	}

	@Test
	public void bbb2() throws Exception {
		InputStream inputStream = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");
		int count = 0;
		byte[] arr = new byte[5 * 1024 * 1024];
		int length = 0;
		while ((length = inputStream.read(arr)) != -1) {
			int begin = 0;
			while (true) {
				for (int i = begin; i < length; i++) {
					if (arr[i] == 10) {
						count++;
						begin = i + 1;
						break;
					}
				}
				if (begin + 400 >= length) {
					break;
				}
			}
		}
		System.out.println(count);
	}


	@Test
	public void bbb3() throws Exception {
		InputStream inputStream = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");
		int count = 0;
		byte[] arr = new byte[5 * 1024 * 1024];
		int length = 0;
		long sum = 0;
		Set<Long> set = new HashSet<>();
		List<Long> list = new ArrayList<>(4000);
		Semaphore semaphore = new Semaphore(20000000);
		while ((length = inputStream.read(arr)) != -1) {
			int begin = 0;
			while (true) {
				for (int i = begin; i < length; i++) {
					if (arr[i] == 10) {
						long traceId = longFrom8Bytes(arr, begin);
						if (isBadTraceData(arr, i - 1)) {
							set.add(traceId);
//							list.add(traceId);
						}
						if (count % 20000 == 0) {
//							System.out.println(set.size());
//							semaphore.acquire();
//							set.clear();
							set.clear();
						}
						count++;
						begin = i + 1;
						break;
					}
				}
				if (begin + 400 >= length) {
					break;
				}
			}
		}
		System.out.println(sum);
//		System.out.println(set);
		System.out.println(count);
	}

	private boolean isBadTraceData(byte[] lineByteArr, int endIndex) {
		if (endIndex < 10) {
			return false;
		}
		if (lineByteArr.length < 20) {
			return false;
		}
		int i = endIndex;
		// error=1
		if (lineByteArr[i--] == 49) {
			if (lineByteArr[i--] == 61) {
				if (lineByteArr[i--] == 114) {
					return true;
				}
			}
		}
		i = endIndex - 3;

		// http.status_code=
		if (lineByteArr[i--] == 61) {
			if (lineByteArr[i--] == 101) {
				return true;
			}
		}
		return false;
	}

	public static long longFrom8Bytes(byte[] input, int offset) {
		long value = 0;
		// 循环读取每个字节通过移位运算完成long的8个字节拼装
		for (int count = 0; count < 8; ++count) {
			int shift = count << 3;
			value |= ((long) 0xff << shift) & ((long) input[offset + count] << shift);
		}
		return value;
	}

}

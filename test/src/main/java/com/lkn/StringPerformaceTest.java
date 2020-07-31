package com.lkn;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import com.google.common.primitives.UnsignedBytes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author likangning
 * @since 2020/5/12 上午8:51
 */
public class StringPerformaceTest {

	private long beginTime;

	private int exeTime = 1000000;
//	private int exeTime = 1;

	private String targetStr = "1d37a8b17db8568b|1589285985482059|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|Frontend|sls.getOperator|192.168.0.15|http.status_code=200&http.url=http://localhost:9003/getAddress&component=java-web-servlet&span.kind=server&http.method=GET";


	@Test
	public void test() {
		for (int i = 0; i < 10000000; i++) {
			int firstIndex = targetStr.indexOf("|");
			String traceId = targetStr.substring(0, firstIndex);
		}
	}

	@Test
	public void test333() {
		for (int i = 0; i < 10000000; i++) {
			String traceId = targetStr.substring(0, 17);
			if (traceId.endsWith("|")) {
				traceId = traceId.substring(0, 16);
			}
		}
	}

	@Test
	public void test2() {
		for (int i = 0; i < exeTime; i++) {
			int index = targetStr.lastIndexOf("|");
			String last = targetStr.substring(index + 1, targetStr.length());
			int firstIndex = targetStr.indexOf("|");
			String first = targetStr.substring(0, firstIndex);
			int startTimeIndex = targetStr.indexOf("|", 1);
			String startTime = targetStr.substring(startTimeIndex + 1, startTimeIndex + 17);
//			System.out.println(first + " : " + last + " : " + startTime);
		}
		System.out.print("切割最后 ");
	}


	@Before
	public void before() {
		beginTime = System.currentTimeMillis();
	}

	@After
	public void after() {
		System.out.println("耗时： " + (System.currentTimeMillis() - beginTime));
	}

	@Test
	public void aaa() {
		String str = "abc\n123\n4444444";
		byte[] bytes = str.getBytes();
		int begin = 0;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == 10) {
				String line = new String(bytes, begin, i - begin);
				begin = i + 1;
				System.out.println(line);
			}
		}
	}

	@Test
	public void aaab() {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < 100000; i++) {
			list.add(String.valueOf(i));
		}

		long begin = System.currentTimeMillis();
		JSON.toJSONString(list);
		long end = System.currentTimeMillis();
		System.out.println(end - begin);

		long begin2 = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append(s).append(",");
		}
		long end2 = System.currentTimeMillis();
		System.out.println(end2 - begin2);

		long begin3 = System.currentTimeMillis();
		JSON.toJSONString(list);
		long end3 = System.currentTimeMillis();
		System.out.println(end3 - begin3);

	}

	@Test
	public void cccc() {
		Long num = 123456789L;
		byte[] bytes = longToByte(num);
		long result = 0;
		for (byte aByte : bytes) {
			result = longFrom8Bytes(result, aByte);
		}
		System.out.println(result);
	}

	public static byte[] longToByte(long number) {
		long temp = number;
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(temp & 0xff).byteValue();
			// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	public static long longFrom8Bytes(long value, byte input) {
		// 循环读取每个字节通过移位运算完成long的8个字节拼装
		for (int count = 0; count < 8; ++count) {
			int shift = count << 3;
			value |= ((long) 0xff << shift) & ((long) input << shift);
		}
		return value;
	}


	public static void main(String[] args) throws Exception {
//		byteTest();
//		test2222();
//		test333333();
	}


	@Test
	public void testFile333333() throws Exception {
		InputStream input = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");
		BufferedReader br = new BufferedReader(new InputStreamReader(input));

		int count = 0;
		String line;
		while ((line = br.readLine()) != null) {
			count++;
		}
		input.close();
		System.out.println(count);
	}

	@Test
	public void testFile444444() throws Exception {
		InputStream input = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");

		byte[] bytes = new byte[1024 * 1024 * 10];
		int count = 0;
		while (input.read(bytes) != -1) {
			count++;
		}
		input.close();
		System.out.println(count);
	}

	@Test
	public void test333333() throws Exception {
		long begin = System.currentTimeMillis();
		String path = "http://localhost:8010/trace1.data";
		URL url = new URL(path);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
		httpConnection.setUseCaches(true);
		InputStream input = httpConnection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		Set<Integer> set = new HashSet<>();

		int count = 0;
		String line;
		while ((line = br.readLine()) != null) {
			count++;
			set.add(line.length() % 8);
		}
		input.close();
		System.out.println(count);
		System.out.println(set);
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void test4444() throws Exception {
		long begin = System.currentTimeMillis();
		String path = "http://localhost:8010/trace1.data";
		URL url = new URL(path);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
		httpConnection.setUseCaches(true);
		InputStream input = httpConnection.getInputStream();
		LineNumberReader br = new LineNumberReader(new InputStreamReader(input));

		int minLength = 1000;
		int count = 0;
		String line;
		while ((line = br.readLine()) != null) {
			minLength =	line.length() < minLength ? line.length() : minLength;
			count++;
			if (count % 10000 == 0) {
				System.out.println(minLength);
			}
		}
		int lineNumber = br.getLineNumber();
		System.out.println(count);
		System.out.println(lineNumber);
		input.close();
		System.out.println(lineNumber);
		System.out.println("minLength is " + minLength);
	}

	/**
	 * f75bba209ef8935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526|Frontend|DoGetAppsGrid|192.168.138.216|&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET
	 */
	@Test
	public void test2222() throws Exception {
		long begin = System.currentTimeMillis();
		String path = "http://localhost:8010/trace1.data";
		URL url = new URL(path);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
		httpConnection.setUseCaches(true);
		InputStream input = httpConnection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		int minPosition5 = Integer.MAX_VALUE;
		int minPosition6 = Integer.MAX_VALUE;
		int minPosition8 = Integer.MAX_VALUE;
		String minStr5 = null;
		String minStr6 = null;
		String minStr8 = null;
		String line;
		int count = 0;
		while ((line = br.readLine()) != null) {
			String[] split = line.split("\\|");
			int length5 = split[5].length();
			int length6 = split[6].length();
			int length8 = split[8].length();
			if (length5 < minPosition5) {
				minPosition5 = length5;
				minStr5 = split[5];
			}
			if (length6 < minPosition6) {
				minPosition6 = length6;
				minStr6 = split[6];
			}
			if (length8 < minPosition8) {
				minPosition8 = length8;
				minStr8 = split[8];
			}
			count++;
			if (count % 1000000 == 0) {
				System.out.println(count);
			}
		}
		input.close();
		System.out.println("minPosition5 is " + minPosition5);
		System.out.println("minStr5 is " + minStr5);

		System.out.println("minPosition6 is " + minPosition6);
		System.out.println("minStr6 is " + minStr6);

		System.out.println("minPosition8 is " + minPosition8);
		System.out.println("minStr8 is " + minStr8);
	}

	// 25117325   16090
	@Test
	public void byteTest() throws Exception {
		long begin = System.currentTimeMillis();
		InputStream input = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");

		long count = 0;
		byte[] data = new byte[1024 * 1024 * 2];
		int byteNum;
		int beginIndex;
		int endIndex;
		int beginPos;
		while ((byteNum = input.read(data)) != -1) {
			beginIndex = 0;
			endIndex = byteNum;
			beginPos = 0;
			while (beginIndex < endIndex) {
				int i;
				for (i = beginPos; i < endIndex; i++) {
					if (data[i] == 10) {
						count++;
						beginIndex = i + 1;
						beginPos = i + 1;
						break;
					}
				}
				if (i >= byteNum) {
					break;
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
		input.close();
	}


	@Test
	public void byteTest333() throws Exception {
		long begin = System.currentTimeMillis();
		InputStream input = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");

		long count = 0;
		byte[] data = new byte[1024 * 1024 * 2];
		int byteNum;
		int beginIndex;
		int endIndex;
		int beginPos;
		while ((byteNum = input.read(data)) != -1) {
			beginIndex = 0;
			endIndex = byteNum;
			beginPos = 0;
			while (true) {
				while (beginIndex < endIndex) {
					if (data[beginIndex++] == 10) {
						count++;
						break;
					}
				}
				if (beginIndex >= endIndex) {
					break;
				}
			}

		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
		input.close();
	}

	@Test
	public void byteTest2() throws Exception {
		long begin = System.currentTimeMillis();
		InputStream input = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");

		int times = 0;
		long count = 0;
		byte[] data = new byte[1024 * 1024 * 2];
		int byteNum;
		int beginIndex;
		int endIndex;
		int beginPos;
		while ((byteNum = input.read(data)) != -1) {
			beginIndex = 0;
			endIndex = byteNum;
			beginPos = 0;
			while (beginIndex < endIndex) {
				int i;
				for (i = beginPos; i < endIndex; i++) {
					if (data[i] == 124) {
						beginPos = i + 1;
						times++;
						break;
					} else {
						if (data[i] == 10) {
							count++;
							beginIndex = i + 1;
							beginPos = i + 1;
							break;
						}
					}

				}
				if (i >= byteNum) {
					break;
				}
			}
		}
		System.out.println(count);
		System.out.println("times is : " + times);
		System.out.println(System.currentTimeMillis() - begin);
		input.close();
	}


	@Test
	public void byteTest2222() throws Exception {
		long begin = System.currentTimeMillis();
		InputStream input = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");

		long count = 0;
		byte[] data = new byte[1024 * 1024 * 2];
		int byteNum;
		byte datum;
		while ((byteNum = input.read(data)) != -1) {
			for (int i = 0; i < byteNum; i++) {
				datum = data[i];
				if (datum == 10) {
					count++;
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
		input.close();
	}

	@Test
	public void byteTest222() throws Exception {
		long begin = System.currentTimeMillis();
		String path = "http://localhost:8010/trace1.data";
		URL url = new URL(path);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
		httpConnection.setUseCaches(true);
		InputStream input = httpConnection.getInputStream();

		long count = 0;
		byte[] data = new byte[1024 * 1024 * 2];
		int byteNum;
		while ((byteNum = input.read(data)) != -1) {
//			for (int i = 0; i < byteNum; i++) {
//				if (data[i] == 10) {
//					count++;
//				}
//			}
		}
		input.close();
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void inputStreamReaderTest() throws Exception {
		long begin = System.currentTimeMillis();
		String path = "http://localhost:8010/trace1.data";
		URL url = new URL(path);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
		httpConnection.setUseCaches(true);
		System.out.println(httpConnection.getClass().getName());
		InputStream input = httpConnection.getInputStream();
		System.out.println(input.getClass().getName());

		long count = 1;
		byte[] data = new byte[10];
		int byteNum;
//		while ((byteNum = input.read(data)) != -1) {
//			for (int i = 0; i < byteNum; i++) {
//				if (data[i] == 10) {
//					count++;
//				}
//			}
//		}
		input.close();
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
	}



	public boolean t(byte d) {
		return d == 10;
	}

	@Test
	public void aaabbb() {
		long a = 5124567;
		long b = 1234;
		a = a << 32;
		long sum = a + b;
		System.out.println("sum is " + sum);
		System.out.println(a);
		System.out.println(sum);
		System.out.println(sum >> 32);
		System.out.println(sum << 32 >> 32);
		System.out.println("sum is " + sum);
		long result = 0;
		for (int i = 0; i < 100000000; i++) {
			result += sum << 32 >> 32;
		}
		System.out.println(result);
	}

	@Test
	public void aaabbb2() {
		int index = 10;
		long sum = ((long) index << 32) + 11;
//		System.out.println(sum >> 32);
//		System.out.println((int) (sum >> 32));

		System.out.println(523 % 35);
		System.out.println(490 % 35);
	}


	@Test
	public void aaabbb3() {
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET";
		byte[] lineByteArr = str.getBytes();
		int endIndex;

		for (int i = 0; i < 20000000; i++) {
			endIndex = lineByteArr.length - 1;
			while (true) {
				// "error=1" :  101 114 114 111 114 61 49
				if (lineByteArr[endIndex] == 49) {
					if (lineByteArr[endIndex - 1] == 61) {
						if (lineByteArr[endIndex - 2] == 114) {
							if (lineByteArr[endIndex - 3] == 111) {
								if (lineByteArr[endIndex - 4] == 114) {
									if (lineByteArr[endIndex - 5] == 114) {
										if (lineByteArr[endIndex - 6] == 101) {
											break;
										}
									}
								}
							}
						}
					}
				}

				// "http.status_code="  :  104 116 116 112 46 115 116 97 116 117 115 95 99 111 100 101 61
				if (lineByteArr[endIndex] > 47 && lineByteArr[endIndex] < 58) {
					if (lineByteArr[endIndex - 1] > 47 && lineByteArr[endIndex - 1] < 58) {
						if (lineByteArr[endIndex - 2] > 47 && lineByteArr[endIndex - 2] < 58) {
							if (lineByteArr[endIndex - 3] == 61) {
								if (lineByteArr[endIndex - 4] == 101) {
									if (lineByteArr[endIndex - 5] == 100) {
										if (lineByteArr[endIndex - 6] == 111) {
											if (lineByteArr[endIndex - 7] == 99) {
												if (lineByteArr[endIndex - 8] == 95) {
													if (lineByteArr[endIndex - 9] == 115) {
														if (lineByteArr[endIndex - 10] == 117) {
															if (lineByteArr[endIndex - 11] == 116) {
																if (lineByteArr[endIndex - 12] == 97) {
																	if (lineByteArr[endIndex - 13] == 116) {
																		if (lineByteArr[endIndex - 14] == 115) {
																			if (lineByteArr[endIndex - 15] == 46) {
																				if (lineByteArr[endIndex - 16] == 112) {
																					if (lineByteArr[endIndex - 17] == 116) {
																						if (lineByteArr[endIndex - 18] == 116) {
																							if (lineByteArr[endIndex - 19] == 104) {
																								break;
																							}
																						}
																					}
																				}
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}

				if (lineByteArr[endIndex] == 124) {
					break;
				}
				--endIndex;
			}
		}
	}

	@Test
	public void isBadTraceData() {
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET";
		byte[] lineByteArr = str.getBytes();
		int endIndex = lineByteArr.length - 1;

		for (int i = 0; i < 20000000; i++) {
			int index = endIndex;
			// error=1
			if (lineByteArr[index--] == 49) {
				if (lineByteArr[index--] == 61) {
					if (lineByteArr[index--] == 114) {
						continue;
					}
				}
			}
			index = endIndex - 3;
			// http.status_code=
			if (lineByteArr[index--] == 61) {
				if (lineByteArr[index--] == 101) {
					continue;
				}
			}
		}
	}

	@Test
	public void ab() {
//		String str = "error=1";
//		String str = "http.status_code=";
		String str = "0123456789";
		byte[] bytes = str.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			System.out.print(bytes[i] + " ");
		}
		System.out.println();
		System.out.println((byte)'|');
	}


	@Test
	public void testFile3333333() throws Exception {
		InputStream input = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");
		BufferedReader br = new BufferedReader(new InputStreamReader(input));

		Map<Integer, Set<String>> set1 = new HashMap<>();
		Map<Integer, Set<String>> set2 = new HashMap<>();
		Map<Integer, Set<String>> set3 = new HashMap<>();
		Map<Integer, Set<String>> set4 = new HashMap<>();
		Map<Integer, Set<String>> set5 = new HashMap<>();
		Map<Integer, Set<String>> set6 = new HashMap<>();
		Map<Integer, Set<String>> set7 = new HashMap<>();
		Map<Integer, Set<String>> set8 = new HashMap<>();
		Map<Integer, Set<String>> set9 = new TreeMap<>();

		String line;
		int count = 0;
		while ((line = br.readLine()) != null) {
			String[] split = line.split("\\|");
			Set<String> set = set1.computeIfAbsent(split[0].length(), k -> new HashSet<>());
//			set.add(split[0]);

			set = set2.computeIfAbsent(split[1].length(), k -> new HashSet<>());
//			set.add(split[1]);

			set = set3.computeIfAbsent(split[2].length(), k -> new HashSet<>());
//			set.add(split[2]);

			set = set4.computeIfAbsent(split[3].length(), k -> new HashSet<>());
//			set.add(split[3]);

			set = set5.computeIfAbsent(split[4].length(), k -> new HashSet<>());
//			set.add(split[4]);

			set = set6.computeIfAbsent(split[5].length(), k -> new HashSet<>());
//			set.add(split[5]);

			set = set7.computeIfAbsent(split[6].length(), k -> new HashSet<>());
//			set.add(split[6]);

			set = set8.computeIfAbsent(split[7].length(), k -> new HashSet<>());
			set.add(split[7]);

			set = set9.computeIfAbsent(split[8].length(), k -> new HashSet<>());
//			set.add(split[7]);

			count++;
			if (count % 100000 == 0) {
				System.out.println(count);
			}
		}
		input.close();
		System.out.println(set1);
		System.out.println(set2);
		System.out.println(set3);
		System.out.println(set4);
		System.out.println(set5);
		System.out.println(set6);
		System.out.println(set7);
		System.out.println(set8);
		System.out.println(set9);
	}


	@Test
	public void bb() {
		System.out.println((byte)'0');
		System.out.println((byte)'9');
		System.out.println((byte)'.');
		System.out.println((byte)'h');
		System.out.println((byte)'&');
		System.out.println((byte)'|');
	}

//	public int compareTo(byte[] buffer1, int offset1, int length1,
//											 byte[] buffer2, int offset2, int length2) {
//		// Short circuit equal case
//		if (buffer1 == buffer2 && offset1 == offset2
//				&& length1 == length2) {
//			return 0;
//		}
//		int minLength = Math.min(length1, length2);
//		int minWords = minLength / Longs.BYTES;
//		int offset1Adj = offset1 + BYTE_ARRAY_BASE_OFFSET;
//		int offset2Adj = offset2 + BYTE_ARRAY_BASE_OFFSET;
//
//		/*
//		 * Compare 8 bytes at a time. Benchmarking shows comparing 8
//		 * bytes at a time is no slower than comparing 4 bytes at a time
//		 * even on 32-bit. On the other hand, it is substantially faster
//		 * on 64-bit.
//		 */
//		for (int i = 0; i < minWords * Longs.BYTES; i += Longs.BYTES) {
//			long lw = theUnsafe.getLong(buffer1, offset1Adj + (long) i);
//			long rw = theUnsafe.getLong(buffer2, offset2Adj + (long) i);
//			long diff = lw ^ rw;
//
//			if (diff != 0) {
//				if (!littleEndian) {
//					return (lw + Long.MIN_VALUE) < (rw + Long.MIN_VALUE) ? -1
//							: 1;
//				}
//
//				// Use binary search,一下省略若干代码
//            .....
//				return (int) (((lw >>> n) & 0xFFL) - ((rw >>> n) & 0xFFL));
//			}
//		}
//
//		// The epilogue to cover the last (minLength % 8) elements.
//		for (int i = minWords * Longs.BYTES; i < minLength; i++) {
//			int result = UnsignedBytes.compare(buffer1[offset1 + i],
//					buffer2[offset2 + i]);
//			if (result != 0) {
//				return result;
//			}
//		}
//		return length1 - length2;
//	}


	@Test
	public void testFile33333331() throws Exception {
		InputStream input = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace2.data");
		BufferedReader br = new BufferedReader(new InputStreamReader(input));

		String line;
		int count = 0;
		int min = 1000000;
		String strTmp = null;
		Map<Integer, Set<String>> map = new TreeMap<>();
		while ((line = br.readLine()) != null) {
			String[] split = line.split("\\|");
			String[] split1 = split[8].split("&");

			for (String str : split1) {
				int length = str.length();
				Set<String> set = map.get(length);
				if (set == null) {
					set = new HashSet<>();
					map.put(length, set);
				}
				set.add(str);
			}
			if (++count % 100000 == 0) {
				System.out.println(count);
			}
		}
		input.close();
		System.out.println("min is " + map);
		System.out.println("end!!!");
	}

	@Test
	public void testFile33333332() throws Exception {
		InputStream input = new FileInputStream("/Users/likangning/Downloads/trace1.data");
		BufferedReader br = new BufferedReader(new InputStreamReader(input));

		String line;
		int count = 0;
		Set<Integer> set = new HashSet<>();
		while ((line = br.readLine()) != null) {
			set.add(line.length());
			if (++count % 100000 == 0) {
				System.out.println(count);
			}
		}
		input.close();
		System.out.println("test " + set);
		System.out.println("end!!!");
	}
}

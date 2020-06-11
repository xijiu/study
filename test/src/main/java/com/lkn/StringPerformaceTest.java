package com.lkn;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
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
	public void test333333() throws Exception {
		long begin = System.currentTimeMillis();
		String path = "http://localhost:8010/trace1.data";
		URL url = new URL(path);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
		httpConnection.setUseCaches(true);
		InputStream input = httpConnection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(input));

		int count = 0;
		String line;
		while ((line = br.readLine()) != null) {
			count++;
		}
		input.close();
		System.out.println(count);
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

		int count = 0;
		String line;
		while ((line = br.readLine()) != null) {
			count++;
		}
		int lineNumber = br.getLineNumber();
		System.out.println(count);
		System.out.println(lineNumber);
		input.close();
		System.out.println(lineNumber);
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

	@Test
	public void byteTest() throws Exception {
		long begin = System.currentTimeMillis();
		String path = "http://localhost:8010/trace1.data";
		URL url = new URL(path);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
		httpConnection.setUseCaches(true);
		InputStream input = httpConnection.getInputStream();

		long count = 0;
		byte[] data = new byte[1024 * 1024 * 50];
		int byteNum;
		while ((byteNum = input.read(data)) != -1) {
			int beginIndex = 0;
			int endIndex = byteNum;
			while (beginIndex + 500 < endIndex) {
				for (int i = beginIndex; i < endIndex; i++) {
					if (data[i] == 10) {
						count++;
						beginIndex = i + 1;
						break;
					}
				}
			}
		}
		input.close();
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
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
		byte[] data = new byte[1024 * 1024 * 50];
		int byteNum;
		while ((byteNum = input.read(data)) != -1) {
			for (int i = 0; i < byteNum; i++) {
				if (data[i] == 10) {
					count++;
					break;
				}
			}
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
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 10000; i++) {
			map.put(String.valueOf(i), String.valueOf(i));
		}
		String line = null;
		long begin = System.currentTimeMillis();
//		for (int i = 0; i < 2000; i++) {
//			line = JSON.toJSONString(map);
//		}
		for (int i = 0; i < 2000; i++) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				sb.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
			}
			line = sb.toString();
		}
		System.out.println(line.length());
		System.out.println("cost : " + (System.currentTimeMillis() - begin));
	}


}

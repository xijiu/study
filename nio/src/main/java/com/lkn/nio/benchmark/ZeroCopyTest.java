package com.lkn.nio.benchmark;

import com.google.common.base.Splitter;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * 零拷贝测试
 *
 * @author likangning
 * @since 2019/10/30 下午8:05
 */
public class ZeroCopyTest {

	@Test
	public void directByteBufferTest() throws IOException, InterruptedException {
		File file = new File("/Users/likangning/Desktop/mqrace/test.index");
		FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
		ByteBuffer buffer = ByteBuffer.allocate(4 * 1024 * 1024);
		for (int i = 0; i < 1000; i++) {
			Thread.sleep(100);
			new Thread(() -> {
				try {
					fileChannel.read(buffer);
					buffer.clear();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

	@Test
	public void test() {
//		int a = 12;
//		int b = 24;
//		int c = a ^ b;
//		System.out.println(c);
//		System.out.println(Integer.toBinaryString(a));
//		System.out.println(Integer.toBinaryString(b));
//		System.out.println(Integer.toBinaryString(c));
//		System.out.println("-----");
//		System.out.println(Integer.toHexString(a));
//		System.out.println(Integer.toHexString(b));
//		System.out.println(Integer.toHexString(c));


//		Float a = 12F;
//		System.out.println(Float.toHexString(a));
//		System.out.println(Integer.toHexString(Float.floatToIntBits(a)));

		Double a = 12D;
		Long target1 = Long.valueOf(Long.toHexString(Double.doubleToLongBits(a)));
		System.out.println(target1);
		Double b = 24D;
		Long target2 = Long.valueOf(Long.toHexString(Double.doubleToLongBits(b)));
		System.out.println(target2);
		System.out.println(target1 ^ target2);
		System.out.println(Long.toHexString(target1 ^ target2));

		long l = Double.doubleToLongBits(a) ^ Double.doubleToLongBits(b);
		System.out.println(Long.toHexString(l));
		System.out.println(Long.toBinaryString(l));
		System.out.println(Long.toBinaryString(l).length());
//		System.out.println(Long.toHexString(Long.MAX_VALUE));
//		System.out.println(Long.toHexString(Long.MAX_VALUE).length());
	}


	@Test
	public void test1() throws IOException {
		ABC a = new ABC();
		System.out.println(a.getClass().getName());
	}

	private class ABC {

	}

}

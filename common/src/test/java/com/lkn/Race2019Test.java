package com.lkn;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * @author likangning
 * @since 2019/8/3 上午9:36
 */
public class Race2019Test {

	private static File file = new File("/Users/likangning/Downloads/new_blog/1.index");

	@Test
	public void writeTest() throws Exception {
		if (file.exists()) {
			file.delete();
			file.createNewFile();
		}
		RandomAccessFile indexFin = new RandomAccessFile(file, "rw");
		FileChannel channel = indexFin.getChannel();
		ByteBuffer byteBuffer = ByteBuffer.allocate(1);
		byte content = 25;
		byteBuffer.put(content);
		byteBuffer.flip();
		channel.write(byteBuffer);
		channel.close();
	}

	@Test
	public void readTest() throws Exception {
		RandomAccessFile indexFin = new RandomAccessFile(file, "rw");
		FileChannel channel = indexFin.getChannel();
		ByteBuffer byteBuffer = ByteBuffer.allocate(1);
		channel.read(byteBuffer);
		channel.close();
		byteBuffer.flip();
		byte b = byteBuffer.get();
		System.out.println(b);
	}

	/**
	 * a-t   [-500, 34861]
	 */
	@Test
	public void test3() {
//		int num = 65003;
//		short unsignedShort = intToUnsignedShort(num);
//		System.out.println(num);
//		System.out.println(getUnsignedShort(unsignedShort));

//		int num = 200;
//		System.out.println(num);
//		byte unsignedByte = intToUnsignedByte(num);
//		System.out.println(unsignedByte);
//		System.out.println(getUnsignedByte(unsignedByte));
		System.out.println(getUnsignedByte((byte) -1));
		System.out.println(getUnsignedShort((byte) -1));
	}

	public static int getUnsignedByte(byte data) {
		return data & 0x0FF;
	}

	public static int getUnsignedShort(short data) {
		return data & 0xFFFF;
	}

	public static long getUnsignedInt(int data) {
		return data & 0xFFFFFF;
	}

	public static short intToUnsignedShort(int data) {
		int diff = data - Short.MAX_VALUE;
		if (diff > 0) {
			return (short) (Short.MIN_VALUE + diff - 1);
		} else {
			return (short)data;
		}
	}

	public static byte intToUnsignedByte(int data) {
		int diff = data - Byte.MAX_VALUE;
		if (diff > 0) {
			return (byte) (Byte.MIN_VALUE + diff - 1);
		} else {
			return (byte)data;
		}
	}

	public static int getT(int a, short fileShort) {
		int diff = getUnsignedShort(fileShort);
		diff = diff - 500;
		return a - diff;
	}

	@Test
	public void bbbb() {
		long begin = System.currentTimeMillis();
		int num = Short.toUnsignedInt(Short.MAX_VALUE);
		for (int i = 0; i < 100000000; i++) {
			String s = Integer.toBinaryString(i);
		}
		long end = System.currentTimeMillis();
		System.out.println("耗时：" + (end - begin));
	}

	@Test
	public void bbbb2() {
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			int i1 = Integer.parseInt("11", 2);
			System.out.println(i1);
		}
		long end = System.currentTimeMillis();
		System.out.println("耗时：" + (end - begin));
	}


	@Test
	public void bbbb4() {
		System.out.println(Integer.toBinaryString(39861));
	}

	@Test
	public void bbbb3() {
//		short shortNum = 10;
//		int num = Short.toUnsignedInt(shortNum);
//		String str = Integer.toBinaryString(num);
//		System.out.println(str);
//		str = str + '0';
//		short short1 = Short.parseShort(str, 2);
//		System.out.println(short1);
		compress(1, 65530);
	}

	private short compress(int aDiff, int atDiff) {
		// 如果 a t 的差值为奇数
		if ((atDiff & 0x1) == 1) {
			return (short) (atDiff + aDiff);
		} else {
			// 如果是偶数的话，那么整体加10000
			return (short) (atDiff + aDiff + 10000);
		}
	}


	private int[] decompress(short target) {
		if (target < 10000) {
			if ((target & 0x1) == 1) {
				return new int[]{0, target};
			} else {
				return new int[]{1, target - 1};
			}
		} else {
			int targetInt = target - 10000;
			if ((targetInt & 0x1) == 1) {
				return new int[]{1, target - 1};
			} else {
				return new int[]{0, target};
			}
		}
	}

}

package com.lkn.race2019;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author likangning
 * @since 2019/9/2 上午8:07
 */
public class Compress {

	@Test
	public void test() {
		long begin = System.currentTimeMillis();
		long a = 1000000000L;
//		long a = Long.MIN_VALUE;
		List<Byte> list = writeVLong(a);
		System.out.println(list.size());
		Byte[] arr = new Byte[list.size()];
		list.toArray(arr);
		for (int i = 0; i < 1000000000; i++) {
			long result = readVLong(arr, 0);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - begin);

	}

	public static long readVLong(Byte[] bytes, int start) {
		int len = bytes[start];
		if (len >= -112) {
			return len;
		}
		boolean isNegative = (len < -120);
		len = isNegative ? -(len + 120) : -(len + 112);
		if (start + 1 + len > bytes.length) {
			throw new RuntimeException("Not enough number of bytes for a zero-compressed integer");
		}
		long i = 0;
		for (int idx = 0; idx < len; idx++) {
			i = i << 8;
			i = i | (bytes[start + 1 + idx] & 0xFF);
		}
		return (isNegative ? (~i) : i);
	}

	public static List<Byte> writeVLong(long target) {
		List<Byte> list = new ArrayList<>();
		// 如果在一个字节可以表示的范围内 直接返回
		if (target >= -112 && target <= 127) {
			list.add((byte) target);
			return list;
		}
		//把负数变成正数
		int len = -112;
		if (target < 0) {
			target ^= -1L; // take one's complement'
			len = -120;
		}

		//判断正数有几个位数 通过右移实现
		long tmp = target;
		while (tmp != 0) {
			tmp = tmp >> 8;
			len--;
		}

		// 写入第一个字节 该字节标识 这个数十正数还是负数 以及接下来有几个字节属于这个数
		list.add((byte) len);

		// 判断需要几个字节表示该数
		len = (len < -120) ? -(len + 120) : -(len + 112);

		//以每八位一组截取 成一个字节
		for (int idx = len; idx != 0; idx--) {
			int shiftBits = (idx - 1) * 8;
			long mask = 0xFFL << shiftBits;
			list.add((byte) ((target & mask) >> shiftBits));
		}
		return list;
	}

//	public static void writeVLong(ByteBuffer byteBuffer, long target) {
//// 如果在一个字节可以表示的范围内 直接返回
//		if (target >= -112 && target <= 127) {
//			stream.writeByte((byte)target);
//			return;
//		}
//		//把负数变成正数
//		int len = -112;
//		if (target < 0) {
//			target ^= -1L; // take one's complement'
//			len = -120;
//		}
//
//		//判断正数有几个位数 通过右移实现
//		long tmp = target;
//		while (tmp != 0) {
//			tmp = tmp >> 8;
//			len--;
//		}
//
//		// 写入第一个字节 该字节标识 这个数十正数还是负数 以及接下来有几个字节属于这个数
//		stream.writeByte((byte)len);
//
//		// 判断需要几个字节表示该数
//		len = (len < -120) ? -(len + 120) : -(len + 112);
//
////以每八位一组截取 成一个字节
//
//		for (int idx = len; idx != 0; idx--) {
//			int shiftbits = (idx - 1) * 8;
//			long mask = 0xFFL << shiftbits;
//			stream.writeByte((byte)((target & mask) >> shiftbits));
//		}
//	}
}

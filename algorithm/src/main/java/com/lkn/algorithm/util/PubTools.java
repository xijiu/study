package com.lkn.algorithm.util;

import java.util.Collection;

/**
 * @author likangning
 * @since 2018/6/22 下午3:50
 */
public class PubTools {
	public static boolean isEmptyCollection(Collection<?> targeCollection) {
		return targeCollection == null || targeCollection.size() == 0;
	}

	/**
	 * 字节数组转换为long类型
	 * @param b	字节数组
	 * @return	long类型
	 */
	public static long bytes2Long(byte[] b) {
		return bytes2Long(b, 0);
	}

	public static long bytes2Long(byte[] b, int offset) {
		long values = 0;
		for (int i = 0; i < 8; i++) {
			values <<= 8;
			values|= (b[i + offset] & 0xff);
		}
		return values;
	}

	/**
	 * 将long转换为字节数组
	 * @param res	long内容
	 * @return	对应的字节数组
	 */
	public static byte[] long2bytes(long res) {
		byte[] buffer = new byte[8];
		for (int i = 0; i < 8; i++) {
			int offset = 64 - (i + 1) * 8;
			buffer[i] = (byte) ((res >> offset) & 0xff);
		}
		return buffer;
	}

	/**
	 * int转换字节数组
	 */
	public static byte[] int2Bytes(int i) {
		byte[] targets = new byte[4];
		targets[3] = (byte) (i & 0xFF);
		targets[2] = (byte) (i >> 8 & 0xFF);
		targets[1] = (byte) (i >> 16 & 0xFF);
		targets[0] = (byte) (i >> 24 & 0xFF);
		return targets;
	}

	public static int bytes2Int(byte[] bytes) {
		return bytes2Int(bytes, 0);
	}

	public static int bytes2Int(byte[] bytes, int offset) {
		int b0 = bytes[offset] & 0xFF;
		int b1 = bytes[offset + 1] & 0xFF;
		int b2 = bytes[offset + 2] & 0xFF;
		int b3 = bytes[offset + 3] & 0xFF;
		return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
	}

	public static void main(String[] args) {
//		byte[] bytes = int2Bytes(-1);
//		int result = bytes2Int(bytes);
//		System.out.println(result);
		byte[] bytes1 = long2bytes(1);
		long l = bytes2Long(bytes1);
		System.out.println(l);


//		byte b = 1;
//		long values = 0;
//
//		values <<= 8;
//		values|= (b & 0xff);
//
//		b = 0;
//		values <<= 8;
//		values|= (b & 0xff);
//
//		System.out.println(values);
	}
}

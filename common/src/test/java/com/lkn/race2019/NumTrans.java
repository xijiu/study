package com.lkn.race2019;

/**
 * @author likangning
 * @since 2019/8/3 下午1:51
 */
public class NumTrans {

	public static int DIFF_A_T_ASSIST_NUM = 32763;

	public static int COMPRESS_ASSIST_NUM = 10000;

	private static ThreadLocal<int[]> INT_ARR_THREAD_LOCAL = ThreadLocal.withInitial(() -> new int[2]);

	/**
	 * 将有符号的byte转换为无符号的byte
	 */
	public static int byteToUnsigned(byte data) {
		return data & 0x0FF;
	}

	public static int shortToUnsigned(short data) {
		return data & 0xFFFF;
	}

	public static short recoverToShort(int data) {
		int diff = data - Short.MAX_VALUE;
		if (diff > 0) {
			return (short) (Short.MIN_VALUE + diff - 1);
		} else {
			return (short)data;
		}
	}

	public static byte recoverToByte(int data) {
		int diff = data - Byte.MAX_VALUE;
		if (diff > 0) {
			return (byte) (Byte.MIN_VALUE + diff - 1);
		} else {
			return (byte)data;
		}
	}

	/**
	 * 内存压缩
	 * @param aDiff	前后两个a的差值
	 * @param atDiff	消息中，a-t的差值
	 * @return	压缩后的值
	 */
	public static short compress(int aDiff, int atDiff) {
		if (aDiff == 0) {
			return (short) atDiff;
		} else {
			return (short) -atDiff;
		}
	}

	/**
	 * 解压缩
	 * @param target	目标值
	 * @return	aDiff 与 atDiff
	 */
	public static int[] decompress(short target) {
		int[] arr = new int[2];
		if (target > 0) {
			arr[0] = 0;
			arr[1] = target;
		} else {
			arr[0] = 1;
			arr[1] = -target;
		}
		return arr;
	}
}

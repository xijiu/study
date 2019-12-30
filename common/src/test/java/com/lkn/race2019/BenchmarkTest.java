package com.lkn.race2019;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author likangning
 * @since 2019/8/12 下午10:20
 */
public class BenchmarkTest {

	private int msgLength = 228000000;

	private int baseA = 200000;

	private int helperNum = 32770;

	private int a_t_maxGap = 34861;

	private short[] msgArr = new short[msgLength];

//	@Test
//	public void test2() {
//		short compress = NumTrans.compress(0, 10230);
//		int[] decompress = NumTrans.decompress(compress);
//		System.out.println(decompress[0]);
//		System.out.println(decompress[1]);
//	}

	@Test
	public void test() {
		createData();
		query();
	}

	private void query() {
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 300000; i++) {
			int beginA = genericBeginA();
			int endA = genericEndA(beginA);
			int beginT = genericBeginT(beginA);
			int endT = genericEndT(beginT);
			long avgVal = avg(beginA, endA, beginT, endT);
//			System.out.println(beginA + ", " + endA + ", " + beginT + ", " + endT + " : " + avgVal);
		}
		long end = System.currentTimeMillis();
		System.out.println("查询耗时： " + (end - begin));
	}

	private long avg(int beginA, int endA, int beginT, int endT) {
		long sum = 0;
		int beginIndex = beginA - baseA;
		beginIndex = beginIndex - 1024;
		beginIndex = Integer.max(beginIndex, 0);
		int lastA = beginIndex + baseA;
		int times = 1;
		for (int i = beginIndex; i < msgArr.length; i++) {
			int[] decompressArr = NumTrans.decompress(msgArr[i]);
			int[] decompressResult = doBusinessDecompress(decompressArr, lastA);
//			int aDiff = decompressArr[0];
//			int atDiff = decompressArr[1];
//			int a = lastA + aDiff;
//			int t = a - helperNum - atDiff;
			int a = decompressResult[0];
			int t = decompressResult[1];
			lastA = a;


			if (a < beginA) {
				continue;
			}
			if (a > endA) {
				break;
			}
			sum += a;
			times++;
			if (t >= beginT && t <= endT) {
				sum += a;
				times++;
			}

		}
		if (times == 0) {
			return 0;
		} else {
			return sum / times;
		}
	}

	private int[] doBusinessDecompress(int[] decompressArr, int lastA) {
		int[] result = new int[2];
		int aDiff = decompressArr[0];
		int atDiff = decompressArr[1];
		int a = lastA + aDiff;
		int t = a - helperNum - atDiff;
		result[0] = a;
		result[1] = t;
		return result;
	}

	private int genericEndT(int beginT) {
		return beginT + ThreadLocalRandom.current().nextInt(20000);
	}

	private int genericBeginT(int beginA) {
		return beginA - a_t_maxGap + ThreadLocalRandom.current().nextInt(100);
	}

	private int genericEndA(int beginA) {
		return beginA + ThreadLocalRandom.current().nextInt(20000);
	}

	private int genericBeginA() {
		return ThreadLocalRandom.current().nextInt(baseA, msgLength + baseA - 1);
	}

	private void createData() {
		long begin = System.currentTimeMillis();
		int arrIndex = 0;
		int lastA = baseA;
		for (int i = baseA; i < Integer.MAX_VALUE; i++) {
			int a = i;
			int t = randomT(a);
			msgArr[arrIndex++] = NumTrans.compress(a - lastA, a - t - helperNum);

			lastA = i;
			if (arrIndex >= msgArr.length) {
				break;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("造数耗时： " + (end - begin));
	}

	private int randomT(int a) {
		return ThreadLocalRandom.current().nextInt(a - a_t_maxGap, a - 32773);
	}
}

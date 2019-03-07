package com.lkn.common.ali_bixuan;

import org.junit.Test;

/**
 * 测试场景，比较synchronized的耗时情况
 *
 * 循环累加一个long变量，一直累加到{@code cycleTime}
 * 		无synchronized耗时：3ms
 * 		有synchronized耗时：260ms
 *
 * 结论：可见悲观锁synchronized是相当耗时的
 *
 * @author likangning
 * @since 2019/3/1 上午10:11
 */
public class SynchronizedTest {

	private int cycleTime = 10000000;

	@Test
	public void test() {
		synchronizedTest();
		non();
	}
	
	private void non() {
		long begin = System.currentTimeMillis();
		int num = 0;
		for (int i = 0; i < cycleTime; i++) {
			num++;
		}
		System.out.println(num);
		long end = System.currentTimeMillis();
		System.out.println("没有synchronized耗时： " + (end - begin));
	}

	private void synchronizedTest() {
		long begin = System.currentTimeMillis();
		int num = 0;
		for (int i = 0; i < cycleTime; i++) {
			synchronized (SynchronizedTest.class) {
				num++;
			}
		}
		System.out.println(num);
		long end = System.currentTimeMillis();
		System.out.println("有synchronized耗时： " + (end - begin));
	}

}

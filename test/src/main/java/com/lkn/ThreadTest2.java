package com.lkn;

import org.junit.Test;

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

	@Test
	public void abc() throws InterruptedException {
		final P1 v = new P1();
		// 线程 1：设置 b = 0
		final Thread t1 = new Thread(() -> {
			while (true) {
				System.out.println("set1");
				v.set1();
			}
		});
		t1.start();

		// 线程 2：设置 b = -1
		final Thread t2 = new Thread(() -> {
			while (true) {
				System.out.println("set2");
				v.set2();
			}
		});
		t2.start();

		// 线程 3：检查 0 != b && -1 != b
		final Thread t3 = new Thread(() -> {
			while (true) {
				v.check();
			}
		});
		t3.start();

		t1.join();
		t2.join();
		t3.join();
	}

}

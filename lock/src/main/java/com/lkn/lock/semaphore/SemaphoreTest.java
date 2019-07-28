package com.lkn.lock.semaphore;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author likangning
 * @since 2019/6/28 下午3:45
 */
public class SemaphoreTest {

	private static Semaphore semaphore = new Semaphore(5);

	private static AtomicInteger atomicInteger = new AtomicInteger(0);

	private static class MyThread extends Thread {

		@Override
		public void run() {
			try {
				while (true) {
					semaphore.acquire();
					sleep(13);
					atomicInteger.incrementAndGet();
					semaphore.release();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void bbb() {
		for (int i = 0; i < 5; i++) {
			new MyThread().start();
		}
		sleep(5000);
		System.out.println(atomicInteger.get());
		System.exit(1);
	}

	@Test
	public void cccc() {
		Random rng = new Random(2019);
		int sum = 0;
		int times = 100;
		for (int i = 0; i < times; i++) {
			double u = rng.nextDouble();
			int x = 0;
			double cdf = 0;
			while (u >= cdf) {
				x++;
				cdf = 1 - Math.exp(-1.0D * 1 / 46.12780363944139D * x);
			}
			sum += x;
		}
		System.out.println(sum / times);
	}
}

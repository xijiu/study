package com.lkn.multithreading;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author likangning
 * @since 2019/6/30 下午6:47
 */
public class ConcurrentTest {
	private Random rng = new Random(2019);

	private Semaphore semaphore = new Semaphore(20);

	private AtomicInteger num = new AtomicInteger();

	/**
	 * 20 : 6800左右
	 * 40 : 6744
	 * 60 : 6786
	 * 80 : 6758
	 */
	@Test
	public void test() throws Exception {
		int threadNum = 80;
		ExecutorService pool = Executors.newFixedThreadPool(threadNum);
		for (int i = 0; i < threadNum; i++) {
			pool.submit((Runnable) () -> {
				while (true) {
					invokeOnce();
				}
			});
		}

		Thread.sleep(2000);
		num.set(0);
		Thread.sleep(20);
		int result = num.getAndSet(0);
		System.out.println(result);
		System.out.println("总通过数：" + num);
		System.exit(1);
	}

	private void invokeOnce() {
		try {
			semaphore.acquire();
			long rtt = nextRTT();
			Thread.sleep(rtt);
			num.incrementAndGet();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			semaphore.release();
		}
	}

	private long nextRTT() {
		double u = rng.nextDouble();
		int x = 0;
		double cdf = 0;
		while (u >= cdf) {
			x++;
			cdf = 1 - Math.exp(-1.0D * 1 / 12.38232D * x);
		}
		return x;
	}

	@Test
	public void aaaa() throws Exception {
		AtomicInteger num = new AtomicInteger(0);
//		ExecutorService executorService = Executors.newCachedThreadPool();
		long begin = System.currentTimeMillis();
		Thread[] arr = new Thread[1000];
		for (int i = 0; i < 1000; i++) {
			Thread thread = new Thread(() -> {
				for (int i1 = 0; i1 < 10000; i1++) {
					num.incrementAndGet();
					num.incrementAndGet();
					num.decrementAndGet();
				}
			});
			thread.start();
			arr[i] = thread;
		}
		for (int i = 0; i < arr.length; i++) {
			arr[i].join();
		}
		System.out.println(num.get());
		System.out.println(System.currentTimeMillis() - begin);
	}
}

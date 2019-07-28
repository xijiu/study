package com.lkn.lock.semaphore;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author likangning
 * @since 2019/6/20 上午9:24
 */
public class SemaphoreQueueTest {

	/** 队列大小 */
	private static final int QUEUE_SIZE = 3;
	private static ExecutorService executorService = Executors.newFixedThreadPool(QUEUE_SIZE);
	private static Semaphore semaphore = new Semaphore(QUEUE_SIZE);

	@Test
	public void test() throws InterruptedException {
		executorService.submit(new Invoker(1));
		executorService.submit(new Invoker(2));
		executorService.submit(new Invoker(3));
		executorService.shutdown();
		Thread.sleep(9999999999999L);
	}

	private static class Invoker implements Runnable {
		/** 1:small 2:medium 3:large */
		private int type;
		private Invoker(int type) {
			this.type = type;
		}

		@Override
		public void run() {
			try {
				while (true) {
					semaphore.acquire(1);
					if (type == 1) {
						sleep(600);
					} else if (type == 2) {
						sleep(400);
					} else {
						sleep(200);
					}
					System.out.println(type + " 处理了一条请求");
					semaphore.release(1);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		public void sleep(long time) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testShare() throws Exception {
		Thread thread1 = new Thread(() -> {
			try {
				semaphore.acquire();
				semaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		thread1.start();
		thread1.join();

		System.out.println(semaphore.availablePermits());

		Thread thread2 = new Thread(() -> {
			semaphore.release();
			semaphore.release();
			semaphore.release();
		});
		thread2.start();
		thread2.join();
		System.out.println(semaphore.availablePermits());
	}

	private Random rng = new Random(2019);

	private long nextRTT() {
		double u = rng.nextDouble();
		int x = 0;
		double cdf = 0;
		while (u >= cdf) {
			x++;
			cdf = 1 - Math.exp(-1.0D * 1 / 46.92323D * x);
		}
		return x;
	}

	@Test
	public void testShare2() throws Exception {
		LongAdder counter = new LongAdder();
		LongAdder timeCounter = new LongAdder();
		long begin = System.currentTimeMillis();
		int permitSize = 435;
		int threadNum = 1235;
		Semaphore semaphore = new Semaphore(permitSize);
		Thread[] threads = new Thread[threadNum];
		for (int i = 0; i < threadNum; i++) {
			Thread thread = new Thread(() -> {
				try {
					for (int j = 0; j < 100; j++) {
						long begin2 = System.currentTimeMillis();
						semaphore.acquire();
						long rtt = nextRTT();
						Thread.sleep(rtt);
						semaphore.release();
						long cost = System.currentTimeMillis() - begin2;
						timeCounter.add(cost);
						counter.increment();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			thread.start();
			threads[i] = thread;
		}

//		new Thread() {
//			public void run() {
//				while (true) {
//					try {
//						long begin = System.currentTimeMillis();
//						for (int j = 0; j < 10; j++) {
//							Method method = Thread.class.getDeclaredMethod("getThreads");
//							method.setAccessible(true);
//							Thread[] threads = (Thread[])method.invoke(null);
//
//							Map<State, Integer> map = Maps.newHashMap();
//							for (int i = 0; i < threadNum; i++) {
//								State state = threads[i].getState();
//								map.merge(state, 1, (a, b) -> a + b);
//							}
//							System.out.println(map);
//						}
//						long cost = System.currentTimeMillis() - begin;
//						System.out.println("耗时1： " + cost);
//
//						begin = System.currentTimeMillis();
//						Thread.getAllStackTraces();
//						cost = System.currentTimeMillis() - begin;
//						System.out.println("耗时2： " + cost);
//						System.out.println();
//
//						Thread.sleep(2000);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();



		for (int i = 0; i < permitSize; i++) {
			threads[i].join();
		}

		long end = System.currentTimeMillis();
		long totalCost = end - begin;


		System.out.println("tps is " + ((double)counter.sum() / (double)totalCost));
		System.out.println(timeCounter.sum() / counter.sum());
	}

	@Test
	public void bb() throws InterruptedException {
		System.out.println("程序进入时间 " + new Date().toLocaleString());

		ScheduledExecutorService scheduler =
				new ScheduledThreadPoolExecutor(1, Executors.defaultThreadFactory());

		scheduler.schedule(() -> {
			System.out.println(new Date().toLocaleString() + " 调度一次");
		}, 2, TimeUnit.SECONDS);

		Thread.sleep(1000);

		System.out.println("准备执行第二次调度时间 " + new Date().toLocaleString());
		scheduler.schedule(() -> {
			System.out.println(new Date().toLocaleString() + "  调度二次");
		}, 2, TimeUnit.SECONDS);

		Thread.sleep(5000);
		System.out.println("over");
	}

	private static int SUIT_MAX_THREAD_NUM = -1;
	private static ScheduledExecutorService scheduler =
			new ScheduledThreadPoolExecutor(1, Executors.defaultThreadFactory());
	@Test
	public void test2() throws InterruptedException {
		SUIT_MAX_THREAD_NUM = 5;
		// 2秒后恢复原状
		scheduler.schedule(() -> {SUIT_MAX_THREAD_NUM = 10;}, 2, TimeUnit.SECONDS);
		System.out.println(SUIT_MAX_THREAD_NUM);
		Thread.sleep(1000);
		System.out.println(SUIT_MAX_THREAD_NUM);
	}







}

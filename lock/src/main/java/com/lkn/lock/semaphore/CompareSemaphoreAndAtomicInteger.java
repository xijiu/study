package com.lkn.lock.semaphore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicInteger与Semaphore性能比较
 * 结论：当前场景下AtomicInteger的平均耗时为 5.5s， Semaphore的平均耗时为26s。 高下立现
 * @author likangning
 * @since 2019/7/28 下午12:20
 */
public class CompareSemaphoreAndAtomicInteger {
	// 并发度
	private int concurrentNum = 10000;

	private int threadNum = 100;

	/** 每个线程的执行次数 */
	private int perThreadExeTimes = 1000000;

	private ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

	private Semaphore semaphore = new Semaphore(concurrentNum);

	private long beginTime;

	@Before
	public void before() {
		beginTime = System.currentTimeMillis();
	}

	@After
	public void after() {
		long endTime = System.currentTimeMillis();
		System.out.println("耗时： " + (endTime - beginTime));
	}


	@Test
	public void atomicIntegerTest() {
		AtomicInteger atomicInteger = new AtomicInteger();
		for (int i = 0; i < threadNum; i++) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					for (int j = 0; j < perThreadExeTimes; j++) {
						atomicIntegerAcquire();
						atomicInteger.decrementAndGet();
					}
				}

				private void atomicIntegerAcquire() {
					int val = atomicInteger.getAndIncrement();
					if (val >= concurrentNum) {
						sleep(1);
						atomicIntegerAcquire();
					}
				}
			});
		}
		waitFinish();
	}


	@Test
	public void semaphoreTest() {
		for (int i = 0; i < threadNum; i++) {
			executorService.submit(() -> {
				for (int j = 0; j < perThreadExeTimes; j++) {
					try {
						semaphore.acquire();
						semaphore.release();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		waitFinish();
	}

	private void waitFinish() {
		executorService.shutdown();
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sleep(long time, int nanos) {
		try {
			Thread.sleep(time, nanos);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

package com.lkn.common.ali_bixuan;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 测试场景：AtomicLong 与 synchronized(long++) 性能比拼，从0累加到1亿，做到线程安全
 *
 * 通过修改 {@code THREAD_SIZE} 的值来模拟并发
 * 并发数 (1)
 * 		atomic: 728ms
 * 		sync:	2684ms
 *
 * 并发数 (10)
 * 		atomic: 3399ms
 * 		sync:	4310ms
 *
 * 并发数 (200)
 * 		atomic: 3418ms
 * 		sync:	4368ms
 *
 * 并发数 (2000)
 * 		atomic: 4277ms
 * 		sync:	3197ms
 *
 * 结论：不能一概而论的讲Atomic系列的性能比Synchronized更高，具体还要看业务场景；在高并发的场景下，悲观锁的效率会更高
 *
 * @author likangning
 * @since 2019/3/1 上午10:11
 */
public class Question11 {

	private AtomicLong atomicNumber = new AtomicLong(0);

	private long syncNumber = 0L;

	private long limitNum = 100000000L;

	private int THREAD_SIZE = 2000;


	@Test
	public void atomicTest() throws Exception {
		long begin = System.currentTimeMillis();
		Thread[] arr = new Thread[THREAD_SIZE];
		for (int i = 0; i < THREAD_SIZE; i++) {
			Thread thread = new Thread(() -> {
				randomSleep();
				while (true) {
					if (atomicNumber.incrementAndGet() > limitNum) {
						break;
					}
				}
			});
			arr[i] = thread;
			thread.start();
		}
		System.out.println("所有线程均已启动");
		for (Thread thread : arr) {
			thread.join();
		}
		long end = System.currentTimeMillis();
		System.out.println("最终结果：" + atomicNumber.get());
		System.out.println("乐观锁耗时： " + (end - begin));
	}

	private void randomSleep() {
		try {
			Thread.sleep((long) (Math.random() * 20));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void synchronizedTest() throws Exception {
		long begin = System.currentTimeMillis();
		Thread[] arr = new Thread[THREAD_SIZE];
		for (int i = 0; i < THREAD_SIZE; i++) {
			Thread thread = new Thread(() -> {
				randomSleep();
				while (true) {
					synchronized (Question11.class) {
						if (syncNumber++ > limitNum) {
							break;
						}
					}
				}
			});
			arr[i] = thread;
			thread.start();
		}
		System.out.println("所有线程均已启动");
		for (Thread thread : arr) {
			thread.join();
		}
		long end = System.currentTimeMillis();
		System.out.println("最终结果：" + syncNumber);
		System.out.println("悲观锁耗时： " + (end - begin));
	}

}

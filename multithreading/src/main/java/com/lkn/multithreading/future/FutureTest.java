package com.lkn.multithreading.future;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author likangning
 * @since 2019/6/12 上午8:35
 */
public class FutureTest {

	@Test
	public void test()  {
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<Integer> result = executor.submit(new Callable<Integer>() {
			public Integer call() throws Exception {
				Thread.sleep(2000);
				return new Random().nextInt();
			}
		});
		executor.shutdown();

		try {
			System.out.println("result:" + result.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}

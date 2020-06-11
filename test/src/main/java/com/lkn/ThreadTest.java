package com.lkn;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Semaphore;

/**
 * @author likangning
 * @since 2020/5/14 下午2:25
 */
public class ThreadTest {
	private static int size = 3;
	private static Semaphore semaphore = new Semaphore(size);
	private static List<String>[] arr = new List[size];

	@Test
	public void aaa() throws Exception {
		Thread thread1 = new Thread(() -> {
			int index = 0;
			try {
				for (int i = 0; i < 1000; i++) {
					semaphore.acquire();
					arr[index] = Lists.newArrayList("a", "b", "c", String.valueOf(i));
					index = (index + 1) % size;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		Thread thread2 = new Thread(() -> {
			int times = 0;
			for (int i = 0; i < 100; i++) {
				for (int j = 0; j < arr.length; j++) {
					if (arr[j] == null) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("无数据");
						j--;
					} else {
						System.out.println(++times + " :::: " + arr[j]);
						arr[j] = null;
						semaphore.release();
					}
					System.out.println("剩余信号量：" + semaphore.availablePermits());
				}
			}
		});

		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();
	}


	@Test
	public void bbb() {
		StringBuilder sb = new StringBuilder();
		for (int i = 7; i < 1024; i = i+8) {
			String tableName = "tb_winfo_";
			int length = String.valueOf(i).length();
			for (int j = 0; j < 4 - length; j++) {
				tableName = tableName + "0";
			}
			tableName += i;
			String str = "ALTER TABLE `" + tableName + "` ADD COLUMN `extend` TEXT NULL;";
			sb.append(str);
//			System.out.println(str);
		}
		System.out.println(sb.toString());
	}
}

package com.lkn.lock;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 信号量
 * 信号量不足时，将线程挂起等待，直至有足够的信号量
 * @author LiKangning
 * @since 2018/3/13 上午11:02
 */
public class SemaphoreTest {
    private static Semaphore semaphore = new Semaphore(5);

    @Test
    public void test() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 11; i++) {
            final int index = i + 1;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
//                        System.out.println(currentTime() + " 线程---> " + index + " 准备获取信号量");
                        semaphore.acquire(3);
                        System.out.println(Tools.currentTime() + " 线程---> " + index + " 已经获取信号量");
                        Thread.sleep(2000);
                        semaphore.release(3);
                        System.out.println(Tools.currentTime() + " 线程---> " + index + " 已经释放信号量");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

}

package com.lkn.lock;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author LiKangning
 * @since 2018/3/13 上午11:53
 */
public class CountDownLatchTest {
    private static CountDownLatch countDownLatch = new CountDownLatch(8);

    @Test
    public void test() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 8; i++) {
            final int index = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Tools.randomSleep();
                    countDownLatch.countDown();
                    System.out.println(index + "准备就绪");
                }
            });
        }

        countDownLatch.await();
        System.out.println("\r\n===> 火箭发射!!!");
    }
}

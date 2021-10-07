package com.lkn.lock;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 多线程计数器
 * 场景举例：
 *      火箭发射前需要8个步骤，这8个步骤分别起8个线程分别执行，
 *      等这8个线程都执行完毕后，开始执行火箭发射的操作
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
//                    Tools.sleep(2000);
                    countDownLatch.countDown();
                    System.out.println(index + "准备就绪");
                }
            });
        }

        countDownLatch.await(10, TimeUnit.SECONDS);

        System.out.println("\r\n===> 火箭发射!!!");


        ExecutorService executorService2 = Executors.newCachedThreadPool();
        for (int i = 0; i < 8; i++) {
            final int index = i;
            executorService2.execute(new Runnable() {
                @Override
                public void run() {
                    Tools.randomSleep();
//                    Tools.sleep(2000);
                    countDownLatch.countDown();
                    System.out.println(index + "准备就绪");
                }
            });
        }

        countDownLatch.await(10, TimeUnit.SECONDS);

        System.out.println("\r\n===> 2222 火箭发射!!!");
    }
}

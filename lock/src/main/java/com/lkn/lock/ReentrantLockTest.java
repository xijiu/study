package com.lkn.lock;

import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LiKangning
 * @since 2018/3/7 上午7:25
 */
public class ReentrantLockTest extends TimeCostTest{
    private static ReentrantLock lock = new ReentrantLock();
    private static int index = 0;

    /**
     * 耗时 1135 1235
     */
    @Test
    public void threadsTest() throws InterruptedException {
        int threadNum = 5;
        final int cycleNum = 10000000;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < cycleNum; j++) {
                        lock.lock();
                        index++;
                        lock.unlock();
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        System.out.println("最后的结果为 ： " + index);
    }



    @Test
    public void casTest() throws InterruptedException {
        final AtomicInteger atomicIndex = new AtomicInteger();
        int threadNum = 5;
        final int cycleNum = 10000000;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < cycleNum; j++) {
                        atomicIndex.incrementAndGet();
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        System.out.println("最后的结果为 ： " + atomicIndex.intValue());
    }


    private static ReentrantLock lock1 = new ReentrantLock();
    private static ReentrantLock lock2 = new ReentrantLock();
    @Test
    public void deadLock() throws InterruptedException {
        Thread thread1 = new Thread("线程1") {
            public void run() {
                try {
                    lock1.lockInterruptibly();
                    Thread.sleep(1000);
                    lock2.lockInterruptibly();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread2 = new Thread("线程2") {
            public void run() {
                try {
                    lock2.lockInterruptibly();
                    Thread.sleep(900);
                    lock1.lockInterruptibly();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        threadMXBean.findDeadlockedThreads();

    }

}

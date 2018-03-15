package com.lkn.lock;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author LiKangning
 * @since 2018/3/6 下午3:41
 */
public class WaitNotifyTest {
    private volatile int sum = 0;
    private volatile int index = 1;
    private volatile int maxIndex = 200;
    private static final Object LOCK = new Object();

    private void sleepRandom() {
        try {
            Thread.sleep(new Random().nextInt(50));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class Thread1 implements Runnable {
        @Override
        public void run() {
            Thread.currentThread().setName("奇数线程");
            while (true) {
                if (index % 2 == 1) {
                    int temp = lockAndWait();
                    synchronized (LOCK) {
                        LOCK.notifyAll();
                        if (temp == maxIndex - 1) {
                            break;
                        }
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    continue;
                }
            }
            System.out.println("线程 ： " + Thread.currentThread().getName() + " 结束");
        }
    }

    private class Thread2 implements Runnable {
        @Override
        public void run() {
            Thread.currentThread().setName("偶数线程");
            // 开始首先等待，不执行后续逻辑，等待线程1唤醒
            synchronized (LOCK) {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (true) {
                if (index % 2 == 0) {
                    int temp = lockAndWait();
                    synchronized (LOCK) {
                        LOCK.notifyAll();
                        if (temp == maxIndex) {
                            break;
                        }
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    continue;
                }
            }

            System.out.println("线程 ： " + Thread.currentThread().getName() + " 结束");
        }
    }

    private int lockAndWait() {
        int temp = index;
        sum += index++;
        System.out.println(Thread.currentThread().getName() + " : 当前为 " + temp + " 累加");
        sleepRandom();
        return temp;
    }

    @Test
    public void test() throws InterruptedException {
        Thread t1 = new Thread(new Thread1());
        Thread t2 = new Thread(new Thread2());
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("最后结果 ： " + sum);
    }


    @Test
    public void notifyTest() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 3; i++) {
            final int index = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Thread.currentThread().setName("加锁线程" + (index + 1));
                    synchronized (LOCK) {
                        try {
                            System.out.println(Thread.currentThread().getName() + " 准备进入等待操作");
                            LOCK.wait();
                            System.out.println(Thread.currentThread().getName() + " 结束等待");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }


        for (int i = 0; i < 4; i++) {
            final int index = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Thread.currentThread().setName("解锁线程" + (index + 1));
                    synchronized (LOCK) {
                        LOCK.notify();
                    }
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }





}

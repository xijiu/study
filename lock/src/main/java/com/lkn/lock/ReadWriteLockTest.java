package com.lkn.lock;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author LiKangning
 * @since 2018/3/13 上午11:20
 */
public class ReadWriteLockTest {
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    private static ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

    @Test
    public void test() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 500; i++) {
            final int index = i + 1;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    readLock.lock();
                    System.out.println(Tools.currentTime() + index + "开始读取数据");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Tools.currentTime() + index + "即将结束读取数据");
                    readLock.unlock();
                }
            });
        }

        for (int i = 0; i < 2; i++) {
            final int index = i + 1;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    writeLock.lock();
                    System.out.println(Tools.currentTime() + index + "开始写数据");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Tools.currentTime() + index + "即将结束写数据");
                    writeLock.unlock();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    @Test
    public void test2() throws InterruptedException {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                Thread.currentThread().setName("read 线程");
                readLock.lock();
                System.out.println(Tools.currentTime() + "开始读取数据");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Tools.currentTime() + "即将结束读取数据");
                readLock.unlock();
            }
        };

        Thread thread2 = new Thread() {
            @Override
            public void run() {
                Thread.currentThread().setName("write 线程");
                writeLock.lock();
                System.out.println(Tools.currentTime() + "开始写数据");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Tools.currentTime() + "即将结束写数据");
                writeLock.unlock();
            }
        };

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}

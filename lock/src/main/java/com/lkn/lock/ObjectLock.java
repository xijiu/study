package com.lkn.lock;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author LiKangning
 * @since 2018/3/2 上午8:49
 */
public class ObjectLock {
    //共享数据，只能有一个线程能写该数据，但可以有多个线程同时读该数据。
    private volatile String data = "_init_";
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void get() {
        // 上读锁，其他线程只能读不能写
        readWriteLock.readLock().lock();
        System.out.println(Thread.currentThread().getName() + " 已拿到读锁");
        sleep();
        System.out.println(Thread.currentThread().getName() + " 读取数据 :" + data);
        // 释放读锁
        readWriteLock.readLock().unlock();
    }

    public void put(String data) {
        // 上写锁，不允许其他线程读也不允许写
        readWriteLock.writeLock().lock();
        System.out.println(Thread.currentThread().getName() + " 已拿到写锁");
        sleep();
        this.data = data;
        System.out.println(Thread.currentThread().getName() + " 已经写入数据: " + data);
        // 释放写锁
        readWriteLock.writeLock().unlock();
    }

    private void sleep() {
        try {
            Thread.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void readAndWriteLockTest() throws Exception {
        int readNumber = 20;
        int writeNumber = 5;
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 读线程
        for (int i = 0; i < readNumber; i++) {
            final int num = i + 1;
            executorService.submit(new Runnable() {
                public void run() {
                    sleep();
                    Thread.currentThread().setName("read--" + num + "--");
                    get();
                }
            });
        }


        // 写线程
        for (int i = 0; i < writeNumber; i++) {
            final int num = i + 1;
            executorService.submit(new Runnable() {
                public void run() {
                    sleep();
                    Thread.currentThread().setName("write--" + num + "--");
                    put(String.valueOf(num));
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }


    // 啊劳动纠纷垃圾分类王 i 放假啊是否加拉加斯两地分居拉丝粉
    // 分辨率真的有这么高吗？我怎么不知道啊
}

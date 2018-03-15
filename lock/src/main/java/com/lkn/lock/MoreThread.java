package com.lkn.lock;

/**
 * @author LiKangning
 * @since 2018/3/6 下午6:14
 */


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 从1加到100
 * 两个线程轮流执行任务，线程1只负责奇数累加，线程2只负责偶数累加
 */
public class MoreThread {

    private static LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue();
    private static Lock lock = new ReentrantLock();
    private static int threadcount1 = 0;
    private static int threadcount2 = 0;

    public static void main(String[] args) throws InterruptedException {


        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    lock.lock();
                    Integer value = queue.peek();
                    if (value == null) {
                        lock.unlock();
                        continue;
                    }
                    if(value.equals(1000)){
                        lock.unlock();
                        break;
                    }
                    if (value % 2 == 1) {
                        value = queue.poll();
                        threadcount1 += value;
                    }
                    lock.unlock();
                }
            }
        }, "Thread-1");
        thread1.start();

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    lock.lock();
                    Integer value = queue.peek();
                    if (value == null) {
                        lock.unlock();
                        continue;
                    }
                    if(value.equals(1000)){
                        lock.unlock();
                        break;
                    }
                    if (value % 2 == 0) {
                        value = queue.poll();
                        threadcount2 += value;
                    }
                    lock.unlock();
                }
            }
        }, "Thread-2");
        thread2.start();

        for (int i = 1; i <= 100; i++) {
            queue.put(i);
        }

        queue.put(1000);
        queue.put(1000);

        thread1.join();
        thread2.join();

        System.out.println("Thread1:"+threadcount1);
        System.out.println("Thread2:"+threadcount2);

    }
}

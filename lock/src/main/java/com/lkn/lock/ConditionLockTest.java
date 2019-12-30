package com.lkn.lock;

import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 通常使用 {@link Condition} 类来替代 @{@code wait} 与 {@code notify}、{@code notifyAll} 操作
 * @author LiKangning
 * @since 2018/3/13 上午9:38
 */
public class ConditionLockTest {
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

    @Test
    public void test123() throws InterruptedException {
        Thread thread1 = new Thread("我是线程1") {
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + "  进入等待中");
                    lock.lock();
                    condition.await();
                    lock.unlock();
                    System.out.println(Thread.currentThread().getName() + "  等待结束");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread2 = new Thread("我是线程2"){
            public void run() {
                try {
                    Thread.sleep(2000);
                    System.out.println(Thread.currentThread().getName() + "  准备打断");
                    lock.lock();
                    condition.signal();
                    lock.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

}



class FooBar {
    private int n;

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private volatile boolean isFooTurn = false;

    public FooBar(int n) {
        this.n = n;
    }

    public void foo(Runnable printFoo) throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        semaphore.release();
        semaphore.drainPermits();

        for (int i = 0; i < n; i++) {
            lock.lock();
            if (!isFooTurn) {
                condition.await();
            }
            printFoo.run();
            isFooTurn = false;
            condition.signalAll();
            lock.unlock();
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            lock.lock();
            if (isFooTurn) {
                condition.await();
            }
            printBar.run();
            isFooTurn = true;
            condition.signalAll();
            lock.unlock();
        }
    }
}

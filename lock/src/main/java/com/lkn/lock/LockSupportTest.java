package com.lkn.lock;

import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport 的测试
 * 提供线程挂起与睡眠的功能
 *
 * @author LiKangning
 * @since 2018/3/13 下午4:44
 */
public class LockSupportTest {

    /**
     * 挂起后，通过unpark进行唤醒
     */
    @Test
    public void test() throws InterruptedException {
        Thread thread = new Thread() {
            public void run() {
                System.out.println("主人准备睡觉");
                LockSupport.park();
                System.out.println("主人被叫醒醒了，准备起床");
            }
        };

        thread.start();
        Thread.sleep(2000);
        System.out.println("准备去唤醒主人");
        LockSupport.unpark(thread);
        thread.join();
    }

    /**
     * 提前唤醒，在进行睡眠时直接跳过
     */
    @Test
    public void test1() throws InterruptedException {
        Thread thread = new Thread() {
            public void run() {
                Tools.sleep(2000);
                System.out.println(Tools.currentTime() + "主人准备睡觉");
                LockSupport.park();
                System.out.println(Tools.currentTime() + "主人被叫醒醒了，准备起床");
            }
        };

        thread.start();
        System.out.println(Tools.currentTime() + "准备去唤醒主人");
        LockSupport.unpark(thread);
        thread.join();
    }
}

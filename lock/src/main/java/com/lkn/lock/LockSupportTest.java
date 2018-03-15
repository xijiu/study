package com.lkn.lock;

import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

/**
 * @author LiKangning
 * @since 2018/3/13 下午4:44
 */
public class LockSupportTest {

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

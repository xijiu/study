package com.lkn.lock;

import org.junit.Test;

import java.util.concurrent.*;

/**
 * 循环栅栏
 * 场景举例：
 *      100个士兵为100个线程，100个士兵就绪完毕后，统一进行匍匐前进任务，100个都执行完毕后，再统一进行下一个任务
 * @author LiKangning
 * @since 2018/3/13 下午4:10
 */
public class CyclicBarrierTest {

    private static final int SOLDIER = 100;
    private CyclicBarrier cyclicBarrier = new CyclicBarrier(SOLDIER, new Runnable() {
        @Override
        public void run() {
            System.out.println("我是优先级别最高的线程");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("优先级别最高的线程结束");
        }
    });


    @Test
    public void test1() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < SOLDIER; i++) {
            final int index = i + 1;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("士兵 " + index + "就绪");
                        cyclicBarrier.await();
                        Tools.randomSleep();
                        System.out.println("士兵 " + index + "=====> 执行");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }
}

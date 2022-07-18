package stat;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xijiu
 * @since 2022/5/27 上午11:34
 */
public class CountDownLatchTest {

    private static final CountDownLatch countDownLatch = new CountDownLatch(3);

    public static void main(String[] args) throws InterruptedException {

        Thread mainThread = new Thread(() -> {
            try {
                System.out.println("我是主线程，等待其他线程执行完毕");
                countDownLatch.await();
                System.out.println("其他3个线程均执行完毕，开始执行主线程任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        mainThread.start();


        Thread thread1 = new Thread(() -> {
            System.out.println("我是线程1");
            sleep(1000);
            System.out.println("线程1执行完毕");
            countDownLatch.countDown();
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            System.out.println("我是线程2");
            sleep(500);
            System.out.println("线程2执行完毕");
            countDownLatch.countDown();
        });
        thread2.start();

        Thread thread3 = new Thread(() -> {
            System.out.println("我是线程3");
            sleep(1500);
            System.out.println("线程3执行完毕");
            countDownLatch.countDown();
        });
        thread3.start();

        mainThread.join();
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR = createThreadPool();

    private static ThreadPoolExecutor createThreadPool() {
        AtomicInteger num = new AtomicInteger();
        BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(100);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                20,
                200,
                1,
                TimeUnit.MINUTES,
                taskQueue,
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("TaskParallelExecuteDriver_" + num.incrementAndGet());
                    return thread;
                },
                new ThreadPoolExecutor.AbortPolicy());
        return poolExecutor;
    }

    @Test
    public void test2() throws Exception {
        Thread thread1 = new Thread(() -> {
            System.out.println("我是线程1");
            sleep(1000);
            if (1 == 1) {
                throw new RuntimeException();
            }
            System.out.println("线程1执行完毕");
            countDownLatch.countDown();
        });
        Future<?> future1 = THREAD_POOL_EXECUTOR.submit(thread1);

        Thread thread2 = new Thread(() -> {
            System.out.println("我是线程2");
            sleep(500);
            System.out.println("线程2执行完毕");
            countDownLatch.countDown();
        });
        Future<?> future2 = THREAD_POOL_EXECUTOR.submit(thread2);

        future1.get();
        future2.get();

        System.out.println("over");
    }
}

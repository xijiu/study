package yammer;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import lombok.SneakyThrows;
import org.junit.Test;

import javax.annotation.processing.Filer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xijiu
 * @since 2022/3/22 上午9:04
 */
public class MetricsTest {

    private static final long MINUTE_LEN = 60 * 1000;

    @Test
    public void test() {
        System.out.println(System.currentTimeMillis() / MINUTE_LEN * MINUTE_LEN);
    }

    @Test
    public void test2() {
        Timer timer = Metrics.newTimer(MetricsTest.class, "responses", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
        timer.update(1, TimeUnit.SECONDS);
        System.out.println(timer.sum());
    }

    @Test
    public void test3() {
        System.out.println(Long.MAX_VALUE / 1024 / 1024 / 1024 / 2 / 60 / 60 / 24);
    }


    @Test
    public void test4() {
        try {
            System.out.println(123);
            File file = new File("abc");
            int a = 1 / 0;
//            throw new RuntimeException("aaa");
        } finally {
            System.out.println("finish");
        }
    }

    @Test
    public void test5() {
        System.out.println(removeDuplicates(new int[]{1, 1, 2}));
    }

    public int removeDuplicates(int[] A) {
        if (A == null || A.length == 0) {
            return 0;
        }
        if (A.length == 1) {
            return 1;
        }
        int count = 1;
        int last = A[0];
        for (int i = 1; i < A.length; i++) {
            if (A[i] != last) {
                count++;
                last = A[i];
            }
        }
        return count;
    }

    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            3,
            30,
            1,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(100), r -> {
                Thread thread = new Thread(r);
                thread.setName("TaskParallelExecuteDriver_");
                return thread;
            });

    @Test
    public void test6() throws InterruptedException {
        AtomicInteger num = new AtomicInteger();
        for (int i = 0; i < 10; i++) {
            THREAD_POOL_EXECUTOR.submit(() -> {
                System.out.println("开始执行" + num.incrementAndGet());
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("结束执行");
            });
        }

        THREAD_POOL_EXECUTOR.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }
}

package thread_pool;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xijiu
 * @since 2022/3/29 下午6:13
 */
public class MyTest {
    private final static EagerThreadPoolExecutor THREAD_POOL_EXECUTOR = createThreadPool();

    private static EagerThreadPoolExecutor createThreadPool() {
        TaskQueue<Runnable> taskQueue = new TaskQueue<>(100);
        EagerThreadPoolExecutor poolExecutor = new EagerThreadPoolExecutor(
                3,
                30,
                1,
                TimeUnit.MINUTES,
                taskQueue, r -> {
            Thread thread = new Thread(r);
            thread.setName("TaskParallelExecuteDriver_");
            return thread;
        }, (r, executor) -> {

        });
        taskQueue.setExecutor(poolExecutor);
        return poolExecutor;
    }

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

    @Test
    public void test() {
        StringBuilder sb = new StringBuilder("123456789");
        sb.delete(sb.length() - 2, sb.length());
        System.out.println(sb);
        System.out.println("hologres_zyb_zbk_bzr_zyb_zbk_bzr_ads_ads_zbk_adl_recruit_activity".length());
    }
}

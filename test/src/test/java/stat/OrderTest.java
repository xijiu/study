package stat;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.lang3.ThreadUtils;
import org.junit.Before;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author xijiu
 * @since 2022/9/23 下午3:19
 */
public class OrderTest {

    private static boolean SYSTEM_BUSY_FLAG = false;

    @Before
    public void before() {
        System.out.println("before");
    }

    @Test
    public void test1() {
        System.out.println("i am test1");
    }

    @Test
    public void test2() {
        System.out.println("i am test2");
    }

    private boolean isSystemBusy() {
        OperatingSystemMXBean systemMXBean;
        java.lang.management.OperatingSystemMXBean system = ManagementFactory.getOperatingSystemMXBean();
        if (system instanceof OperatingSystemMXBean) {
            systemMXBean = (OperatingSystemMXBean) system;
        } else {
            SYSTEM_BUSY_FLAG = false;
            return SYSTEM_BUSY_FLAG;
        }

        double totalMemorySize = ((Long) (systemMXBean.getTotalPhysicalMemorySize())).doubleValue();
        double freeMemorySize = ((Long) (systemMXBean.getFreePhysicalMemorySize())).doubleValue();
        double freeSwapSize = ((Long) (systemMXBean.getFreeSwapSpaceSize())).doubleValue();
        boolean isMemoryBusy = (freeMemorySize + freeSwapSize) / totalMemorySize < 0.05D;
        int processors = systemMXBean.getAvailableProcessors();
        double loadAverage = systemMXBean.getSystemLoadAverage();
        boolean isLoadBusy = loadAverage / processors < 0.05D;
        SYSTEM_BUSY_FLAG = isMemoryBusy || isLoadBusy;
        return SYSTEM_BUSY_FLAG;
    }


    @Test
    public void test3333() {
        OrderTest orderTest = new OrderTest();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            orderTest.test555();
        }
        long cost = System.currentTimeMillis() - begin;
        System.out.println("cost time is : " + cost);
    }

    @Test
    public void test555() {
        OperatingSystemMXBean systemMXBean;
        java.lang.management.OperatingSystemMXBean system = ManagementFactory.getOperatingSystemMXBean();
        if (system instanceof OperatingSystemMXBean) {
            systemMXBean = (OperatingSystemMXBean) system;
        } else {
            System.out.println("error");
            return;
        }

        double totalMemorySize = ((Long) (systemMXBean.getTotalPhysicalMemorySize())).doubleValue();
        double freeMemorySize = ((Long) (systemMXBean.getFreePhysicalMemorySize())).doubleValue();
        double freeSwapSize = ((Long) (systemMXBean.getFreeSwapSpaceSize())).doubleValue();

        System.out.println("totalMemorySize : " + formatMemory(totalMemorySize));
        System.out.println("freeMemorySize : " + formatMemory(freeMemorySize));
        System.out.println("freeSwapSize : " + formatMemory(freeSwapSize));
        System.out.println("ratio : " + ((freeSwapSize + freeMemorySize) / totalMemorySize));

        int processors = systemMXBean.getAvailableProcessors();
        double loadAverage = systemMXBean.getSystemLoadAverage();

        if (totalMemorySize + freeMemorySize + freeSwapSize + processors + loadAverage < 0) {
            System.out.println(123);
        }
        System.out.println("processors : " + processors);
        System.out.println("loadAverage : " + loadAverage);

    }

    private String formatMemory(double value) {
        value = value / 1024 / 1024 / 1024;
        return String.format("%.2f", value);
    }

    @Test
    public void test() {
        List<String> list = new ArrayList<>();
    }


    public static ThreadFactory genThreadFactory(String name) {
        return new ThreadFactoryBuilder()
                .setNameFormat(name + "-%d").build();
    }

    @Test
    public void test23() throws InterruptedException {
        ThreadPoolExecutor releaseOrStopThreadPool = new ThreadPoolExecutor(
                5, 10,
                0, TimeUnit.NANOSECONDS,
                new LinkedBlockingDeque<>(200),
                genThreadFactory("releaseOrStopThreadPool-thread"),
                new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 210; i++) {
            int finalI = i;
            releaseOrStopThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("i am " + finalI);
                    try {
                        Thread.sleep(1000000000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        releaseOrStopThreadPool.shutdown();
        releaseOrStopThreadPool.awaitTermination(10000000L, TimeUnit.SECONDS);
    }


    protected enum Serial {
        n1,
        n2,
        n3,
        ;
        public final int n;

        Serial() {
            String substring = this.toString().substring(1);
            this.n = Integer.parseInt(substring);
        }
    }
}

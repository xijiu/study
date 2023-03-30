package stat;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sun.management.OperatingSystemMXBean;
import lombok.Getter;
import org.junit.Test;

import java.io.File;

import java.io.FileWriter;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class GraphUtil {

    public GraphUtil() {
        System.out.println("GraphUtil init");
    }

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    public static void main(String[] args) {
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("a");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("b");
        }, 0,1, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("a1");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("b1");
        }, 0,1, TimeUnit.SECONDS);
    }

    private LoadingCache<String, String> instanceCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
//            .expireAfterAccess(4, TimeUnit.SECONDS)
            .refreshAfterWrite(1, TimeUnit.SECONDS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String instanceId) throws Exception {
                    System.out.println("load");
                    return instanceId + "_tail";
                }

                @Override
                public ListenableFuture<String> reload(String key, String oldValue) throws Exception {
                    return Futures.immediateFuture(load(key));
                }
            });

    @Test
    public void test() throws InterruptedException {
        instanceCache.put("abc", "abc_abc");
        String abc = instanceCache.getUnchecked("abc");
        System.out.println(abc);
        Thread.sleep(20000);
        System.out.println(instanceCache.getUnchecked("abc"));
    }

    @Getter
    private static class User {
        private String user1 = "null";
        private String user2 = null;
    }


    @Test
    public void test2() throws InterruptedException {
        User user = new User();
        boolean result = user.getUser1() == null ^ user.getUser2() != null;
        System.out.println(result);
//        System.out.println(true ^ true);
//        System.out.println(true ^ false);
//        System.out.println(false ^ true);
//        System.out.println(false ^ false);
    }


    @Test
    public void test555() {
        System.out.println(isSystemBusy());
    }


    /**
     * judge whether the current system is busy
     *
     * @return  true: busy    false: not busy
     */
    private boolean isSystemBusy() {
        OperatingSystemMXBean systemMXBean;
        java.lang.management.OperatingSystemMXBean system = ManagementFactory.getOperatingSystemMXBean();
        if (system instanceof OperatingSystemMXBean) {
            systemMXBean = (OperatingSystemMXBean) system;
        } else {
            return false;
        }
        return isMemoryBusy(systemMXBean) || isLoadBusy(systemMXBean);
    }

    private boolean isLoadBusy(OperatingSystemMXBean systemMXBean) {
        int processors = systemMXBean.getAvailableProcessors();
        double loadAverage = systemMXBean.getSystemLoadAverage();
        boolean isLoadBusy = loadAverage / processors > 0.9D;
        if (true) {
            System.out.println(String.format("load busy, processors %s, loadAverage %s", processors, loadAverage));
        }
        return isLoadBusy;
    }

    private boolean isMemoryBusy(OperatingSystemMXBean systemMXBean) {
        double totalMemorySize = ((Long) (systemMXBean.getTotalPhysicalMemorySize())).doubleValue();
        double freeMemorySize = ((Long) (systemMXBean.getFreePhysicalMemorySize())).doubleValue();
        double freeSwapSize = ((Long) (systemMXBean.getFreeSwapSpaceSize())).doubleValue();

        boolean isMemoryBusy = (freeMemorySize + freeSwapSize) / totalMemorySize < 0.05D;
        if (true) {
            System.out.println(String.format("memory busy, totalMemorySize %s, freeMemorySize %s, freeSwapSize %s",
                    formatMemory(totalMemorySize), formatMemory(freeMemorySize), formatMemory(freeSwapSize)));
        }
        return isMemoryBusy;
    }

    private String formatMemory(double memory) {
        memory = memory / 1024 / 1024 / 1024;
        return String.format("%.2f", memory);
    }

    private static final int num = 5;

    @Test
    public void test5() throws Exception {
        Field field = GraphUtil.class.getDeclaredField("num");
        field.setAccessible(true);
        field.getModifiers();

        field.set(null, 50);
        System.out.println(field.get(null));
    }

    public static void modify(Object object, String fieldName, Object newFieldValue) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true); //Field 的 modifiers 是私有的
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        if(!field.isAccessible()) {
            field.setAccessible(true);
        }

        field.set(object, newFieldValue);
    }
}




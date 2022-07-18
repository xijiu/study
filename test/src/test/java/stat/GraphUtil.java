package stat;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.junit.Test;

import java.awt.*;

import java.io.File;

import java.io.FileWriter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class GraphUtil {

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
}




package stat_os_usage;

import com.sun.management.OperatingSystemMXBean;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xijiu
 * @since 2022/1/29 下午4:07
 */
public class MyTest {

    @Test
    public void test() {
        OperatingSystemMXBean osmxb = null;
        java.lang.management.OperatingSystemMXBean osmxb1 = ManagementFactory.getOperatingSystemMXBean();
        if (osmxb1 instanceof com.sun.management.OperatingSystemMXBean) {
            osmxb = (OperatingSystemMXBean) osmxb1;
        }

        // 总的物理内存
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize();
        // 剩余的物理内存
        long freeMemorySize = osmxb.getFreePhysicalMemorySize();
        long virtualMemorySize = osmxb.getCommittedVirtualMemorySize();

        System.out.println("totalMemorySize : " + totalMemorySize / 1024D / 1024D / 1024D);
        System.out.println("freeMemorySize : " + freeMemorySize / 1024D / 1024D / 1024D);
        System.out.println("virtualMemorySize : " + virtualMemorySize / 1024D / 1024D / 1024D);
        System.out.println(osmxb.getSystemLoadAverage());
        for (int i = 0; i < 10; i++) {
            System.out.println(osmxb.getAvailableProcessors());
            System.out.println(osmxb.getSystemLoadAverage());
        }
    }

    @Test
    public void test2() {
        String str = "8888888888888888888";
        byte[] bytes = str.getBytes();

        long begin = System.currentTimeMillis();
        for (int j = 0; j < 1000000000; j++) {
            long sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum = sum * 10 + (bytes[i] - 48);
            }
        }
        long cost = System.currentTimeMillis() - begin;

        long aaa = 0;

//        long begin = System.currentTimeMillis();
//        for (int j = 0; j < 1000000000; j++) {
//            long sum = 0;
//            sum += (bytes[0] - 48) * 1000000000000000000L;
//            sum += (bytes[1] - 48) * 100000000000000000L;
//            sum += (bytes[2] - 48) * 10000000000000000L;
//            sum += (bytes[3] - 48) * 1000000000000000L;
//            sum += (bytes[4] - 48) * 100000000000000L;
//            sum += (bytes[5] - 48) * 10000000000000L;
//            sum += (bytes[6] - 48) * 1000000000000L;
//            sum += (bytes[7] - 48) * 100000000000L;
//            sum += (bytes[8] - 48) * 10000000000L;
//            sum += (bytes[9] - 48) * 1000000000L;
//            sum += (bytes[10] - 48) * 100000000L;
//            sum += (bytes[11] - 48) * 10000000L;
//            sum += (bytes[12] - 48) * 1000000L;
//            sum += (bytes[13] - 48) * 100000L;
//            sum += (bytes[14] - 48) * 10000L;
//            sum += (bytes[15] - 48) * 1000L;
//            sum += (bytes[16] - 48) * 100L;
//            sum += (bytes[17] - 48) * 10L;
//            sum += (bytes[18] - 48);
//            aaa += sum;
//        }
//        long cost = System.currentTimeMillis() - begin;

        System.out.println(aaa);
        System.out.println("time cost : " + cost);
    }

    @Test
    public void test3() {
        A test = new B();
        System.out.println(test.toString());
    }

    private class A {
        public String toString() {
            return "a";
        }
    }

    private class B extends A {
        public String toString() {
            return "b";
        }
    }


}

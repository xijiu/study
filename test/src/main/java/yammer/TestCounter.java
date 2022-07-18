package yammer;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.reporting.ConsoleReporter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TestCounter {

    private final Counter pendingJobs = Metrics.newCounter(TestCounter.class, "pending-jobs");

    public void add(long num) {
        pendingJobs.inc(num);


        AtomicLong atomicLong = new AtomicLong();
        atomicLong.getAndSet(0);
    }

    /**
     * TODO
     *
     * @param args void
     * @throws InterruptedException
     * @author scutshuxue.chenxf
     */
    public static void main(String[] args) throws InterruptedException {
        // TODOAuto-generated method stub
        TestCounter tc = new TestCounter();
        ConsoleReporter.enable(1, TimeUnit.SECONDS);
        while (true) {
            tc.add(1);
            Thread.sleep(1000);
        }



    }

}
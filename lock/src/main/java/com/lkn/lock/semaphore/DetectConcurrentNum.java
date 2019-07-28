package com.lkn.lock.semaphore;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 探测并发度
 * @author likangning
 * @since 2019/7/2 上午9:19
 */
public class DetectConcurrentNum {

	private Random rng = new Random(2019);
	private double avgRtt = 46.23234333D;
	/** 设定程序的并发度是116 */
	private Semaphore semaphore = new Semaphore(180);

	private void doOnce() {
		try {
			semaphore.acquire();
			long rtt = nextRTT();
			Thread.sleep(rtt);
			"abcdefghiklnadfadfadfasdfasdfafasdf".hashCode();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			semaphore.release();
		}
	}

	private long nextRTT() {
		double u = rng.nextDouble();
		int x = 0;
		double cdf = 0;
		while (u >= cdf) {
			x++;
			cdf = 1 - Math.exp(-1.0D * 1 / avgRtt * x);
		}
		return x;
	}


	private AtomicInteger counter = new AtomicInteger();

	private int concurrentNum = 20;

	private InternalSemaphore localSemaphore = new InternalSemaphore(concurrentNum);

	@Test
	public void detect() throws InterruptedException {
		ExecutorService pool = Executors.newCachedThreadPool();
		for (int i = 0; i < 500; i++) {
			pool.submit(() -> {
				while (true) {
					try {
						localSemaphore.acquire();
						doOnce();
						counter.incrementAndGet();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						localSemaphore.release();
					}
				}
			});
		}

		runTimerTask();
		pool.shutdown();
		pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
	}

	/**
	 * 40 : 8041
	 * 60 : 12156
	 * 80 : 16101
	 * 100 : 20186
	 * @throws InterruptedException
	 */
	@Test
	public void detect2() throws InterruptedException {
		ExecutorService pool = Executors.newCachedThreadPool();
		for (int i = 0; i < 160; i++) {
			pool.submit(() -> {
				while (true) {
					try {
						doOnce();
						counter.incrementAndGet();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
					}
				}
			});
		}

		pool.shutdown();
		Thread.sleep(10000);
		System.out.println(counter.get());
	}

	private void runTimerTask() throws InterruptedException {
		int sampleTime = 10000;
		Timer timer = new Timer();
		Thread.sleep(2000);
		counter.set(0);

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println(concurrentNum + " : " + counter.getAndSet(0));
				concurrentNum += 20;
				localSemaphore.addPermit(concurrentNum);
			}
		}, sampleTime, sampleTime);
	}

	private static class InternalSemaphore extends Semaphore {
		public InternalSemaphore(int permits) {
			super(permits);
		}

		public InternalSemaphore(int permits, boolean fair) {
			super(permits, fair);
		}

		public void acquire() throws InterruptedException {
			super.acquire();
		}

		void reducePermit(int n) {
			super.reducePermits(n);
		}

		void addPermit(int n) {
			super.release(n);
		}
	}

	@ToString
	@AllArgsConstructor
	private static class Bean {
		private int threadNum;
		private int tps;
	}


	@Test
	public void aaaa() {
		List<Bean> list = prepareData();
		for (int i = 1; i < list.size(); i++) {
			Bean pre = list.get(i - 1);
			Bean curr = list.get(i);
			System.out.println(curr);
			System.out.println("建议设置并发数：" + suggestConcurrent(pre.threadNum, pre.tps, curr.threadNum, curr.tps));
			System.out.println();
		}
	}

	private static final int TOTAL_THREAD_NUM = 200;

	private double BOUND_RATIO = 0.05D;

	private int suggestConcurrent(int preThreadNum, int preLastTps, int currThreadNum, int currTps) {
		if (currThreadNum > preThreadNum) {
			// 线程数有增加，且tps有增加
			if (currTps > preLastTps) {
				return threadRise_tpsRise(preThreadNum, preLastTps, currThreadNum, currTps);
			} else {
				return threadRise_tpsDecline(preThreadNum, preLastTps, currThreadNum, currTps);
			}
		} else {
			// 线程数少了，tps反而多了，不正常
			if (currTps > preLastTps) {
				return threadDecline_tpsRise(preThreadNum, preLastTps, currThreadNum, currTps);
			} else {
				return threadDecline_tpsDecline(preThreadNum, preLastTps, currThreadNum, currTps);
			}
		}
	}

	/**
	 * tps上升、线程上升
	 */
	private int threadRise_tpsRise(int preThreadNum, int preLastTps, int currThreadNum, int currTps) {
		double threadRatio = changeRatio(preThreadNum, currThreadNum);
		double tpsRatio = changeRatio(preLastTps, currTps);
		// 线程上升速率太快，tps并没有一起上升
		if (threadRatio / tpsRatio > 1.8D) {
			return preThreadNum;
		} else {
			// 如果tps上升的速率在5%以内，那么视同正常波动
			if (tpsRatio < BOUND_RATIO) {
				return currThreadNum;
			} else {
				// 线程与tps同步上升，说明tps很有可能会随着线程的上升而上升
				return (int) (currThreadNum + TOTAL_THREAD_NUM * 0.1);
			}
		}
	}

	/**
	 * 线程数上升、tps下降
	 */
	private int threadRise_tpsDecline(int preThreadNum, int preLastTps, int currThreadNum, int currTps) {
		// 此类情况发生概率较低，直接取较低线程数
		return preThreadNum;
	}

	/**
	 * 线程下降、tps上升
	 */
	private int threadDecline_tpsRise(int preThreadNum, int preLastTps, int currThreadNum, int currTps) {
		// 此类情况发生概率较低，直接取较低线程数
		return currThreadNum;
	}

	/**
	 * 线程下降、tps下降
	 */
	private int threadDecline_tpsDecline(int preThreadNum, int preLastTps, int currThreadNum, int currTps) {
		double threadRatio = changeRatio(currThreadNum, preThreadNum);
		double tpsRatio = changeRatio(currTps, preLastTps);
		// 线程与tps的下降速率不一致，此时返回数量较大的线程
		if (threadRatio / tpsRatio > 1.8D) {
			return currThreadNum;
		} else {
			// 如果tps下降的速率在5%以内，那么视同正常波动
			if (tpsRatio < BOUND_RATIO) {
				return currThreadNum;
			} else {
				// 线程与tps同步下降，说明tps很有可能会随着线程的上升而上升
				return (int) (preThreadNum + TOTAL_THREAD_NUM * 0.1);
			}
		}
	}


	private double changeRatio(int origin, int change) {
		return (double)(change - origin) / (double)origin;
	}

	private List<Bean> prepareData() {
		List<Bean> list = Lists.newArrayList();
		list.add(new Bean(125, 1112));
		list.add(new Bean(134, 1127));
		list.add(new Bean(155, 1167));
		list.add(new Bean(109, 1006));
		list.add(new Bean(140, 1115));
		list.add(new Bean(128, 1091));
		list.add(new Bean(115, 1023));
		list.add(new Bean(157, 1067));
		list.add(new Bean(126, 1114));
		list.add(new Bean(134, 1085));
		list.add(new Bean(115, 1020));
		list.add(new Bean(106, 1019));
		list.add(new Bean(136, 1087));

		list.add(new Bean(101, 1230));
		list.add(new Bean(91, 1363));
		list.add(new Bean(87, 1257));
		list.add(new Bean(90, 1249));
		list.add(new Bean(95, 1354));
		list.add(new Bean(95, 1278));
		list.add(new Bean(92, 1307));
		list.add(new Bean(87, 1172));
		list.add(new Bean(93, 1270));
		list.add(new Bean(97, 1316));
		list.add(new Bean(96, 1338));
		list.add(new Bean(90, 1284));
		return list;
	}

	@Test
	public void aaaab() {
		Map<String, String> map = Maps.newHashMap();
//		map.put("a", "a");
		map.computeIfAbsent("a", key -> {
			System.out.println(key);
			return "b";
		});
		System.out.println(map);
	}
}

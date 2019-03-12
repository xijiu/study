package com.lkn.nio.compare;

import com.google.common.base.Stopwatch;
import com.lkn.nio.compare.batch.BatchBioTest;
import com.lkn.nio.compare.batch.BatchNettyTest;
import com.lkn.nio.compare.batch.BatchNioTest;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 一个把bio、nio、aio讲的比较清楚的文章
 * https://blog.csdn.net/ty497122758/article/details/78979302
 * https://zhuanlan.zhihu.com/p/23488863
 *
 * 测试场景：模拟聊天的场景，client启动{@code THREAD_NUM}个线程，每个线程发送{@code CLIENT_SAY_HELLO_TIMES}次数据
 * 结论：经过两天的反复测试，得出了一个意外的结果。在同一台机器1000以内的并发测试，bio的性能要略优于nio。。。
 *
 * @author likangning
 * @since 2019/3/4 上午10:15
 */
public class BatchThreadNioAndBioCompare {

	protected static int PORT = 7777;

	protected static String SERVER_HOST = "127.0.0.1";

	protected static int THREAD_NUM = 500;

	protected static int CLIENT_SAY_HELLO_TIMES = 2000;

	protected static String LOGIN_SUCCESS = "LOGIN_SUCCESS";

	protected static String SAY = "say";

	protected static String BYE = "bye";

	private static BatchBioTest batchBioTest = new BatchBioTest();

	private static BatchNioTest batchNioTest = new BatchNioTest();

	private static BatchNettyTest batchNettyTest = new BatchNettyTest();

	/**
	 * 41769   43483
	 */
	@Test
	public void bioServer() throws Exception {
		batchBioTest.bioServerStart();
	}

	@Test
	public void bioClient() throws Exception {
		Stopwatch stopwatch = Stopwatch.createStarted();
		batchBioTest.bioClientStart();
		long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		System.out.println("bio 总共耗时(ms)：" + elapsed);
	}


	/***********************        分割线，以下为nio       ***********************/

	@Test
	public void nioServer() throws Exception {
		batchNioTest.nioServerStart();
	}

	@Test
	public void nioClient() throws Exception {
		Stopwatch stopwatch = Stopwatch.createStarted();
		batchNioTest.nioClientStart();
		long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		System.out.println("nio 总共耗时(ms)：" + elapsed);
	}


	/***********************        分割线，以下为netty       ***********************/


	@Test
	public void nettyServer() throws Exception {
		batchNettyTest.jettyServerStart();
	}

	@Test
	public void nettyClient() throws Exception {
		Stopwatch stopwatch = Stopwatch.createStarted();
		batchNettyTest.jettyClientStart();
		long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		System.out.println("jetty 总共耗时(ms)：" + elapsed);
	}



}

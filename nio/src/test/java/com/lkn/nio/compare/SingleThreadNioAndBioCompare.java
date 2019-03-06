package com.lkn.nio.compare;

import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

/**
 * 一个把bio、nio、aio讲的比较清楚的文章 https://blog.csdn.net/ty497122758/article/details/78979302
 *
 *
 * 测试背景：
 * 1、将一个130M的文件分别用bio与nio拷贝10次
 * 2、server端与client端均为单线程
 *
 * 测试结果：
 * nio 耗时：18725(ms)
 * bio 耗时：19360(ms)
 *
 * 结论：
 * 在连接数量不大的情况下，nio略有优势，但不够明显
 *
 * @author likangning
 * @since 2019/3/4 上午10:15
 */
public class SingleThreadNioAndBioCompare {
	private static File SOURCE_FILE = new File(System.getProperty("user.dir") + "/tmpFile/mysql.tar");

	private static int COPY_SIZE = 1024;

	@Test
	public void batchCompareTest() throws Exception {
		int batchTimes = 10;
		Stopwatch stopwatch = Stopwatch.createStarted();

		for (int i = 0; i < batchTimes; i++) {
			nioCopy();
		}
		System.out.println("nio 批量执行 " + batchTimes + " 次耗时：" + stopwatch.elapsed(TimeUnit.MILLISECONDS));

		stopwatch.reset().start();

		for (int i = 0; i < batchTimes; i++) {
			bioCopy();
		}
		System.out.println("bio 批量执行 " + batchTimes + " 次耗时：" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
	}

	@Test
	public void compareTest() throws Exception {
		Stopwatch stopwatch = Stopwatch.createStarted();
		nioCopy();
		System.out.println("nio 耗时： " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

		stopwatch.reset().start();
		bioCopy();
		System.out.println("bio 耗时： " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
	}

	/**
	 * 阻塞io的拷贝
	 */
	private void bioCopy() throws Exception {
		File targetFile = new File(System.getProperty("user.dir") + "/tmpFile/mysql_bio.tar");
		deleteFileIfExist(targetFile);
		FileInputStream fis = new FileInputStream(SOURCE_FILE);
		FileOutputStream fos = new FileOutputStream(targetFile);
		byte[] buffer = new byte[COPY_SIZE];
		int len;
		while ((len = fis.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
		}
		fis.close();
		fos.close();
	}

	/**
	 * 新io
	 */
	private void nioCopy() throws Exception {
		File targetFile = new File(System.getProperty("user.dir") + "/tmpFile/mysql_nio.tar");
		deleteFileIfExist(targetFile);
		FileInputStream fis = new FileInputStream(SOURCE_FILE);
		FileOutputStream fos = new FileOutputStream(targetFile);
		FileChannel channel = fis.getChannel();
		FileChannel outChannel = fos.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(COPY_SIZE);
		while (channel.read(buffer) != -1) {
			buffer.flip();
			outChannel.write(buffer);
			buffer.clear();
		}
		channel.close();
		fis.close();
	}

	private void deleteFileIfExist(File targetFile) {
		if (targetFile.exists()) {
			targetFile.delete();
		}
	}

}

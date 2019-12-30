package com.lkn.race2019;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author likangning
 * @since 2019/8/19 下午10:39
 */
public class FileChannelReadTest {

	/** 并发度 */
	private static int CONCURRENT_NUM = 1;

	/** 每次写入的大小（单位K） */
	private static int WRITE_PER_SIZE = 4;

	/** 总文件大小，单位K */
	private static int TOTAL_SIZE = 16 * 1024 * 1024;

	/** 每个文件的大小，单位K */
	private static int PER_FILE_SIZE = TOTAL_SIZE / CONCURRENT_NUM;

	private static File BASE_FILE = new File("/Users/likangning/Desktop/mqrace/");
	private static File file = new File(BASE_FILE.getPath() + "/0.index");
	private static ExecutorService executorService = Executors.newCachedThreadPool();


	public FileChannelReadTest() throws IOException {
	}


	/**
	 * 3391
	 * 3401
	 * 3424
	 *
	 * 3834
	 * 3901
	 * 3854
	 */
	@Test
	public void test() throws Exception {
		long begin = System.currentTimeMillis();
		FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);

		for (int i = 0; i < 10; i++) {
			executorService.submit(() -> {
				int size = 20000 * 8;
				ByteBuffer byteBuffer = ByteBuffer.allocate(size);
				try {
					for (int j = 0; j < 30000; j++) {
						byteBuffer.clear();
						int beginPos = ThreadLocalRandom.current().nextInt(TOTAL_SIZE);
						fileChannel.read(byteBuffer, beginPos);
						byteBuffer.flip();
						byte b = byteBuffer.get();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

		executorService.shutdown();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

		long end = System.currentTimeMillis();
		long cost = end - begin;
		System.out.println("耗时： " + cost);
	}


	/**
	 * 2818
	 * 2972
	 * 2955
	 *
	 * 3435
	 * 3262
	 * 3367
	 */
	@Test
	public void test2() throws Exception {
		long begin = System.currentTimeMillis();
		int totalTimes = 300000;
		int concurrent = 10;
		int per = totalTimes / concurrent;

		for (int i = 0; i < concurrent; i++) {
			int flag = i;
			executorService.submit(() -> {
				File file = new File("/Users/likangning/Desktop/mqrace/" + flag + ".index");
				int size = 20000 * 8;
				ByteBuffer byteBuffer = ByteBuffer.allocate(size);
				try {
					FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
					int totalSize = TOTAL_SIZE / concurrent;
					for (int j = 0; j < per; j++) {
						byteBuffer.clear();
						int beginPos = ThreadLocalRandom.current().nextInt(totalSize);
						fileChannel.read(byteBuffer, beginPos);
						byteBuffer.flip();
						byte b = byteBuffer.get();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

		executorService.shutdown();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

		long end = System.currentTimeMillis();
		long cost = end - begin;
		System.out.println("耗时： " + cost);
	}

	@Test
	public void test3() throws Exception {
		File file = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");
		FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.READ);
		fileChannel.position(233);
		ByteBuffer byteBuffer = ByteBuffer.allocate(1);
		while (true) {
			Scanner scanner = new Scanner(System.in);
			int num = scanner.nextInt();
			byteBuffer.clear();
			fileChannel.read(byteBuffer);
			byteBuffer.flip();
			byte content = byteBuffer.get();
		}
	}







}

package com.lkn.race2019;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 结论：本地mac，20个并发，每次写入8K能将io打满，本地mac的io写入吞吐极限在1.3g/s左右
 * @author likangning
 * @since 2019/8/17 上午11:00
 */
public class WriteTest {

	private static ExecutorService executorService = Executors.newCachedThreadPool();

	/** 并发度 */
	private static int CONCURRENT_NUM = 20;

	/** 每次写入的大小（单位K） */
	private static int WRITE_PER_SIZE = 8;

	/** 总文件大小，单位K */
	private static int TOTAL_SIZE = 4 * 1024 * 1024;

	/** 每个文件的大小，单位K */
	private static int PER_FILE_SIZE = TOTAL_SIZE / CONCURRENT_NUM;

	private static File BASE_FILE = new File("/Users/likangning/Desktop/mqrace/");

	@Test
	public void test() throws InterruptedException {
		deleteSubFile();
		long begin = System.currentTimeMillis();
		for (int i = 0; i < CONCURRENT_NUM; i++) {
			int threadNum = i;
			executorService.submit(() -> {
				File file = new File(BASE_FILE.getPath() + "/" + threadNum + ".index");
				reCreateFile(file);
				try (FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ)) {
					ByteBuffer byteBuffer = ByteBuffer.allocate(WRITE_PER_SIZE * 1024);
//					ByteBuffer byteBuffer = ByteBuffer.allocateDirect(WRITE_PER_SIZE * 1024);
					for (int j = 0; j < PER_FILE_SIZE / WRITE_PER_SIZE; j++) {
						byteBuffer.clear();
						byteBuffer.put(new byte[WRITE_PER_SIZE * 1024]);
						byteBuffer.flip();
						fileChannel.write(byteBuffer);
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
		System.out.println("写入速率(g/s)： " + ((double)TOTAL_SIZE / 1024 / 1024 / cost * 1000));
	}

	private void deleteSubFile() {
		File[] files = BASE_FILE.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file != null) {
					file.delete();
				}
			}
		}
	}

	private void reCreateFile(File file) {
		try {
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			} else {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

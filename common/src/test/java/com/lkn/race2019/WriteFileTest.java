package com.lkn.race2019;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author likangning
 * @since 2019/9/17 下午3:28
 */
public class WriteFileTest {

	private static ExecutorService executorService = Executors.newCachedThreadPool();

	/** 并发度 */
	private static int CONCURRENT_NUM = 1;

	/** 每次写入的大小（单位K） */
	private static int WRITE_PER_SIZE = 8;

	/** 总文件大小，单位K */
	private static int TOTAL_SIZE = 16 * 1024 * 1024;

	/** 每个文件的大小，单位K */
	private static int PER_FILE_SIZE = TOTAL_SIZE / CONCURRENT_NUM;

	private static File SOURCE_FILE = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");

	private static File BASE_FILE = new File("/Users/likangning/Desktop/mqrace/");

	@Test
	public void aaaa() {
		// bytebuffer 7047  directbytebuffer 6483
		int times = 10;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			byteBufferVSDirectByteBuffer();
		}
		long cost = System.currentTimeMillis() - begin;
		System.out.println(cost / times);
	}

	@Test
	public void byteBufferVSDirectByteBuffer() {
		deleteSubFile();
		long begin = System.currentTimeMillis();
		File file = new File(BASE_FILE.getPath() + "/test.index");
		reCreateFile(file);
		try (FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
				 FileChannel sourceFileChannel = FileChannel.open(Paths.get(SOURCE_FILE.toURI()), StandardOpenOption.READ)) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(WRITE_PER_SIZE * 1024);
//			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(WRITE_PER_SIZE * 1024);
			for (int j = 0; j < PER_FILE_SIZE / WRITE_PER_SIZE; j++) {
				byteBuffer.clear();
				sourceFileChannel.read(byteBuffer);
				byteBuffer.flip();
				fileChannel.write(byteBuffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		long cost = end - begin;
		System.out.println("耗时： " + cost);
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

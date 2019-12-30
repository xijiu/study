package com.lkn.race2019;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author likangning
 * @since 2019/8/19 下午10:39
 */
public class FileChannelWriteTest {

	/** 并发度 */
	private static int CONCURRENT_NUM = 1;

	/** 每次写入的大小（单位K） */
	private static int WRITE_PER_SIZE = 4;

	/** 总文件大小，单位K */
	private static int TOTAL_SIZE = 4 * 1024 * 1024;

	/** 每个文件的大小，单位K */
	private static int PER_FILE_SIZE = TOTAL_SIZE / CONCURRENT_NUM;

	private static File BASE_FILE = new File("/Users/likangning/Desktop/mqrace/");

//	@Test
//	public void test() throws Exception {
//		deleteSubFile();
//		long begin = System.currentTimeMillis();
//
//		File file = new File(BASE_FILE.getPath() + "/test.index");
//		reCreateFile(file);
//		try (FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ)) {
//			ByteBuffer byteBuffer = ByteBuffer.allocate(WRITE_PER_SIZE * 1024);
//			for (int j = 0; j < PER_FILE_SIZE / WRITE_PER_SIZE; j++) {
//				byteBuffer.clear();
//				byteBuffer.put(new byte[WRITE_PER_SIZE * 1024]);
//				byteBuffer.flip();
//				fileChannel.write(byteBuffer);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		long end = System.currentTimeMillis();
//		long cost = end - begin;
//		System.out.println("耗时： " + cost);
//		System.out.println("写入速率(g/s)： " + ((double)TOTAL_SIZE / 1024 / 1024 / cost * 1000));
//	}


	@Test
	public void test2() throws Exception {
		deleteSubFile();
		long begin = System.currentTimeMillis();

		File file = new File(BASE_FILE.getPath() + "/test.index");
		reCreateFile(file);
		try (FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(WRITE_PER_SIZE * 1024);
			int fileSize = 1024 * 1024 * 1024;

			for (int i = 0; i < 1; i++) {
				int position = i * fileSize;
				MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, position, fileSize);
				for (int j = 0; j < fileSize / (WRITE_PER_SIZE * 1024); j++) {
					byteBuffer.clear();
					byteBuffer.put(new byte[WRITE_PER_SIZE * 1024]);
					byteBuffer.flip();
					mappedByteBuffer.put(byteBuffer);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		long cost = end - begin;
		System.out.println("耗时： " + cost);
		System.out.println("写入速率(g/s)： " + ((double)TOTAL_SIZE / 1024 / 1024 / cost * 1000));
	}

	private void deleteSubFile() throws IOException {
		File[] files = BASE_FILE.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				file.delete();
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

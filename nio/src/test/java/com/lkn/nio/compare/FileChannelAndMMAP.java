package com.lkn.nio.compare;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

/**
 * 单线程下，FileChannel与MMAP的性能比较
 * @author likangning
 * @since 2019/7/23 上午10:14
 */
public class FileChannelAndMMAP {

	private File channelFile = new File("/Users/likangning/test/io/data.index");

	/** 单位byte */
	private long fileSize = 4 * 1024 * 1024 * 500;

	@Before
	public void init () throws IOException {
		if (channelFile.exists()) {
			channelFile.delete();
		}
		channelFile.createNewFile();
	}

	@Test
	public void abc() throws FileNotFoundException {
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			try (RandomAccessFile indexFin = new RandomAccessFile(channelFile, "rw");
					 FileChannel indexChannel = indexFin.getChannel()) {
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}

	@Test
	public void fileChannelWrite2() throws IOException {
		long fileSize = 1024 * 1024 * 1024;
		long begin = System.currentTimeMillis();
		FileChannel fileChannel = FileChannel.open(Paths.get(channelFile.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);

		int perSize = 1024 * 1024 * 64;
		int loop = (int) (fileSize / perSize);
		for (int i = 0; i < loop; i++) {
			fileChannel.write(ByteBuffer.wrap(new byte[perSize]));
		}

		long end = System.currentTimeMillis();
		System.out.println("耗时： " + (end - begin));
	}

	@Test
	public void mmapWrite() throws IOException {
		long fileSize = 1024 * 1024 * 1024;
		long begin = System.currentTimeMillis();
		FileChannel fileChannel = FileChannel.open(Paths.get(channelFile.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
		MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
		int perSize = 4096;
		int loop = (int) (fileSize / perSize);
		for (int i = 0; i < loop; i++) {
			mappedByteBuffer.put(new byte[perSize]);
		}
		long end = System.currentTimeMillis();
		System.out.println("耗时： " + (end - begin));
	}


	// 并发线程数
	private int concurrentThreadNum = 32;
	// totalSize
	private long totalSize = 8L * 1024 * 1024 * 1024;


	@Test
	public void mmapWrite22222() throws Exception {
		long begin = System.currentTimeMillis();
		Thread[] threads = new Thread[concurrentThreadNum];
		for (int i = 0; i < threads.length; i++) {
			File channelFile = new File("/Users/likangning/test/io/data" + i + ".index");
			if (channelFile.exists()) {
				channelFile.delete();
			}
			channelFile.createNewFile();
			Thread thread = new Thread(() -> {
				try {
					mmapWrite2222(channelFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			threads[i] = thread;
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
		long end = System.currentTimeMillis();
		long cost = end - begin;
		long ps = (long) ((double)totalSize / 1024D / 1024D / ((double)cost / 1000D));
		System.out.println("耗时： " + cost);
		System.out.println("能力 (m/s) " + ps);
	}

	private void mmapWrite2222(File channelFile) throws Exception {
		long fileSize = totalSize / concurrentThreadNum;

		FileChannel fileChannel = FileChannel.open(Paths.get(channelFile.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
		MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
		int perSize = 4096;
		ByteBuffer byteBuffer = ByteBuffer.allocate(perSize);
		int loop = (int) (fileSize / perSize);
		for (int i = 0; i < loop; i++) {
			byteBuffer.put(new byte[perSize]);
			mappedByteBuffer.put(byteBuffer.array());
			byteBuffer.clear();
		}
	}

	private long partFileSize = 1024 * 1024 * 1024;
	private int perSize = 2048;

	@Test
	public void mmapWrite2() throws IOException {
		long begin = System.currentTimeMillis();
		for (int j = 0; j < 10; j++) {
			FileChannel fileChannel = FileChannel.open(Paths.get(channelFile.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, partFileSize);
			for (int i = 0; i < partFileSize / perSize; i++) {
				mappedByteBuffer.put(new byte[perSize]);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("耗时： " + (end - begin));
	}

	@Test
	public void mmapWrite3() throws IOException {
		long begin = System.currentTimeMillis();
		for (int j = 0; j < 10; j++) {
			FileChannel fileChannel = FileChannel.open(Paths.get(channelFile.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
			ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[perSize]);
			for (int i = 0; i < partFileSize / perSize; i++) {
				fileChannel.write(byteBuffer);
				byteBuffer.clear();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("耗时： " + (end - begin));
	}

	/**
	 * 50亿： 1628
	 */
	@Test
	public void ac() {
		TreeSet<Integer> set = new TreeSet<>();
		set.add(10);
		set.add(8);
		set.add(9);
		set.add(2);
		set.add(4);
		set.add(5);

		Integer ele = set.floor(6);
		Integer higher = set.higher(ele);
		System.out.println(ele);
		System.out.println(higher);
	}


}

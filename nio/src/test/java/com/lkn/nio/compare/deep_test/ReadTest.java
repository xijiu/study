package com.lkn.nio.compare.deep_test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author likangning
 * @since 2019/8/1 下午7:48
 */
public class ReadTest {

	private static File file = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");

	private long beginTime = 0L;

	private long cost = 0L;
	@Before
	public void begin() {
		beginTime = System.currentTimeMillis();
	}

	@After
	public void end() {
		cost = System.currentTimeMillis() - beginTime;
		System.out.println("耗时： " + cost);
	}

	@Test
	public void fileChannelRead() throws Exception {
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
		FileChannel fileChannel = randomAccessFile.getChannel();
//		ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 16);
		ByteBuffer byteBuffer = ByteBuffer.allocate(34 * 30 * 15);

		while (true) {
			byteBuffer.clear();
			int read = fileChannel.read(byteBuffer);
			if (read == -1) {
				break;
			}
		}
		fileChannel.close();
		randomAccessFile.close();
	}

	@Test
	public void mappedBufferRead() throws Exception {
		File file = new File("/Users/likangning/test/test123.index");
		if (file.exists()) {
			file.delete();
		}
		FileChannel fileChannel = FileChannel.open(file.toPath());
		fileChannel.close();
	}

	@Test
	public void mappedBufferRead2() throws Exception {
		File file = new File("/Users/likangning/test/test123.index");
		if (file.exists()) {
			file.delete();
		}
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
		randomAccessFile.setLength(10 * 1024 * 1024);
		FileChannel fileChannel = randomAccessFile.getChannel();
		fileChannel.close();
	}

}

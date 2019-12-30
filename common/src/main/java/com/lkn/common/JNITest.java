package com.lkn.common;

import net.smacke.jaydio.DirectRandomAccessFile;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author likangning
 * @since 2019/9/20 上午8:25
 */
public class JNITest {

	private static File SOURCE_FILE = new File("/serving/ykp/20190508.sql");

	private static int BUFFER_SIZE = 4096;
	private static int RANDOM_READ_BUFFER_SIZE = 1;
	private static int READ_TIMES = 500000;

	public static void main(String[] args) throws IOException {
//		dioSeqRead();
		dioRandomRead();
//		bufferIOSeqRead();
		bufferIORandomRead();
	}

	/**
	 * dio 顺序读
	 */
	private static void dioSeqRead() throws IOException {
		long begin = System.currentTimeMillis();
		byte[] buf = new byte[BUFFER_SIZE];
		try (DirectRandomAccessFile fin = new DirectRandomAccessFile(SOURCE_FILE, "r")) {
			for (int i = 0; i < READ_TIMES; i++) {
				fin.seek(i * BUFFER_SIZE);
				fin.read(buf);
				if (i % 10000 == 0) {
					System.out.println(i);
				}
			}
		}
		long cost = System.currentTimeMillis() - begin;
		System.out.println("dio顺序读耗时 : " + cost + "， 读取文件大小：" + fileSize() + "g");
	}

	/**
	 * dio 随机读
	 */
	private static void dioRandomRead() throws IOException {
		long begin = System.currentTimeMillis();
		int totalByteNum = READ_TIMES * BUFFER_SIZE;
		byte[] buf = new byte[RANDOM_READ_BUFFER_SIZE];
		try (DirectRandomAccessFile fin = new DirectRandomAccessFile(SOURCE_FILE, "r")) {
			for (int i = 0; i < READ_TIMES; i++) {
				int position = ThreadLocalRandom.current().nextInt(totalByteNum);
				fin.seek(position);
				fin.read(buf);
				if (i % 10000 == 0) {
					System.out.println(i);
				}
			}
		}
		long cost = System.currentTimeMillis() - begin;
		System.out.println("dio随机读耗时 : " + cost + "， 读取文件大小：" + randomFileSize() + "g");
	}


	/**
	 * buffer io 顺序读
	 */
	private static void bufferIOSeqRead() throws IOException {
		long begin = System.currentTimeMillis();
		ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		try (FileChannel fileChannel = FileChannel.open(Paths.get(SOURCE_FILE.toURI()), StandardOpenOption.READ)) {
			for (int i = 0; i < READ_TIMES; i++) {
				byteBuffer.clear();
				fileChannel.read(byteBuffer);
				byteBuffer.flip();
				if (i % 10000 == 0) {
					System.out.println(i);
				}
			}
		}
		long cost = System.currentTimeMillis() - begin;
		System.out.println("buffer io 顺序读耗时 : " + cost + "， 读取文件大小：" + fileSize() + "g");
	}

	/**
	 * buffer io 随机读
	 */
	private static void bufferIORandomRead() throws IOException {
		long begin = System.currentTimeMillis();
		int totalByteNum = READ_TIMES * BUFFER_SIZE;
		ByteBuffer byteBuffer = ByteBuffer.allocate(RANDOM_READ_BUFFER_SIZE);
		try (FileChannel fileChannel = FileChannel.open(Paths.get(SOURCE_FILE.toURI()), StandardOpenOption.READ)) {
			for (int i = 0; i < READ_TIMES; i++) {
				int position = ThreadLocalRandom.current().nextInt(totalByteNum);
				byteBuffer.clear();
				fileChannel.read(byteBuffer, position);
				byteBuffer.flip();
				if (i % 10000 == 0) {
					System.out.println(i);
				}
			}
		}
		long cost = System.currentTimeMillis() - begin;
		System.out.println("buffer io 随机读耗时 : " + cost + "， 读取文件大小：" + randomFileSize() + "g");
	}


	private static double fileSize() {
		return (double) READ_TIMES * BUFFER_SIZE / 1024 / 1024 / 1024;
	}

	private static double randomFileSize() {
		return (double) READ_TIMES * RANDOM_READ_BUFFER_SIZE / 1024 / 1024 / 1024;
	}

}

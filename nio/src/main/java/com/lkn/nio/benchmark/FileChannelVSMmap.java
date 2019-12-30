package com.lkn.nio.benchmark;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * @author likangning
 * @since 2019/10/18 上午11:26
 */
public class FileChannelVSMmap {
	private static File DATA_FILE = new File("/Users/likangning/test/io/data.index");

	private static int PER_SIZE = (int) (64 * 1024);

	private static long WRITE_FILE_SIZE = (long) (1 * 1024 * 1024 * 1024L);

	private static long WRITE_TIMES = WRITE_FILE_SIZE / PER_SIZE;

	private static long START_TIME = -1L;

	private static File SOURCE_FILE = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");



	/**************************************************************** 写（堆内存） *******************************************************************/

	public static void main(String[] args) {
		if (args.length >= 1) {
			PER_SIZE = (int) (Double.parseDouble(args[0]) * 1024);
			WRITE_TIMES = WRITE_FILE_SIZE / PER_SIZE;
		}
		new FileChannelVSMmap().mmapWrite();
	}

//	@Test
//	public void aaaa() {
//		// bytebuffer 5337  directbytebuffer 5207
//		int times = 10;
//		long begin = System.currentTimeMillis();
//		for (int i = 0; i < times; i++) {
//			byteBufferVSDirectByteBuffer();
//		}
//		long cost = System.currentTimeMillis() - begin;
//		System.out.println(cost / times);
//	}

//	@Test
//	public void byteBufferVSDirectByteBuffer() {
//		clearFile();
//		byte[] data = new byte[PER_SIZE];
//		try (FileChannel fileChannel = FileChannel.open(Paths.get(DATA_FILE.toURI()),
//				StandardOpenOption.WRITE, StandardOpenOption.READ)) {
//
////			ByteBuffer byteBuffer = ByteBuffer.allocate(PER_SIZE);
//			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(PER_SIZE);
//			for (int i = 0; i < WRITE_TIMES; i++) {
//				byteBuffer.clear();
//				byteBuffer.put(data);
//				byteBuffer.flip();
//				fileChannel.write(byteBuffer);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("写入耗时：" + (System.currentTimeMillis() - START_TIME));
//	}

	@Test
	public void fileChannelWrite() {
		clearFile();
		byte[] data = new byte[PER_SIZE];
		try (FileChannel fileChannel = FileChannel.open(Paths.get(DATA_FILE.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(PER_SIZE);
			for (int i = 0; i < WRITE_TIMES; i++) {
				byteBuffer.clear();
				byteBuffer.put(data);
				byteBuffer.flip();
				fileChannel.write(byteBuffer);
			}
			long begin = System.currentTimeMillis();
			fileChannel.force(true);
			System.out.println("force 耗时： " + (System.currentTimeMillis() - begin));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("FileChannel 写入耗时：" + (System.currentTimeMillis() - START_TIME));
	}

	@Test
	public void mmapWrite() {
		clearFile();
		byte[] data = new byte[PER_SIZE];
		try (FileChannel fileChannel = FileChannel.open(Paths.get(DATA_FILE.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, WRITE_FILE_SIZE);
			for (int i = 0; i < WRITE_TIMES; i++) {
				mappedByteBuffer.put(data);
			}
			long begin = System.currentTimeMillis();
			mappedByteBuffer.force();
			System.out.println("force 耗时： " + (System.currentTimeMillis() - begin));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("mmap 写入耗时：" + (System.currentTimeMillis() - START_TIME));
	}



	/**************************************************************** 写（直接内存） *******************************************************************/

//	@Test
//	public void fileChannelDirectWrite() {
//		clearFile();
//		try (FileChannel fileChannel = FileChannel.open(Paths.get(DATA_FILE.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
//				 FileChannel sourceFileChannel = FileChannel.open(Paths.get(SOURCE_FILE.toURI()), StandardOpenOption.READ)) {
//			ByteBuffer byteBuffer = ByteBuffer.allocate(PER_SIZE);
//			for (int i = 0; i < WRITE_TIMES; i++) {
//				byteBuffer.clear();
//				sourceFileChannel.read(byteBuffer);
//				byteBuffer.flip();
//				fileChannel.write(byteBuffer);
//			}
//			long begin = System.currentTimeMillis();
//			fileChannel.force(true);
//			System.out.println("force 耗时： " + (System.currentTimeMillis() - begin));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("FileChannel (direct memory) 写入耗时：" + (System.currentTimeMillis() - START_TIME));
//	}

	@Test
	public void mmapDirectWrite() {
		clearFile();
		try (FileChannel fileChannel = FileChannel.open(Paths.get(DATA_FILE.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
				 FileChannel sourceFileChannel = FileChannel.open(Paths.get(SOURCE_FILE.toURI()), StandardOpenOption.READ)) {
			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, WRITE_FILE_SIZE);
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(PER_SIZE);
			for (int i = 0; i < WRITE_TIMES; i++) {
				byteBuffer.clear();
				sourceFileChannel.read(byteBuffer);
				byteBuffer.flip();
				mappedByteBuffer.put(byteBuffer);
			}
			long begin = System.currentTimeMillis();
			mappedByteBuffer.force();
			System.out.println("force 耗时： " + (System.currentTimeMillis() - begin));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("mmap 写入耗时：" + (System.currentTimeMillis() - START_TIME));
	}


	/**************************************************************** 以下是读 *******************************************************************/


	@Test
	public void readFileChannel() {
		recordStartTime();
		try (FileChannel fileChannel = FileChannel.open(Paths.get(DATA_FILE.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(PER_SIZE);
			for (int i = 0; i < WRITE_TIMES; i++) {
				byteBuffer.clear();
				fileChannel.read(byteBuffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("FileChannel 读取耗时：" + (System.currentTimeMillis() - START_TIME));
	}

	@Test
	public void readMmap() {
		recordStartTime();
		try (FileChannel fileChannel = FileChannel.open(Paths.get(DATA_FILE.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, WRITE_FILE_SIZE);
			for (int i = 0; i < WRITE_TIMES; i++) {
				mappedByteBuffer.get(new byte[PER_SIZE]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("mmap 读取耗时：" + (System.currentTimeMillis() - START_TIME));
	}




	private void clearFile() {
		if (DATA_FILE.exists()) {
			DATA_FILE.delete();
		}
		try {
			DATA_FILE.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		recordStartTime();
	}

	private void recordStartTime() {
		START_TIME = System.currentTimeMillis();
	}

	@Test
	public void cc() {
		String[] test = {"1", "2", "3"};
		String[] test2 = new String[test.length];
		System.arraycopy(test, 0, test2, 0, test.length);
		test2[0] = "a";
		System.out.println(Arrays.toString(test));
		System.out.println(Arrays.toString(test2));
	}


}

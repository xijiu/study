package com.lkn.algorithm.index_file;

import org.junit.Test;

import java.io.*;

/**
 * @author likangning
 * @since 2018/10/23 上午10:00
 */
public class FileTest {
	private File file = new File("/Users/likangning/myTest/file/a.txt");

	@Test
	public void saveTest() throws Exception {
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		for (int i = 0; i < 1000000; i++) {
			pw.print(i + "_");
			if (i % 10000 == 0) {
				pw.flush();
				System.out.println("已写入 " + i);
			}
		}
		pw.close();
	}

	@Test
	public void readTest() throws IOException {
		long length = file.length();
		System.out.println("文件总长度：" + length);
		FileReader fileReader = new FileReader(file);
		int buffer = 100;
		char[] arr = new char[buffer];
		int read = fileReader.read(arr, 0, buffer);
		System.out.println(read);
		System.out.println(arr);
	}

	/**
	 * 随机读取文件内容
	 */
	@Test
	public void randomReadFile() throws IOException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		int buffer = 100;
		byte[] arr = new byte[buffer];
		randomAccessFile.seek(400);
		int read = randomAccessFile.read(arr);
		System.out.println("读取文件长度：" + read);
		System.out.println("读取文件内容：" + new String(arr));
	}


	/**
	 * 写文件测试
	 */
	@Test
	public void writeFile() throws IOException {
		File file = new File("/Users/likangning/myTest/file/b.index");
		if (!file.exists()) {
			file.createNewFile();
		}
		Long content = 1L;
		OutputStream outputStream = new FileOutputStream(file, true);
		outputStream.write(long2byte(content));
		outputStream.flush();
		outputStream.close();
	}

	private static byte[] long2byte(long res) {
		byte[] buffer = new byte[8];
		for (int i = 0; i < 8; i++) {
			int offset = 64 - (i + 1) * 8;
			buffer[i] = (byte) ((res >> offset) & 0xff);
		}
		return buffer;
	}

	/**
	 * 写文件测试
	 */
	@Test
	public void readFile() throws IOException {
		File file = new File("/Users/likangning/myTest/file/b.index");
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		byte[] arr = new byte[8];
		randomAccessFile.seek(2 * 8);
		randomAccessFile.read(arr);
		System.out.println("读取文件内容：" + byteArray2Long(arr));
	}

	private static long byteArray2Long(byte[] b) {
		long values = 0;
		for (int i = 0; i < 8; i++) {
			values <<= 8; values|= (b[i] & 0xff);
		}
		return values;
	}
}

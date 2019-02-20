package com.lkn.polardb.game.write;

import com.lkn.polardb.game.Writer;
import com.lkn.polardb.game.constant.Constant;
import com.lkn.polardb.game.file.AbstractDatabaseFile;
import com.lkn.polardb.game.file.channel.NioHandler;
import com.lkn.polardb.game.file.stream.IoHandler;
import com.lkn.polardb.game.imp.WriterImp;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @author likangning
 * @since 2019/1/16 下午5:34
 */
public class WriteTest {

	/**
	 * 运行模式
	 * 1：普通io
	 * 2：nio方式
	 */
	private int mode = 1;

	private char[] items = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

	private boolean reulst = cleanFile();

	private Writer writer = new WriterImp(mode == 1 ? new IoHandler() : new NioHandler());

	@Test
	public void test() throws Exception {
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			String key = genericKey(i);
			String val = genericVal(key);
			writer.write(key.getBytes(), val.getBytes());
		}
		long end = System.currentTimeMillis();
		System.out.println("cost time : " + (end - begin));
	}

	/**
	 * 在进行后续操作之前首先清空文件所有内容
	 */
	private boolean cleanFile() {
		try {
			if (AbstractDatabaseFile.KEY_FILE.exists()) {
				try (FileOutputStream keyFileClean = new FileOutputStream(AbstractDatabaseFile.KEY_FILE)) {
					keyFileClean.write(new byte[0]);
				}
			}
			if (AbstractDatabaseFile.VAL_FILE.exists()) {
				try (FileOutputStream valFileClean = new FileOutputStream(AbstractDatabaseFile.VAL_FILE)) {
					valFileClean.write(new byte[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 *
	 *
	 * 2827
	 * 2938
	 * 2920
	 * 生成value
	 */
	private String genericVal(String key) {
		StringBuilder valBuilder = new StringBuilder();
		for (int q = 0; q < Constant.VAL_SIZE / Constant.KEY_SIZE; q++) {
			valBuilder.append(key);
		}
		return valBuilder.toString();
	}

	/**
	 * 生成key
	 */
	private String genericKey(int num) {
		StringBuilder sb = new StringBuilder();
		appendKeyStr(num, sb);
		return sb.toString();
	}

	private void appendKeyStr(int num, StringBuilder sb) {
		int size = 8;
		if (sb.length() >= size) {
			sb.reverse();
			return ;
		}
		int index = num % size;
		sb.append(items[index]);
		appendKeyStr(num / size, sb);
	}


	@Test
	public void aaa() {
		long cuboidId = 0L;
		for (int i = 0; i < 8; i++) {
			System.out.println("i=" + i + " : " + (1L << i));
			cuboidId |= 1L << i;
		}

		System.out.println();

		System.out.println(cuboidId);
	}


}

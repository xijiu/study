package com.lkn.polardb.game.write;

import com.lkn.polardb.game.Writer;
import com.lkn.polardb.game.constant.Constant;
import com.lkn.polardb.game.file.stream.StreamHandler;
import com.lkn.polardb.game.imp.WriterImp;
import org.junit.Test;

/**
 * @author likangning
 * @since 2019/1/16 下午5:34
 */
public class WriteTest {

	private char[] items = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

	private Writer writer = new WriterImp(new StreamHandler());

	@Test
	public void test() {
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			String key = genericKey(i);
			String val = genericVal(key);
			writer.write(key.getBytes(), val.getBytes());
		}
		long end = System.currentTimeMillis();
		System.out.println("cost time : " + (end - begin) / 1000);
	}

	/**
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


}

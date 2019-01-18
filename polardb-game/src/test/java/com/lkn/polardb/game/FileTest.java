package com.lkn.polardb.game;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * @author likangning
 * @since 2019/1/17 下午12:30
 */
public class FileTest {

	private File file = new File(System.getProperty("user.dir") + "/db/test.db");

	private int writeTimes = 100000;

	private long beginTime = 0L;

	@Before
	public void before() throws Exception {
		if (!file.exists()) {
			file.createNewFile();
		} else {
			file.delete();
			file.createNewFile();
		}
		beginTime = System.currentTimeMillis();
	}

	@After
	public void after() {
		long end = System.currentTimeMillis();
		System.out.println("总共耗时：(ms) : " + (end - beginTime));
	}


	@Test
	public void write1() throws Exception {
		try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
			for (int i = 0; i < writeTimes; i++) {
				fileOutputStream.write(content(4096));
				fileOutputStream.flush();
			}
		}
	}

	@Test
	public void write2() throws Exception {
		for (int i = 0; i < writeTimes; i++) {
			try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
				fileOutputStream.write(content(4096));
				fileOutputStream.flush();
			}
		}
	}

	private byte[] content(int size) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append('a');
		}
		return sb.toString().getBytes();
	}
}

package com.lkn.polardb.game;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author likangning
 * @since 2019/1/16 下午4:28
 */
public class SimpleTest {

	@Test
	public void test() throws IOException {
		File DB_FILE = new File(System.getProperty("user.dir") + "/data.db");
		DB_FILE.createNewFile();
	}

	@Test
	public void test2() throws Exception {
		File file = new File("/Users/likangning/myTest/abc.txt");
		FileOutputStream outputStream = new FileOutputStream(file, false);
		Integer a = 1;
		outputStream.write("1234567890".getBytes());
	}
	@Test
	public void test3() throws Exception {
		byte[] bytes = PubTools.intToBytes(1);
		System.out.println(PubTools.bytesToInt(bytes, 0));
	}
}

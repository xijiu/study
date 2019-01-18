package com.lkn.polardb.game.file;

import com.lkn.polardb.game.PubTools;

import java.io.*;
import java.util.Arrays;

/**
 * 持久化文件
 * @author likangning
 * @since 2019/1/16 下午4:26
 */
public abstract class AbstractDatabaseFile implements DatabaseReader, DatabaseWriter {
	protected final File KEY_FILE = new File(System.getProperty("user.dir") + "/db/data.key");
	protected final File VAL_FILE = new File(System.getProperty("user.dir") + "/db/data.val");


//	/**
//	 * 存储val的偏移量，初始化为-1
//	 */
//	private static int VAL_OFFSET = -1;
//
//	static {
//		createFile();
//	}

//	public boolean write(byte[] key, byte[] value) throws IOException {
//		// 每次写文件时，清空已有文件
//		cleanFile();
//		try (FileOutputStream valOutStream = new FileOutputStream(VAL_FILE, true)) {
//			valOutStream.write(value);
//		}
//
//		int currentOffset = getCurrentOffset();
//		byte[] offsetBytes = PubTools.intToBytes(currentOffset);
//
//		try (FileOutputStream keyOutStream = new FileOutputStream(KEY_FILE, true)) {
//			keyOutStream.write(key);
//			keyOutStream.write(offsetBytes);
//		}
//		return true;
//	}
//
//	/**
//	 * 删除文件
//	 */
//	private static void cleanFile() {
//		if (VAL_OFFSET == -1) {
//			KEY_FILE.delete();
//			VAL_FILE.delete();
//		}
//	}
//
//	public byte[] read(byte[] key) throws IOException {
//		try (FileInputStream inputStream = new FileInputStream(KEY_FILE)) {
//			byte[] arr = new byte[12];
//			while (inputStream.read(arr) != -1) {
//				if (Arrays.equals(key, Arrays.copyOfRange(arr, 0, 8))) {
//					int offset = PubTools.bytesToInt(arr, 8);
//					try (FileInputStream valInputStream = new FileInputStream(VAL_FILE)) {
//						byte[] valArr = new byte[4096];
//						valInputStream.skip(offset * 4096);
//						if (valInputStream.read(valArr) != -1) {
//							return valArr;
//						}
//					}
//					return null;
//				}
//			}
//		}
//		return null;
//	}

//	/**
//	 * 创建新文件
//	 */
//	private static void createFile() {
//		try {
//			KEY_FILE.createNewFile();
//			VAL_FILE.createNewFile();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	private static int getCurrentOffset() {
//		if (VAL_OFFSET >= 0) {
//			return VAL_OFFSET++;
//		} else {
//			// 单位字节
//			long length = VAL_FILE.length();
//			VAL_OFFSET = (int) length / 4096;
//			return VAL_OFFSET - 1;
//		}
//	}
}

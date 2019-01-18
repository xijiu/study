package com.lkn.polardb.game.file.stream;

import com.lkn.polardb.game.PubTools;
import com.lkn.polardb.game.constant.Constant;
import com.lkn.polardb.game.file.AbstractDatabaseFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 普通流的方式读取文件
 */
public class StreamHandler extends AbstractDatabaseFile {

	private FileInputStream keyInputStream;
	private FileOutputStream keyOutputStream;
	private FileInputStream valInputStream;
	private FileOutputStream valOutputStream;
	private int VAL_OFFSET = -1;

	public StreamHandler() {
		try {
			if (!KEY_FILE.exists()) {
				KEY_FILE.createNewFile();
			}
			if (!VAL_FILE.exists()) {
				VAL_FILE.createNewFile();
			}
			keyInputStream = new FileInputStream(KEY_FILE);
			keyOutputStream = new FileOutputStream(KEY_FILE, true);
			valInputStream = new FileInputStream(VAL_FILE);
			valOutputStream = new FileOutputStream(VAL_FILE, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] read(byte[] key) {
		try {
			byte[] arr = new byte[Constant.KEY_SIZE + Constant.OFFSET_SIZE];
			while (keyInputStream.read(arr) != -1) {
				// 已找到key
				if (Arrays.equals(key, Arrays.copyOfRange(arr, 0, Constant.KEY_SIZE))) {
					int offset = PubTools.bytesToInt(arr, Constant.KEY_SIZE);
					System.out.println(offset);
					// 跳转至偏移量位置
					valInputStream.skip(offset * Constant.VAL_SIZE);
					byte[] valArr = new byte[Constant.VAL_SIZE];
					// 向后读取4096子节的数据
					if (valInputStream.read(valArr) != -1) {
						return valArr;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				keyInputStream = new FileInputStream(KEY_FILE);
				valInputStream = new FileInputStream(VAL_FILE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public boolean write(byte[] key, byte[] value) {
		try {
			cleanFile();
			valOutputStream.write(value);
			valOutputStream.flush();

			int currentOffset = getCurrentOffset();
			System.out.println(currentOffset);
			byte[] offsetBytes = PubTools.intToBytes(currentOffset);

			keyOutputStream.write(key);
			keyOutputStream.write(offsetBytes);
			keyOutputStream.flush();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void cleanFile() throws IOException {
		if (VAL_OFFSET == -1) {
			FileOutputStream keyFileClean = new FileOutputStream(KEY_FILE);
			FileOutputStream valFileClean = new FileOutputStream(VAL_FILE);
			keyFileClean.write(new byte[0]);
			valFileClean.write(new byte[0]);
		}
	}

	private int getCurrentOffset() {
		if (VAL_OFFSET >= 0) {
			return VAL_OFFSET++;
		} else {
			// 单位字节
			long length = VAL_FILE.length();
			VAL_OFFSET = (int) length / Constant.VAL_SIZE;
			return VAL_OFFSET - 1;
		}
	}
}

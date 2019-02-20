package com.lkn.polardb.game.file;

import com.lkn.polardb.game.constant.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 持久化文件
 * @author likangning
 * @since 2019/1/16 下午4:26
 */
public abstract class AbstractDatabaseFile implements DatabaseReader, DatabaseWriter {
	public static final File KEY_FILE = new File(System.getProperty("user.dir") + "/db/data.key");
	public static final File VAL_FILE = new File(System.getProperty("user.dir") + "/db/data.val");

	protected int VAL_OFFSET = -1;


	protected int getCurrentOffset() {
		if (VAL_OFFSET >= 0) {
			return VAL_OFFSET++;
		} else {
			// 单位字节
			long length = VAL_FILE.length();
			VAL_OFFSET = (int) length / Constant.VAL_SIZE;
			return VAL_OFFSET - 1;
		}
	}

	public void cleanFile() throws IOException {
		if (VAL_OFFSET == -1) {
			try (FileOutputStream keyFileClean = new FileOutputStream(KEY_FILE);
					 FileOutputStream valFileClean = new FileOutputStream(VAL_FILE)) {
				keyFileClean.write(new byte[0]);
				valFileClean.write(new byte[0]);
			}
		}
	}
}

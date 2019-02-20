package com.lkn.polardb.game.file.channel;

import com.lkn.polardb.game.PubTools;
import com.lkn.polardb.game.constant.Constant;
import com.lkn.polardb.game.file.AbstractDatabaseFile;
import org.apache.commons.lang3.ArrayUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * nio的读取文件的方式
 */
public class NioHandler extends AbstractDatabaseFile {

	private FileChannel valChannel;
	private FileChannel keyChannel;
	private MappedByteBuffer valMappedByteBuffer;
	private MappedByteBuffer keyMappedByteBuffer;

	private int valPageSize = Constant.VAL_SIZE * 1024;
//	private long valPageSize = Integer.MAX_VALUE;
	private long keyPageSize = (Constant.KEY_SIZE + Constant.OFFSET_SIZE) * 1024;
//	private long keyPageSize = Integer.MAX_VALUE;


	public NioHandler() {
		try {
			if (!KEY_FILE.exists()) {
				KEY_FILE.createNewFile();
			}
			if (!VAL_FILE.exists()) {
				VAL_FILE.createNewFile();
			}
			valChannel = FileChannel.open(Paths.get(VAL_FILE.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
//			valChannel = new RandomAccessFile(VAL_FILE, "rw").getChannel();
			keyChannel = FileChannel.open(Paths.get(KEY_FILE.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
			keyChannel = new RandomAccessFile(KEY_FILE, "rw").getChannel();
			valMappedByteBuffer = valChannel.map(FileChannel.MapMode.READ_WRITE, 0, valPageSize);
			keyMappedByteBuffer = keyChannel.map(FileChannel.MapMode.READ_WRITE, 0, keyPageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] read(byte[] key) {
		try {
			FileInputStream keyInputStream = new FileInputStream(KEY_FILE);
			FileChannel channel = keyInputStream.getChannel();
			MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, 10);
//			mappedByteBuffer
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	@Override
	public boolean write(byte[] key, byte[] value) {
		try {
			adjustIfNecessary();

			valMappedByteBuffer.put(value);

			int currentOffset = getCurrentOffset();
			byte[] offsetBytes = PubTools.intToBytes(currentOffset);

			byte[] keyArr = ArrayUtils.addAll(key, offsetBytes);
			keyMappedByteBuffer.put(keyArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void adjustIfNecessary() throws IOException {
		int valRemaining = valMappedByteBuffer.remaining();
		if (valRemaining < Constant.VAL_SIZE) {
			System.out.println("发生一次重置val");
			valMappedByteBuffer = valChannel.map(FileChannel.MapMode.READ_WRITE, VAL_FILE.length(), valPageSize);
		}

		int keyRemaining = keyMappedByteBuffer.remaining();
		if (keyRemaining < Constant.KEY_SIZE) {
			System.out.println("发生一次重置key");
			keyMappedByteBuffer = valChannel.map(FileChannel.MapMode.READ_WRITE, KEY_FILE.length(), keyPageSize);
		}
	}


}
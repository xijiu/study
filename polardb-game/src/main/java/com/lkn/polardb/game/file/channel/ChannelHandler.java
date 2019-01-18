package com.lkn.polardb.game.file.channel;

import com.lkn.polardb.game.file.AbstractDatabaseFile;

import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * nio的读取文件的方式
 */
public class ChannelHandler extends AbstractDatabaseFile {


	@Override
	public byte[] read(byte[] key) {
		try {
			FileInputStream keyInputStream = new FileInputStream(KEY_FILE);
			FileChannel channel = keyInputStream.getChannel();
			MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 10);
//			mappedByteBuffer
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	@Override
	public boolean write(byte[] key, byte[] value) {
		return false;
	}
}

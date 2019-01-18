package com.lkn.polardb.game;

public interface Writer {

	/**
	 * 写接口
	 * @param key	主键 key 8B
	 * @param value	内容 value 4KB
	 * @return	是否写入成功
	 */
	boolean write(byte[] key, byte[] value);
}

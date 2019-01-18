package com.lkn.polardb.game;

public interface Reader {

	/**
	 * 读接口
	 * @param key	主键id
	 * @return	当前key对应的val
	 */
	byte[] read(byte[] key);
}

package com.lkn.polardb.game.imp;

import com.lkn.polardb.game.Reader;
import com.lkn.polardb.game.file.DatabaseReader;

/**
 * @author likangning
 * @since 2019/1/16 下午8:08
 */
public class ReaderImp implements Reader {

	private DatabaseReader databaseReader;

	public ReaderImp(DatabaseReader databaseReader) {
		this.databaseReader = databaseReader;
	}

	@Override
	public byte[] read(byte[] key) {
		return databaseReader.read(key);
	}
}

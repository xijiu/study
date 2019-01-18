package com.lkn.polardb.game.imp;

import com.lkn.polardb.game.Writer;
import com.lkn.polardb.game.file.DatabaseWriter;

/**
 * @author likangning
 * @since 2019/1/16 下午3:34
 */
public class WriterImp implements Writer {

	private DatabaseWriter databaseWriter;

	public WriterImp(DatabaseWriter databaseWriter) {
		this.databaseWriter = databaseWriter;
	}
	@Override
	public boolean write(byte[] key, byte[] value) {
		return databaseWriter.write(key, value);
	}
}

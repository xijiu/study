package com.lkn.polardb.game.read;

import com.lkn.polardb.game.Reader;
import com.lkn.polardb.game.file.stream.IoHandler;
import com.lkn.polardb.game.imp.ReaderImp;
import org.junit.Test;

/**
 * @author likangning
 * @since 2019/1/17 上午8:34
 */
public class ReaderTest {

	private Reader reader = new ReaderImp(new IoHandler());

	@Test
	public void readTest() {
		System.out.println(get("aaaaaaaa"));
		System.out.println(get("aaaaaaab"));
		System.out.println(get("aaaaaaac"));
		System.out.println(get("aaaaaaad"));
		System.out.println(get("aaaaaaae"));
		System.out.println(get("aaaaaaaf"));
	}

	private String get(String key) {
		byte[] val = this.reader.read(key.getBytes());
		if (val == null) {
			return "null";
		}
		return new String(val);
	}
}

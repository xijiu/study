package com.lkn;

import org.junit.Test;

/**
 * @author likangning
 * @since 2019/1/14 下午5:28
 */
public class SimpleTest {

	/**
	 * 556
	 * 20180501
	 */
	@Test
	public void abc() {
		String str = "00000000000000000401800000000000022b00";
		System.out.println(str.length());
	}

	@Test
	public void aaa() {
		int a = 5;
		String abc = new String("a");
		System.out.println(abc.getBytes().length);

//		Character single = new Character('a');
//		System.out.println(single);

		byte[] bytes = intToByteArray(a);
		System.out.println(bytes.length);



	}

	public static byte[] intToByteArray(int a) {
		return new byte[] {
				(byte) ((a >> 24) & 0xFF),
				(byte) ((a >> 16) & 0xFF),
				(byte) ((a >> 8) & 0xFF),
				(byte) (a & 0xFF)
		};
	}
}

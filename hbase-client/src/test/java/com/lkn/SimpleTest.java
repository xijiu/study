package com.lkn;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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

	@Test
	public void bbb() {
		Long a = 556L;
		byte[] bytes = long2byte(a);
		System.out.println(toHexString(bytes));
	}

	public static byte[] long2byte(long res) {
		byte[] buffer = new byte[8];
		for (int i = 0; i < 8; i++) {
			int offset = 64 - (i + 1) * 8;
			buffer[i] = (byte) ((res >> offset) & 0xff);
		}
		return buffer;
	}

	public static String toHexString(byte[] byteArray) {
		if (byteArray == null || byteArray.length < 1)
			throw new IllegalArgumentException("this byteArray must not be null or empty");

		final StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
				hexString.append("0");
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return hexString.toString().toLowerCase();
	}

	@Test
	public void hashTest() {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 20; i++) {
			map.put(String.valueOf(i), String.valueOf(i));
		}
	}

	@Test
	public void aaaaa() {
		int hash = "abg".hashCode();
		int result = (16 - 1) & hash;
		System.out.println(result);
		System.out.println(64F * 0.75F);
	}


}

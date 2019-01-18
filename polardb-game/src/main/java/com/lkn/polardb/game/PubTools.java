package com.lkn.polardb.game;

/**
 * @author likangning
 * @since 2019/1/16 下午5:27
 */
public class PubTools {

	public static byte[] intToBytes(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	public static int bytesToInt(byte[] src, int offset) {
		return ((src[offset] & 0xFF) << 24)
				| ((src[offset + 1] & 0xFF) << 16)
				| ((src[offset + 2] & 0xFF) << 8)
				| (src[offset + 3] & 0xFF);
	}

}

package com.lkn.unsafe;

import com.google.common.collect.Sets;
import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

/**
 * @author likangning
 * @since 2020/6/24 下午9:23
 */
public class MyTest {
	public static Unsafe getUnsafe() {
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			return (Unsafe) theUnsafe.get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	public static int[] unsafeTwoBytesToInts(byte[] bytes) {
		Unsafe unsafe = getUnsafe();
		int[] ints = new int[bytes.length / 2];
		for (int i = 0; i < ints.length; i++)
			ints[i] = Short.reverseBytes(
					unsafe.getShort(bytes, i * 2 + Unsafe.ARRAY_BYTE_BASE_OFFSET)) & 0xFFFF;
		return ints;
	}

	@Test
	public void abc() {
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET";
		int count = 0;
		byte[] lineByteArr = str.getBytes();
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			int j;
			int beginPos = 0;
			while (true) {
				for (j = beginPos; j < lineByteArr.length; j++) {
					if (lineByteArr[j] == 124) {
						count++;
						beginPos = j + 1;
						break;
					}
				}
				if (j >= lineByteArr.length) {
					break;
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
//		Unsafe unsafe = getUnsafe();
//		long val = unsafe.getLong(lineByteArr, 0 + Unsafe.ARRAY_BYTE_BASE_OFFSET);
//		System.out.println(val);


//		long abc = byteArr2Long(lineByteArr, 0);
//		System.out.println(abc);
	}

	@Test
	public void abc33() {
		int count = 0;
		byte[] lineByteArr = str.getBytes();
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			for (int j = 0; j < lineByteArr.length - 4; j++) {
				if (lineByteArr[j] == 49 && lineByteArr[j + 1] == 57 && lineByteArr[j + 2] == 50) {
					count++;
					break;
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
//		Unsafe unsafe = getUnsafe();
//		long val = unsafe.getLong(lineByteArr, 0 + Unsafe.ARRAY_BYTE_BASE_OFFSET);
//		System.out.println(val);


//		long abc = byteArr2Long(lineByteArr, 0);
//		System.out.println(abc);
	}

	@Test
	public void abc2() {
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526526526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET\n";
		Unsafe unsafe = getUnsafe();
		int count = 0;
		byte[] lineByteArr = str.getBytes();
		int times = lineByteArr.length / 8 + 1;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			for (int j = 0; j < times; j++) {
				long val = unsafe.getLong(lineByteArr, j * 8 + Unsafe.ARRAY_BYTE_BASE_OFFSET);
				if ((byte) (val >>> 56) == 10) {
					count++;
				} else if ((byte) (val >>> 48) == 10) {
					count++;
				} else if ((byte) (val >>> 40) == 10) {
					count++;
				} else if ((byte) (val >>> 32) == 10) {
					count++;
				} else if ((byte) (val >>> 24) == 10) {
					count++;
				} else if ((byte) (val >>> 16) == 10) {
					count++;
				} else if ((byte) (val >>> 8) == 10) {
					count++;
				} else if ((byte) (val) == 10) {
					count++;
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
	}
	@Test
	public void abc3334() {
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526526526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET\n";
		Unsafe unsafe = getUnsafe();
		int count = 0;
		byte[] lineByteArr = str.getBytes();
		int times = lineByteArr.length / 4 + 1;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			for (int j = 0; j < times; j++) {
				int val = unsafe.getInt(lineByteArr, j * 4 + Unsafe.ARRAY_BYTE_BASE_OFFSET);
				if ((byte) (val >> 24) == 10) {
					count++;
				} else if ((byte) (val >> 16) == 10) {
					count++;
				} else if ((byte) (val >> 8) == 10) {
					count++;
				} else if ((byte) (val) == 10) {
					count++;
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void abc333() {
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526526526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET\n";
		int count = 0;
		byte[] lineByteArr = str.getBytes();
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			for (byte b : lineByteArr) {
				if (b == 10) {
					count++;
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void abc33333() {
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526526526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET\n";
		int count = 0;
		byte[] lineByteArr = str.getBytes();
		int index;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			index = 0;
			while (true) {
				byte b = lineByteArr[index++];
				if (b == 10) {
					count++;
					break;
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void abc3333() {
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526526526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET\n";
		int count = 0;
		byte[] lineByteArr = str.getBytes();
		int index;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			index = 0;
			while (true) {
				byte b = lineByteArr[index++];
				if (b == 10) {
					count++;
					break;
				}
				if (b == 124) {
					index += 25;
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
	}

	// 775043377 825111097 909192754 943075630
	@Test
	public void aaa111() {
		Unsafe unsafe = getUnsafe();
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526526526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET";
		byte[] bytes = str.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == '1' && bytes[i + 1] == '9' && bytes[i + 2] == '2') {
				int val1 = unsafe.getInt(bytes, i + Unsafe.ARRAY_BYTE_BASE_OFFSET);
				int val2 = unsafe.getInt(bytes, i + 1 + Unsafe.ARRAY_BYTE_BASE_OFFSET);
				int val3 = unsafe.getInt(bytes, i + 2 + Unsafe.ARRAY_BYTE_BASE_OFFSET);
				int val4 = unsafe.getInt(bytes, i + 3 + Unsafe.ARRAY_BYTE_BASE_OFFSET);
				System.out.println(val1);
				System.out.println(val2);
				System.out.println(val3);
				System.out.println(val4);
				return;
			}
		}
	}

	@Test
	public void aaa11122() {
		Unsafe unsafe = getUnsafe();
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526526526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET";
		byte[] bytes = str.getBytes();
		int count = 0;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 100000000; i++) {
			int index = 0;
			while (true) {
				int val = unsafe.getInt(bytes, index + Unsafe.ARRAY_BYTE_BASE_OFFSET);
				index += 4;
				if (val == 775043377 || val == 825111097 || val == 909192754 || val == 943075630) {
					count++;
					break;
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void aaa111222() {
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526526526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET";
		byte[] bytes = str.getBytes();
		int count = 0;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 100000000; i++) {
			int index = 0;
			int myCount = 0;
			while (true) {
				if (bytes[index++] == 124) {
					if (++myCount == 8) {
						count++;
						break;
					}
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
	}

	private static String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526526526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET";

	@Test
	public void abc2222() {
		System.out.println((byte)'h');
		Unsafe unsafe = getUnsafe();
		int count = 0;
		byte[] lineByteArr = str.getBytes();
		int times = lineByteArr.length / 4 + 1;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			for (int j = 0; j < times; j++) {
				int val = unsafe.getInt(lineByteArr, j * 4 + Unsafe.ARRAY_BYTE_BASE_OFFSET);
				// 775043377 825111097 909192754 943075630
				if (val == 775043377 || val == 825111097 || val == 909192754 || val == 943075630) {
					count++;
					break;
				}
			}
		}
		System.out.println(count);
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void abc3() {
//		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526526526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET";
		String str = "\n";
		Unsafe unsafe = getUnsafe();
		int count = 0;
		byte[] lineByteArr = str.getBytes();

		long val = unsafe.getLong(lineByteArr, 0 + Unsafe.ARRAY_BYTE_BASE_OFFSET);
		System.out.println(val);
	}

	public static void long2ByteArr(long target, byte[] bytes, int index) {
		bytes[index] = (byte) (target >> 56);
		bytes[index + 1] = (byte) (target >> 48);
		bytes[index + 2] = (byte) (target >> 40);
		bytes[index + 3] = (byte) (target >> 32);
		bytes[index + 4] = (byte) (target >> 24);
		bytes[index + 5] = (byte) (target >> 16);
		bytes[index + 6] = (byte) (target >> 8);
		bytes[index + 7] = (byte) (target);
	}

	public static long byteArr2Long(byte[] bs, int beginIndex) {
		return (bs[beginIndex] & 0xffL) <<56 | (bs[beginIndex + 1] & 0xffL) << 48 | (bs[beginIndex + 2] & 0xffL) <<40 | (bs[beginIndex + 3] & 0xffL)<<32 |
				(bs[beginIndex + 4] & 0xffL) <<24 | (bs[beginIndex + 5] & 0xffL) << 16 | (bs[beginIndex + 6] & 0xffL) <<8 | (bs[beginIndex + 7] & 0xffL);
	}

	/**
	 *
	 10
	 2560
	 655360
	 167772160
	 42949672960
	 10995116277760
	 2814749767106560
	 720575940379279360
	 */
	@Test
	public void sss() {
		System.out.println(Long.parseLong("00000000000000000000000000000000000000000000000000000000 00001010".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("000000000000000000000000000000000000000000000000 00001010 00000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("0000000000000000000000000000000000000000 00001010 0000000000000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("00000000000000000000000000000000 00001010 000000000000000000000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("000000000000000000000000 00001010 00000000000000000000000000000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("0000000000000000 00001010 0000000000000000000000000000000000000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("00000000 00001010 000000000000000000000000000000000000000000000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("00001010 00000000000000000000000000000000000000000000000000000000".replaceAll(" ", ""), 2));


		long num = 200000010L;
		System.out.println(num & 10L);
	}


	/**
	 *
	 124
	 31744
	 8126464
	 2080374784
	 532575944704
	 136339441844224
	 34902897112121344
	 8935141660703064064
	 */
	@Test
	public void sss2() {
		System.out.println(Long.parseLong("00000000000000000000000000000000000000000000000000000000 1111100".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("000000000000000000000000000000000000000000000000 1111100 00000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("0000000000000000000000000000000000000000 1111100 0000000000000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("00000000000000000000000000000000 1111100 000000000000000000000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("000000000000000000000000 1111100 00000000000000000000000000000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("0000000000000000 1111100 0000000000000000000000000000000000000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("00000000 1111100 000000000000000000000000000000000000000000000000".replaceAll(" ", ""), 2));
		System.out.println(Long.parseLong("1111100 00000000000000000000000000000000000000000000000000000000".replaceAll(" ", ""), 2));
	}

	@Test
	public void abc111() {
		String str = "rofInter";
		StringBuilder sb = new StringBuilder(str);
		str = sb.reverse().toString();
		byte[] bytes = str.getBytes();
		long longValue = byteArr2Long(bytes, 0);
		System.out.println(longValue & 10);
		System.out.println(longValue & 2560);
		System.out.println(Long.toBinaryString(longValue));
		System.out.println(Integer.toBinaryString(2560));

		System.out.println((byte)'&');
	}


	@Test
	public void abc222() {
		String str = "f75bba209e8a935|1590216618729493|468867cbb73092fb|f75bba209ef8935|526|Frontend|DoGetAppsGrid|192.168.138.216|http.status_code=500&component=java-spring-rest-template&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET&span.kind=client&peer.port=9005&http.method=GET";
		byte[] lineByteArr = str.getBytes();
		int endIndex = lineByteArr.length - 1;
		while (true) {
			// "error=1" :  101 114 114 111 114 61 49
			if (lineByteArr[endIndex] == 49) {
				if (lineByteArr[endIndex - 1] == 61) {
					if (lineByteArr[endIndex - 2] == 114) {
						if (lineByteArr[endIndex - 3] == 111) {
							if (lineByteArr[endIndex - 4] == 114) {
								if (lineByteArr[endIndex - 5] == 114) {
									if (lineByteArr[endIndex - 6] == 101) {
										break;
									}
								}
							}
						}
					}
				}
			}

			// "http.status_code="  :  104 116 116 112 46 115 116 97 116 117 115 95 99 111 100 101 61
			if (lineByteArr[endIndex] > 47 && lineByteArr[endIndex] < 58) {
				if (lineByteArr[endIndex - 1] > 47 && lineByteArr[endIndex - 1] < 58) {
					if (lineByteArr[endIndex - 2] > 47 && lineByteArr[endIndex - 2] < 58) {
						if (lineByteArr[endIndex - 3] == 61) {
							if (lineByteArr[endIndex - 4] == 101) {
								if (lineByteArr[endIndex - 5] == 100) {
									if (lineByteArr[endIndex - 6] == 111) {
										if (lineByteArr[endIndex - 7] == 99) {
											if (lineByteArr[endIndex - 8] == 95) {
												if (lineByteArr[endIndex - 9] == 115) {
													if (lineByteArr[endIndex - 10] == 117) {
														if (lineByteArr[endIndex - 11] == 116) {
															if (lineByteArr[endIndex - 12] == 97) {
																if (lineByteArr[endIndex - 13] == 116) {
																	if (lineByteArr[endIndex - 14] == 115) {
																		if (lineByteArr[endIndex - 15] == 46) {
																			if (lineByteArr[endIndex - 16] == 112) {
																				if (lineByteArr[endIndex - 17] == 116) {
																					if (lineByteArr[endIndex - 18] == 116) {
																						if (lineByteArr[endIndex - 19] == 104) {
																							break;
																						}
																					}
																				}
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if (lineByteArr[endIndex] == 124) {
				break;
			}
			--endIndex;
		}
	}


	@Test
	public void abctest() {
		String str = "6c528f4fe5cf5492|1592840907843108|3fcec995dbf47462|547868d1ba177b97|146|OrderCenter|checkAndRefresh|192.168.111.186|http.status_code=200&component=java-web-servlet&span.kind=server&http.url=http://tracing.console.com/createOrder&&component=java-web-servlet&span.kind=server&http.url=http://tracing.console.com/createOrder&entrance=pc&http.method=GET&userId=89982&entrance=pc&http.method=GET&userId=89982&userId=89982\n";
		byte[] bytes = str.getBytes();
		long begin = System.currentTimeMillis();
		boolean result = false;
		for (int i = 0; i < 10000000; i++) {
			result = isBadTraceData2(bytes, bytes.length - 1);
//			result = isBadTraceData3(bytes, 116);
		}
		System.out.println("cost is : " + (System.currentTimeMillis() - begin));
		System.out.println(result);

	}

	byte tmp;

	private boolean isBadTraceData(byte[] lineByteArr, int endIndex) {
		endIndex = lineByteArr[endIndex] == 38 ? endIndex - 1 : endIndex;
		int totalEndIndex = endIndex;

		tmp = lineByteArr[endIndex - 7];
		if (tmp == 38 || tmp == 124) {
			return true;
		}
		tmp = lineByteArr[endIndex - 20];
		if ((tmp == 38 || tmp == 124) && lineByteArr[endIndex - 20] == 104) {
			return lineByteArr[endIndex - 2] != 50;
		}
		endIndex -= 10;

		// & 38        | 124
		while (true) {
			for (int i = endIndex; i > 0; i--) {
				byte current = lineByteArr[i];
				if (current == 124) {
					return false;
				}
				if (i < totalEndIndex && current != 38) {
					continue;
				}
				if (lineByteArr[i - 1] == 38) {
					continue;
				}
				tmp = lineByteArr[i - 8];
				if (tmp == 38 || tmp == 124) {
					return true;
				}
				tmp = lineByteArr[i - 21];
				if ((tmp == 38 || tmp == 124) && lineByteArr[i - 20] == 104) {
					return lineByteArr[i - 3] != 50;
				}

				// ?id=    63 105 100 61
//                if (lineByteArr[i - 5] == 63) {
//                    if (lineByteArr[i - 44] == 38) {
//                        endIndex -= 44;
//                    } else if (lineByteArr[i - 57] == 38) {
//                        endIndex -= 57;
//                    } else if (lineByteArr[i - 59] == 38) {
//                        endIndex -= 59;
//                    } else if (lineByteArr[i - 60] == 38) {
//                        endIndex -= 60;
//                    } else {
//                        endIndex -= 61;
//                    }
//                } else {
//                    endIndex -= 10;
//                }
				endIndex -= 10;
				break;
			}
		}
	}


	private boolean isBadTraceData2(byte[] lineByteArr, int endIndex) {
		endIndex = lineByteArr[endIndex] == 38 ? endIndex - 1 : endIndex;

		byte tmp;
		// & 38        | 124
		while (true) {
			tmp = lineByteArr[endIndex - 7];
			if (tmp == 38 || tmp == 124) {
				return true;
			}
			tmp = lineByteArr[endIndex - 20];
			if ((tmp == 38 || tmp == 124) && lineByteArr[endIndex - 19] == 104) {
				return lineByteArr[endIndex - 2] != 50;
			}
			if (lineByteArr[endIndex] == 38) {
				endIndex = endIndex - 1;
				continue;
			}

			// ?id=    63 105 100 61
//			if (lineByteArr[endIndex - 4] == 63) {
//				if (lineByteArr[endIndex - 43] == 38) {
//					endIndex -= 43;
//				} else if (lineByteArr[endIndex - 56] == 38) {
//					endIndex -= 56;
//				} else if (lineByteArr[endIndex - 58] == 38) {
//					endIndex -= 58;
//				} else if (lineByteArr[endIndex - 59] == 38) {
//					endIndex -= 59;
//				} else {
//					endIndex -= 60;
//				}
//				continue;
//			} else {
//				endIndex -= 10;
//			}

			endIndex -= 10;

			while (true) {
				tmp = lineByteArr[endIndex--];
				if (tmp == 38) {
					break;
				} else if (tmp == 124) {
					return false;
				}
			}
		}
	}

	private boolean isBadTraceData3(byte[] data, int beginPos) {
		beginPos = data[beginPos] == 38 ? beginPos + 1 : beginPos;

		byte tmp;
		// & 38        | 124
		while (true) {
			if (data[beginPos] == 10) {
				return false;
			}
			tmp = data[beginPos + 7];
			if (tmp == 38 || tmp == 10) {
				beginPos += 7;
				while (data[beginPos++] != 10) {
				}
				--beginPos;
				return true;
			}
			if (beginPos + 20 < data.length) {
				tmp = data[beginPos + 20];
				if ((tmp == 38 || tmp == 10) && data[beginPos] == 104) {
					boolean result = data[beginPos + 18] != 50;
					beginPos += 20;
					while (data[beginPos++] != 10) {
					}
					--beginPos;
					return result;
				}
			}

			if (data[beginPos] == 38) {
				++beginPos;
				continue;
			}

			beginPos += 10;

			while (true) {
				tmp = data[beginPos++];
				if (tmp == 38) {
					break;
				} else if (tmp == 10) {
					--beginPos;
					return false;
				}
			}
		}
	}

	@Test
	public void abctest333() {
		String str = "1c4fb8577e2083c4|1592840909310711|1fe990551982895|52fe6e0cd846661|434|Frontend|db.UserFeeStatusDao.query(..)|192.168.192.27|&http.status_code=504&component=java-spring-rest-template&span.kind=client&http.url=http://tracing.console.com/createOrder?id=2&peer.port=9002&http.method=GET&http.method=GET\n";
		byte[] bytes = str.getBytes();
//		for (int i = 0; i < bytes.length; i++) {
//			if (bytes[i] == '|') {
//				System.out.println(i);
//			}
//		}
		long begin = System.currentTimeMillis();
		boolean result = isBadTraceData3(bytes, 124);
		System.out.println(result);
		System.out.println("cost is : " + (System.currentTimeMillis() - begin));

		System.out.println((byte)'h');
		System.out.println((byte)'u');

		int firstIndex = str.indexOf("|");
		System.out.println(firstIndex);
		System.out.println(str.substring(0, firstIndex));

		int index1 = str.indexOf("|", 20);
		System.out.println(str.substring(index1 - 9, index1));
	}

	@Test
	public void abcc() {
//		System.out.println(isBadTrace44());
		String str = " sql";
		byte[] bytes = str.getBytes();
		System.out.println(Arrays.toString(bytes));

	}

	private boolean isBadTrace4() {
		String line = "6d25f0b0538a2d67|1592840904831582|30938c8b33112aed|32d1fe5970d9db15|292|PromotionCenter|processZipkin|192.168.50.142|error=1&db.instance=db&component=java-jdbc&db.type=h2&span.kind=client&__sql_id=xsw624&peer.address=localhost:8082";

		byte[] bytes = line.getBytes();
		int count = 0;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			int index = i % 150;
			byte aByte1 = bytes[index];
			byte aByte2 = bytes[index + 1];
			byte aByte3 = bytes[index + 2];
			byte aByte4 = bytes[index + 3];
			byte aByte5 = bytes[index + 4];
			if (aByte1 == 1) {
				count++;
			}
			if (aByte2 == 1) {
				count++;
			}
			if (aByte3 == 1) {
				count++;
			}
			if (aByte4 == 1) {
				count++;
			}
			if (aByte5 == 1) {
				count++;
			}
		}
		System.out.println(count);
		System.out.println("cost : " + (System.currentTimeMillis() - begin));
		return false;
	}

	private boolean isBadTrace44() {
		String line = "6d25f0b0538a2d67|1592840904831582|30938c8b33112aed|32d1fe5970d9db15|292|PromotionCenter|processZipkin|192.168.50.142|error=1&db.instance=db&component=java-jdbc&db.type=h2&span.kind=client&__sql_id=xsw624&peer.address=localhost:8082";

		byte[] bytes = line.getBytes();
		int count = 0;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			int index = i % 150;
			byte aByte1 = bytes[index];
			if (aByte1 == 1) {
				count++;
			}
			byte aByte2 = bytes[index + 1];
			if (aByte2 == 1) {
				count++;
			}
			byte aByte3 = bytes[index + 2];
			if (aByte3 == 1) {
				count++;
			}
			byte aByte4 = bytes[index + 3];
			if (aByte4 == 1) {
				count++;
			}
			byte aByte5 = bytes[index + 4];
			if (aByte5 == 1) {
				count++;
			}
		}
		System.out.println(count);
		System.out.println("cost : " + (System.currentTimeMillis() - begin));
		return false;
	}

	private boolean isBadTrace5() {
		String line = "6d25f0b0538a2d67|1592840904831582|30938c8b33112aed|32d1fe5970d9db15|292|PromotionCenter|processZipkin|192.168.50.142|error=1&db.instance=db&component=java-jdbc&db.type=h2&span.kind=client&__sql_id=xsw624&peer.address=localhost:8082";

		byte[] bytes = line.getBytes();
		int count = 0;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			int index = i % 150;
			byte aByte5 = bytes[index + 4];
			byte aByte2 = bytes[index + 1];
			byte aByte4 = bytes[index + 3];
			byte aByte3 = bytes[index + 2];
			byte aByte1 = bytes[index];
			if (aByte1 == 1 || aByte2 == 1 || aByte3 == 1 || aByte4 == 1 || aByte5 == 1) {
				count++;
			}
		}
		System.out.println(count);
		System.out.println("cost : " + (System.currentTimeMillis() - begin));
		return false;
	}

	@Test
	public void ccccc() {
		System.out.println(Integer.MAX_VALUE - 1);
	}
}

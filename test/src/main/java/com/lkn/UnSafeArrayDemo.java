package com.lkn;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnSafeArrayDemo {

	private static final sun.misc.Unsafe UNSAFE;
	private static final long TBASE;
	private static final int TSHIFT;

	static {
		int ts;
		try {
			UNSAFE = getUnsafe();
			TBASE = UNSAFE.arrayBaseOffset(String[].class);
			ts = UNSAFE.arrayIndexScale(String[].class);
		} catch (Exception e) {
			throw new Error(e);
		}
		TSHIFT = 31 - Integer.numberOfLeadingZeros(ts);
	}

	static Unsafe getUnsafe() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = Unsafe.class.getDeclaredField("theUnsafe");
		f.setAccessible(true);
		Unsafe unsafe = (Unsafe) f.get(null);
		return unsafe;
	}

	@SuppressWarnings("unchecked")
	static final String entryAt(String[] tab, int i) {
		return (String) UNSAFE.getObjectVolatile(tab, ((long) i << TSHIFT) + TBASE);
	}

	public static void main(String[] args) {
		int nLen = 370;
		String[] table = new String[nLen];
		for (int i = 0; i < nLen; i++) {
			table[i] = String.valueOf(i);
		}
		String str = entryAt(table, 100);
		System.out.println(str);
	}

}
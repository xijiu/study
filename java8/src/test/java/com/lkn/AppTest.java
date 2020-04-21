package com.lkn;

import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lkn.java8.stream.bean.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class AppTest {

	private Map<String, Long> map;
	/**
	 * Rigorous Test :-)
	 */
	@Test
	public void shouldAnswerWithTrue() {
		System.out.println(1L ^ (-1L << (8 * 8 - 1)));
		System.out.println(Short.MAX_VALUE);
		System.out.println(Short.toUnsignedInt((short) -1));
		System.out.println(Byte.toUnsignedInt((byte) -1));
		System.out.println(Byte.MAX_VALUE);
		System.out.println(Byte.MIN_VALUE);
		System.out.println(Integer.toBinaryString(64));
		System.out.println(0x40);
		int num = 138;
		System.out.println(Integer.toBinaryString(num));
		System.out.println(Integer.toBinaryString(0x40));
		System.out.println(0x40 & num);
		System.out.println(Integer.parseUnsignedInt("1111111111", 2));
		System.out.println(Integer.toBinaryString(0x3f));
		int sum = ((138 & 0x3f) << 8);
		System.out.println(Integer.toBinaryString(sum));
	}

	private void print(Map<String, Long> temp) {
		System.out.println("map size : " + map.size());
		System.out.println("temp size : " + temp.size());
	}

	@Setter
	@Getter
	@AllArgsConstructor
	private class Person<T extends Comparable> {
		T content;
	}

	@Setter
	@Getter
	@AllArgsConstructor
	private class Info implements Comparable {
		private String name;

		@Override
		public int compareTo(Object o) {
			return 0;
		}
	}
}

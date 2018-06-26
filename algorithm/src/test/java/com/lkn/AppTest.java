package com.lkn;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

public class AppTest {



	@Test
	public void test() {
		System.out.println("1,2,3,4");
		System.out.println("-        -");
		System.out.println(" -");
		System.out.println("  -");
		System.out.println("   -");
		System.out.println("    -");
	}

	@Test
	public void test2() {
		List<Character> list = Lists.newArrayList();
		addElement(list, '-', 10);
		System.out.println(list);
		addElement(list, '-', 2);
		System.out.println(list);
	}

	private void addElement(List<Character> list, Character element, int index) {
		int originSize = list.size();
		if (index >= list.size()) {
			for (int i = originSize; i <= index; i++) {
				list.add(' ');
			}
		}
		list.remove(index);
		list.add(index, element);
	}

	@Test
	public void test3() {
		calcTwoPoint(0, 10);
	}

	private int height = 5;
	private void calcTwoPoint(int parentPosition, int childPosition) {
		int differ = childPosition - parentPosition;
		for (int i = 0; i < height; i++) {
			int currLevel = parentPosition + (new BigDecimal(differ).divide(new BigDecimal(height))
					.multiply(new BigDecimal(i + 1)).intValue());
			System.out.println(currLevel);
		}
	}

	@Test
	public void test4() {
		int a = 13 / 2;
		System.out.println(a);
	}
}

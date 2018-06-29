package com.lkn;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.lkn.algorithm.b_tree.bean.Element;
import org.junit.Test;

import java.util.List;

public class AppTest {


	@Test
	public void test() {
		Element<Integer> element1 = new Element<>(1);
		Element<Integer> element2 = new Element<>(1);
		System.out.println(element1.equals(element2));
		System.out.println(Objects.equal(element1, element2));
	}

	@Test
	public void test2() {
		List<Character> list = Lists.newArrayList();
		addElement(list, '-', 10);
		System.out.println(list);
		addElement(list, '-', 2);
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



}

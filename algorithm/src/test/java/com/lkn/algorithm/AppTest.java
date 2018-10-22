package com.lkn.algorithm;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.lkn.algorithm.b_tree.TreeDelete;
import com.lkn.algorithm.b_tree.bean.Element;
import org.junit.Test;

import java.util.List;

public class AppTest {


	@Test
	public void test() {
		System.out.println(TreeDelete.MIN_ELEMENT_NUM_PER_NODE);
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

package com.lkn.algorithm.b_tree.base;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;

/**
 * B树的节点，一个节点中可能有多个元素
 *
 * @author likangning
 * @since 2018/6/22 下午12:07
 */
public class Node {
	// 阶数
	public static final int ORDER_NUM = 5;
	public static final int MIDDLE_INDEX = (ORDER_NUM % 2 == 0 ? ORDER_NUM - 2 : ORDER_NUM - 1) / 2;

	/**
	 * 当前节点拥有的元素
 	 */
	private Set<Element> elements = Sets.newTreeSet();

	// 当前节点的父节点
	@Setter
	@Getter
	private Node parent;

	public Node(Node parent) {
		this.parent = parent;
	}

	public Set<Element> getElements() {
		return Collections.unmodifiableSet(elements);
	}

	public void clearElements() {
		elements.clear();
	}

	public void addElement(Element element) {
		elements.add(element);
	}

	@Override
	public String toString() {
		return elements.toString();
	}


}

package com.lkn.algorithm.b_tree.base;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

	public void addElements(Collection<Element> elements) {
		for (Element element : elements) {
			addElement(element);
		}
	}

	public void removeElement(Element element) {
		elements.remove(element);
	}

	public List<Node> getChildrenNode() {
		List<Node> list = Lists.newArrayList();
		for (Element element : elements) {
			Node leftNode = element.getLeftNode();
			Node rightNode = element.getRightNode();
			if (leftNode != null) {
				list.add(leftNode);
			}
			if (rightNode != null) {
				list.add(rightNode);
			}
		}
		return list;
	}

	@Override
	public String toString() {
		return elements.toString();
	}

	/**
	 * 是否是叶子节点
	 */
	public boolean isLeaf() {
		for (Element element : elements) {
			return element.getLeftNode() == null;
		}
		throw new RuntimeException("节点中的元素为null");
	}


}

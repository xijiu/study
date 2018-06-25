package com.lkn.algorithm.b_tree;

import lombok.Getter;
import lombok.Setter;

/**
 * 元素
 * @author likangning
 * @since 2018/6/22 下午12:10
 */
@Getter
@Setter
public class Element<T extends Comparable> implements Comparable<Element> {

	private T t;

	// 该元素连接的左节点；如果该元素所在的节点有孩子节点的话，那么此变量一定不为null
	private Node leftNode;

	// 该元素连接的右节点；如果该元素是排序最大的元素，那么此变量不为null，否则一定为null
	private Node rightNode;


	public Element(T t) {
		this.t = t;
	}

	@Override
	public int compareTo(Element o) {
		return this.t.compareTo(o.getT());
	}

	@Override
	public String toString() {
		return t.toString();
	}
}

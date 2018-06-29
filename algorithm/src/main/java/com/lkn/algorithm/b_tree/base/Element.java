package com.lkn.algorithm.b_tree.base;

import lombok.Getter;
import lombok.Setter;

/**
 * 元素
 * 一个元素所在位置不同，其左右节点的结构也不同，分为两种：
 * 1、当前节点中的最后一位： {@link this#leftNode} 不为空，{@link this#rightNode} 不为空
 * 2、非当前节点中的最后一位： {@link this#leftNode} 不为空，{@link this#rightNode} 一定为空
 *
 * @author likangning
 * @since 2018/6/22 下午12:10
 */
@Getter
@Setter
public class Element<T extends Comparable> implements Comparable<Element> {

	// 元素存储的实际内容，该对象一定实现了Comparable接口
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

	@Override
	public boolean equals(Object target) {
		if (target instanceof Element) {
			if (t.compareTo(((Element) target).t) == 0) {
				return true;
			}
		}
		return false;
	}
}

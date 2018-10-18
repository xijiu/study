package com.lkn.algorithm.r_b_tree;

import lombok.Getter;

/**
 * 红黑树的结构
 * @author likangning
 * @since 2018/10/17 下午5:18
 */
public class Tree<T extends Comparable> {

	@Getter
	private Node<T> root;

	/**
	 * 添加一个节点
	 * @param node	添加的节点
	 */
	public synchronized void add(Node<T> node) {
		RBTreeAdd.add(root, node);
		root = RBTreeHandler.findRoot(node);
	}
}

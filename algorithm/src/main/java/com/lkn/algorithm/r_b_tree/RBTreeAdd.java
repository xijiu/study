package com.lkn.algorithm.r_b_tree;

/**
 * 红黑树的添加操作
 *
 * @author likangning
 * @since 2018/7/11 上午8:20
 */
public class RBTreeAdd {

	/**
	 * 节点添加操作
	 *
	 * @param root 根节点
	 * @param newNode	新加入的节点
	 * @param <T>	节点数据的泛型
	 */
	public static <T extends Comparable> void add(Node<T> root, Node<T> newNode) {
		if (root == null) {
			newNode.toRoot();
			return;
		}
	}
}

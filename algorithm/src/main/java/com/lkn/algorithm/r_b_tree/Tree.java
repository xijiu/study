package com.lkn.algorithm.r_b_tree;

import com.google.common.base.Objects;
import com.lkn.algorithm.util.Parasite;
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
		rootRest(node);
	}

	/**
	 * 删除节点
	 * @param t	删除节点的内容
	 */
	public synchronized void delete(T t) {
		Parasite<Node<T>> parasite = new Parasite<>();
		RBTreeHandler.bfs(root, (level, currentNode) -> {
			if (Objects.equal(t, currentNode.getData())) {
				parasite.set(currentNode);
			}
		});
		Node<T> node = parasite.get();
		if (node != null) {
			RBTreeDelete.delete(node);
			if (root.getData() == null) {
				root = null;
			} else {
				rootRest(node);
			}
		}
	}

	private void rootRest(Node<T> node) {
		root = RBTreeHandler.findRoot(node);
	}
}

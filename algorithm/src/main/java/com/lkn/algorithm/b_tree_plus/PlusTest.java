package com.lkn.algorithm.b_tree_plus;

import com.lkn.algorithm.b_tree.PrintTree;
import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
import org.junit.Test;

/**
 * @author likangning
 * @since 2018/6/29 下午5:43
 */
public class PlusTest {
	// 根节点
	private Node root;

	private void reset() {
		root = null;
	}

	private void addElement(int element) {
		if (root == null) {
			root = new Node(null);
		}
		BPlusTreeAdd.add(root, new Element<>(element));
	}

	private void deleteElement(int element) {
		BPlusTreeDelete.delete(root, new Element<>(element));
	}


	@Test
	public void addTest() {
		int begin = 10;
		int end = 20;
		for (int i = begin; i < end; i++) {
			addElement(i);
		}
		PrintTree.print(root);
	}


	@Test
	public void deleteTest() {
		int begin = 10;
		int end = 100;
		for (int i = begin; i <= end; i++) {
			addElement(i);
		}
		PrintTree.print(root);
		for (int i = begin; i <= end; i++) {
			System.out.println("删除节点" + i);
			deleteElement(i);
			PrintTree.print(root);
		}

		reset();

		for (int i = begin; i <= end; i++) {
			addElement(i);
		}
		PrintTree.print(root);
		for (int i = end; i >= begin; i--) {
			System.out.println("删除节点" + i);
			deleteElement(i);
			PrintTree.print(root);
		}
	}
}

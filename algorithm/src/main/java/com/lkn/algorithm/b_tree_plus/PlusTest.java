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

	private void addElement(int element) {
		if (root == null) {
			root = new Node(null);
		}
		BPlusTreeAdd.add(root, new Element<>(element));
	}


	@Test
	public void createManyTest() {
		for (int i = 10; i < 100; i++) {
			addElement(i);
		}
		PrintTree.print(root);
	}
}

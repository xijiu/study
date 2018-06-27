package com.lkn.algorithm.b_tree;

import com.lkn.algorithm.b_tree.base.Element;
import com.lkn.algorithm.b_tree.base.Node;
import org.junit.Test;



/**
 * https://www.cnblogs.com/nullzx/p/8729425.html -> 详细讲解B树添加操作
 *
 * @author likangning
 * @since 2018/6/24 下午12:28
 */
public class BtreeTest {
	// 根节点
	private Node root;

	@Test
	public void createTree() {
		addElement(39);
		addElement(22);
		addElement(97);
		addElement(41);
		addElement(53);
		addElement(13);
		addElement(21);
		addElement(40);

		addElement(30);
		addElement(27);
		addElement(33);
		addElement(36);
		addElement(35);
		addElement(34);
		addElement(24);
		addElement(29);

		addElement(26);

		addElement(17);
		addElement(28);
		addElement(23);
		addElement(31);
		addElement(32);

		levelNodeShow();
	}

	private void levelNodeShow() {
		PrintTree.print(root);
	}

	private void addElement(int element) {
		if (root == null) {
			root = new Node(null);
			TreeAdd.add(root, new Element<>(element));
		} else {
			Tree.addElement(root, new Element<>(element));
		}
	}

	@Test
	public void createManyTest() {
		for (int i = 10; i < 100; i++) {
			addElement(i);
		}
		levelNodeShow();
	}
}

package com.lkn.algorithm.b_tree;

import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
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

	private void deleteElement(int element) {
		TreeDelete.delete(root, new Element<>(element));
	}

	@Test
	public void createManyTest() {
		for (int i = 10; i < 70; i++) {
			addElement(i);
		}
		PrintTree.print(root);
	}

	@Test
	public void deleteTest() {
		createTree();
		deleteElement(21);
		PrintTree.print(root);
		deleteElement(27);
		PrintTree.print(root);
		deleteElement(32);
		PrintTree.print(root);
		deleteElement(40);
		PrintTree.print(root);
	}

	@Test
	public void deleteManyTest() {
		int size = 100;
		for (int i = 10; i < size; i++) {
			addElement(i);
			PrintTree.print(root);
		}
		System.out.println(" add 结束 ****************************************");
		for (int i = 10; i < size; i++) {
			System.out.println("删除节点 " + i);
			deleteElement(i);
			PrintTree.print(root);
		}
	}
}

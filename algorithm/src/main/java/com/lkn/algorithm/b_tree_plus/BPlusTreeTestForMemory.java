package com.lkn.algorithm.b_tree_plus;

import com.lkn.algorithm.b_tree.BtreeTest;
import com.lkn.algorithm.b_tree.PrintTree;
import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
import org.junit.Test;

/**
 * B+树在内存中的测试
 *
 * @author likangning
 * @since 2018/6/29 下午5:43
 */
public class BPlusTreeTestForMemory extends BtreeTest {

	@Override
	protected void addElement(int element) {
		if (root == null) {
			root = new Node(null);
		}
		System.out.println("添加元素 ====> " + element);
		BPlusTreeAdd.add(root, new Element<>(element));
		PrintTree.print(root);
	}

	@Override
	protected void deleteElement(int element) {
		System.out.println("删除元素 ====> " + element);
		BPlusTreeDelete.delete(root, new Element<>(element));
		PrintTree.print(root);
	}

	@Test
	public void addTest() {
		Node.resetOrderNum(5);
		int begin = 1;
		int end = 20;
		for (int i = begin; i < end; i++) {
			addElement(i);
			PrintTree.print(root);
		}
	}

	@Test
	public void randomDeleteTest() {
		Node.resetOrderNum(5);
		int begin = 1;
		int end = 99;

		randomAddElement(begin, end);
		PrintTree.print(root);
		randomDeleteElement(begin, end);
	}

}

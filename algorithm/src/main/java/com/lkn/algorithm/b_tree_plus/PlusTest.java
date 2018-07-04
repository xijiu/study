package com.lkn.algorithm.b_tree_plus;

import com.lkn.algorithm.b_tree.BtreeTest;
import com.lkn.algorithm.b_tree.PrintTree;
import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
import org.junit.Test;

/**
 * @author likangning
 * @since 2018/6/29 下午5:43
 */
public class PlusTest extends BtreeTest {


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
		int begin = 10;
		int end = 20;
		for (int i = begin; i < end; i++) {
			addElement(i);
		}
		PrintTree.print(root);
	}



	@Test
	public void bPlusTreeDeleteRandom() {
		int begin = 1;
		int end = 99;

		randomAddElement(begin, end);
		PrintTree.print(root);
		randomDeleteElement(begin, end);
	}


}

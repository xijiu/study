package com.lkn.algorithm.r_b_tree.test;

import com.lkn.algorithm.r_b_tree.Node;
import com.lkn.algorithm.r_b_tree.PrintRedBlackTree;
import com.lkn.algorithm.r_b_tree.RBTreeHandler;
import org.junit.Before;
import org.junit.Test;

/**
 * 旋转测试
 * @author likangning
 * @since 2018/10/17 下午5:08
 */
public class RotateTest {

	private Node<Integer> root = new Node<>(10);

	@Before
	public void before() {
		Node<Integer> left = new Node<>(8);
		Node<Integer> right = new Node<>(11);
		root.setLeftChild(left);
		root.setRightChild(right);
	}

	/**
	 * 左旋测试
	 */
	@Test
	public void leftRotateTest() {
		PrintRedBlackTree.print(root);
		RBTreeHandler.leftRotate(root);
		PrintRedBlackTree.print(root);
	}

	/**
	 * 右旋测试
	 */
	@Test
	public void rightRotateTest() {
		PrintRedBlackTree.print(root);
		RBTreeHandler.rightRotate(root);
		PrintRedBlackTree.print(root);
	}
}

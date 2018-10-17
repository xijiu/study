package com.lkn.algorithm.r_b_tree;

import org.junit.Test;

/**
 * @author likangning
 * @since 2018/10/16 下午7:35
 */
public class RBTest {

	@Test
	public void test() {
		Node<Integer> node = new Node<>(10);
		Node<Integer> left = new Node<>(8);
		Node<Integer> right = new Node<>(11);

		node.setLeftChild(left);
		node.setRightChild(right);

		Node<Integer> left1 = new Node<>(7);
		left.setLeftChild(left1);

		Node<Integer> right1 = new Node<>(9);
		left.setRightChild(right1);

		Node<Integer> left2 = new Node<>(1);
		right.setLeftChild(left2);

		Node<Integer> right2 = new Node<>(12);
		right.setRightChild(right2);

		PrintRedBlackTree.print(node);
	}
}
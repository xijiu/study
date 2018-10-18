package com.lkn.algorithm.r_b_tree.test;

import com.google.common.collect.Sets;
import com.lkn.algorithm.r_b_tree.Node;
import com.lkn.algorithm.r_b_tree.PrintRedBlackTree;
import com.lkn.algorithm.r_b_tree.Tree;
import org.junit.Test;

import java.util.Set;

/**
 * @author likangning
 * @since 2018/10/17 下午5:32
 */
public class TreeTest {
	private Tree<Integer> tree = new Tree<>();

	/**
	 * 添加节点测试
	 */
	@Test
	public void addNodeTest() {
		add(41);
		add(38);
		add(31);
		PrintRedBlackTree.print(tree);
	}

	/**
	 * 添加节点测试
	 */
	@Test
	public void addNodeTest2() {
		add(12);
		add(1);
		add(9);
		add(2);
		add(0);
		add(11);
		add(7);
		add(19);
		add(4);
		add(15);
		add(18);
		add(5);
		add(14);
		add(13);
		add(10);
		add(16);
		add(6);
		add(3);
		add(8);
		add(17);
	}

	private void add(Integer data) {
		System.out.println("将要添加" + data);
		tree.add(new Node<>(data));
		PrintRedBlackTree.print(tree);
		System.out.println();
		System.out.println();
	}

	@Test
	public void test3() {
		Node<Integer> node1 = new Node<>(10);
		Node<Integer> node2 = new Node<>(8);
		Set<Node> set = Sets.newTreeSet();
		set.add(node1);
		set.add(node2);
		System.out.println(set);
	}
}

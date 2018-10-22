package com.lkn.algorithm.r_b_tree.test;

import com.lkn.algorithm.r_b_tree.Node;
import com.lkn.algorithm.r_b_tree.PrintRedBlackTree;
import com.lkn.algorithm.r_b_tree.Tree;
import org.junit.Test;

/**
 * 该测试用例参考：https://blog.csdn.net/xiaojun111111/article/details/51898486
 *
 * @author likangning
 * @since 2018/10/17 下午5:32
 */
public class TreeTest {
	private Tree<Integer> tree = new Tree<>();

	/**
	 * 添加节点测试
	 */
	@Test
	public void addTest() {
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
	public void deleteTest() {
		addTest();
		delete(12);
		delete(1);
		delete(9);
		delete(2);
		delete(0);
		delete(11);
		delete(7);
		delete(19);
		delete(4);
		delete(15);
		delete(18);
		delete(5);
		delete(14);
		delete(13);
		delete(10);
		delete(16);
		delete(6);
		delete(3);
		delete(8);
		delete(17);
	}

	private void delete(int data) {
		System.out.println("将要删除" + data);
		tree.delete(data);
		PrintRedBlackTree.print(tree);
		System.out.println();
		System.out.println();
	}
}

package com.lkn.algorithm.b_tree;

import com.google.common.collect.Lists;
import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
import com.lkn.algorithm.b_tree_plus.index_file.DefaultIndexFileOperation;
import com.lkn.algorithm.b_tree_plus.index_file.IndexFileOperation;
import org.junit.Test;

import java.util.List;
import java.util.Random;


/**
 * https://www.cnblogs.com/nullzx/p/8729425.html -> 详细讲解B树添加操作
 *
 * @author likangning
 * @since 2018/6/24 下午12:28
 */
public class BtreeTest {

	protected static IndexFileOperation indexFileOperation = DefaultIndexFileOperation.getSingleInstance();

	// 根节点
	protected Node root;

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


	protected void addElement(int element) {
		System.out.println("插入：" + element + "\r\n");
		if (root == null) {
			root = new Node(null);
			TreeAdd.add(root, new Element<>(element));
		} else {
			Tree.addElement(root, new Element<>(element));
		}
		PrintTree.print(root);
	}

	protected void deleteElement(int element) {
		System.out.println("删除 " + element + "\r\n");
		TreeDelete.delete(root, new Element<>(element));
	}

	@Test
	public void createManyTest() {
		for (int i = 1; i < 70; i++) {
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

	protected void reset() {
		root = null;
	}

	@Test
	public void deleteManyTest() {
		int begin = 10;
		int end = 100;
		addBatchElement(begin, end);
		for (int i = 10; i < end; i++) {
			System.out.println("删除节点 " + i);
			deleteElement(i);
			PrintTree.print(root);
		}

		reset();

		addBatchElement(begin, end);
		for (int i = end; i >= 10; i--) {
			System.out.println("删除节点 " + i);
			deleteElement(i);
			PrintTree.print(root);
		}
	}

	private void addBatchElement(int begin, int end) {
		for (int i = begin; i <= end; i++) {
			addElement(i);
		}
	}

	@Test
	public void addRandom() {
		int begin = 1;
		int end = 100;
		randomAddElement(begin, end);
	}



	protected List<Integer> initList(int begin, int end) {
		List<Integer> list = Lists.newArrayList();
		for (int i = begin; i <= end; i++) {
			list.add(i);
		}
		return list;
	}

	@Test
	public void deleteRandomTest() {
		int begin = 1;
		int end = 100;
		randomAddElement(begin, end);
		PrintTree.print(root);
		randomDeleteElement(begin, end);
	}

	/**
	 * 随机添加元素
	 * @param begin
	 * @param end
	 */
	protected void randomAddElement(int begin, int end) {
		List<Integer> list = initList(begin, end);
		while (true) {
			Integer ele = getRandomIndex(list);
			if (ele != null) {
				addElement(ele);
			} else {
				break;
			}
		}
	}

	/**
	 * 随机删除元素
	 * @param begin
	 * @param end
	 */
	protected void randomDeleteElement(int begin, int end) {
		List<Integer> list = Lists.newArrayList();
		for (int i = begin; i <= end; i++) {
			list.add(i);
		}
		while (true) {
			Integer ele = getRandomIndex(list);
			if (ele != null) {
				deleteElement(ele);
			} else {
				break;
			}
		}
	}

	private Integer getRandomIndex(List<Integer> list) {
		if (list.size() == 0) {
			return null;
		}
		Random random = new Random();
		int index = random.nextInt(list.size());
		return list.remove(index);
	}


}

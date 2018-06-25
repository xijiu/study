package com.lkn.algorithm.b_tree;

import com.google.common.collect.*;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;


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

		printTree();

		addElement(26);

		addElement(17);
		addElement(28);
		addElement(23);
		addElement(31);
		addElement(32);

		printTree();
		levelNodeShow();
	}

	private void printTree() {
		Tree.bfs(root, (node, level) -> System.out.println(node.getElements()));
		System.out.println("==================================");
	}

	private void levelNodeShow() {
		Multimap<Integer, Node> map = HashMultimap.create();
		Tree.bfs(root, (node, level) -> map.put(level, node));
		Set<Integer> keys = Sets.newTreeSet(map.keys());
		for (Integer level : keys) {
			Collection<Node> nodes = map.get(level);
			System.out.println(nodes);
		}
	}

	private void addElement(int element) {
		if (root == null) {
			root = new Node(null);
			root.add(new Element<>(element));
		} else {
			Tree.addElement(root, new Element<>(element));
		}
	}
}

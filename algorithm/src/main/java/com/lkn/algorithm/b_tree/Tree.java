package com.lkn.algorithm.b_tree;

import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @author likangning
 * @since 2018/6/24 下午12:16
 */
public class Tree {

	/**
	 * 对当前树进行广度优先遍历
	 * @param rootNode	根节点
	 */
	public static void bfs(Node rootNode, NodeVisitor nodeVisitor) {
		Queue<MyNode> queue = new LinkedList<>();
		queue.add(new MyNode(rootNode, 0));
		bfs(queue, nodeVisitor);
	}

	private static void bfs(Queue<MyNode> queue, NodeVisitor nodeVisitor) {
		MyNode myNode = queue.poll();
		if (myNode != null) {
			nodeVisitor.visitor(myNode.node, myNode.level);
			Set<Element> elements = myNode.node.getElements();
			for (Element element : elements) {
				Node leftNode = element.getLeftNode();
				Node rightNode = element.getRightNode();
				if (leftNode != null) {
					queue.add(new MyNode(leftNode, myNode.level + 1));
				}
				if (rightNode != null) {
					queue.add(new MyNode(rightNode, myNode.level + 1));
				}
			}
			bfs(queue, nodeVisitor);
		}
	}

	@FunctionalInterface
	public interface NodeVisitor {
		void visitor(Node node, int level);
	}

	@Data
	@AllArgsConstructor
	private static class MyNode {
		private Node node;
		private int level;
	}

	/**
	 * 添加元素对外暴露方法
	 * @param root	根节点
	 * @param newElement	新元素对象
	 */
	public static <T extends Comparable> void addElement(Node root, Element<T> newElement) {
		Node targetNode = findNodeForNewElement(root, newElement);
		Set<Element> elements = targetNode.getElements();
		if (!elements.contains(newElement)) {
			TreeAdd.add(targetNode, newElement);
		}
	}

	/**
	 * 找到新元素隶属的节点
	 * @param node
	 * @param newElement
	 * @param <T>
	 * @return
	 */
	private static <T extends Comparable> Node findNodeForNewElement(Node node, Element<T> newElement) {
		Set<Element> elements = node.getElements();
		if (elements.contains(newElement)) {
			return node;
		} else {
			Element lastElement = null;
			for (Element lookup : elements) {
				lastElement = lookup;
				if (newElement.compareTo(lookup) < 0) {
					Node leftNode = lookup.getLeftNode();
					if (leftNode != null) {
						return findNodeForNewElement(leftNode, newElement);
					} else {
						return node;
					}
				}
			}
			Node rightNode = lastElement.getRightNode();
			if (rightNode != null) {
				return findNodeForNewElement(rightNode, newElement);
			} else {
				return node;
			}
		}
	}

}

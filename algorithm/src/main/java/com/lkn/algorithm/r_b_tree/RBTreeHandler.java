package com.lkn.algorithm.r_b_tree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 主要控制树的左旋及右旋
 *
 * @author likangning
 * @since 2018/7/11 上午8:36
 */
public class RBTreeHandler {

	/**
	 * 节点左旋
	 *
	 * @param targetNode	目标节点
	 */
	public static <T extends Comparable> void leftRotate(Node<T> targetNode) {
		Node<T> rightChild = targetNode.getRightChild();
		// 如果目标节点的右孩子节点为null，将无法发生左旋
		if (rightChild == null) {
			return ;
		}
		Node<T> parent = targetNode.getParent();
		Node.Position position = targetNode.inParentPosition();
		// 1、将目标节点从母体剥离
		targetNode = breakAwayFromTree(targetNode);
		// 2、将目标节点的右子节点从母体剥离
		rightChild = breakAwayFromTree(rightChild);
		// 3、将目标节点的右子节点的左节点从母体剥离
		Node<T> childLeftNode = breakAwayFromTree(rightChild.getLeftChild());
		// 4、重置目标节点的右子节点的左节点的位置
		targetNode.setRightChild(childLeftNode);
		// 5、重置目标节点的位置
		rightChild.setLeftChild(targetNode);
		// 6、重置右子节点的位置
		if (parent != null) {
			addChildNode(parent, rightChild, position);
		} else {
			rightChild.toRoot();
		}
	}


	/**
	 * 节点右旋
	 *
	 * @param targetNode	目标节点
	 */
	public static <T extends Comparable> void rightRotate(Node<T> targetNode) {
		Node<T> leftChild = targetNode.getLeftChild();
		// 如果目标节点的右孩子节点为null，将无法发生左旋
		if (leftChild == null) {
			return ;
		}
		Node<T> parent = targetNode.getParent();
		Node.Position position = targetNode.inParentPosition();
		// 1、将目标节点从母体剥离
		targetNode = breakAwayFromTree(targetNode);
		// 2、将目标节点的左子节点从母体剥离
		leftChild = breakAwayFromTree(leftChild);
		// 3、将目标节点的右子节点的左节点从母体剥离
		Node<T> childRightNode = breakAwayFromTree(leftChild.getRightChild());
		// 4、重置目标节点的右子节点的左节点的位置
		targetNode.setLeftChild(childRightNode);
		// 5、重置目标节点的位置
		leftChild.setRightChild(targetNode);
		// 6、重置右子节点的位置
		if (parent != null) {
			addChildNode(parent, leftChild, position);
		} else {
			leftChild.toRoot();
		}
	}

	/**
	 * 为目标节点添加子节点
	 * @param node	目标节点
	 * @param child	目标节点的子节点
	 * @param position	应该添加的位置信息
	 * @param <T>	泛型
	 */
	private static <T extends Comparable> void addChildNode(Node<T> node, Node<T> child, Node.Position position) {
		if (node != null && position != null) {
			if (position == Node.Position.LEFT) {
				node.setLeftChild(child);
			} else {
				node.setRightChild(child);
			}
		}
	}

	/**
	 * 目标节点从树上脱离出来
	 * @param targetNode	目标节点
	 * @param <T>	泛型
	 */
	private static <T extends Comparable> Node<T> breakAwayFromTree(Node<T> targetNode) {
		if (targetNode != null && targetNode.getParent() != null) {
			Node<T> parent = targetNode.getParent();
			Node.Position position = targetNode.inParentPosition();
			if (position == Node.Position.LEFT) {
				parent.setLeftChild(null);
			} else if (position == Node.Position.RIGHT) {
				parent.setRightChild(null);
			}
		}
		return targetNode;
	}

	/**
	 * 寻找根节点
	 *
	 * @param node	当前节点
	 * @param <T>	泛型
	 * @return	根节点
	 */
	public static <T extends Comparable> Node<T> findRoot(Node<T> node) {
		if (node.isRoot()) {
			return node;
		} else {
			return findRoot(node.getParent());
		}
	}

	/**
	 * 广度优先遍历
	 * @param node 根节点
	 * @param template 遍历模板
	 * @param <T>	泛型
	 */
	public static <T extends Comparable> void bfs(Node<T> node, Template template) {
		Node<T> root = findRoot(node);
		Queue<Node> queue = new LinkedList<>();
		queue.add(root);
		doBfs(queue, template);
	}

	private static void doBfs(Queue<Node> queue, Template template) {
		Node node = queue.poll();
		if (node != null) {
			template.traverse(findLevel(node), node);
			Node leftChild = node.getLeftChild();
			Node rightChild = node.getRightChild();
			queue.add(leftChild);
			queue.add(rightChild);
			doBfs(queue, template);
		}
	}

	private static int findLevel(Node node) {
		int level = 1;
		while (!node.isRoot()) {
			level++;
			node = node.getParent();
		}
		return level;
	}

	@FunctionalInterface
	public interface Template {
		/**
		 * 节点的遍历
		 *
		 * @param level	节点的层级
		 * @param currentNode	当前遍历节点
		 */
		void traverse(int level, Node currentNode);
	}
}

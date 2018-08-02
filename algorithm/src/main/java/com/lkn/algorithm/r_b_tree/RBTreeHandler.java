package com.lkn.algorithm.r_b_tree;

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
		// 如果目标节点的右孩子节点为null，将无法发生左旋
		if (targetNode.getRightChild() == null) {
			return ;
		}
		Node<T> rightChild = targetNode.getRightChild();
		// 首先替换目标节点的位置
		nodeReplace(targetNode, rightChild);
		// 设置当前节点的右子节点
		targetNode.setRightChild(rightChild.getLeftChild());
		// 设置右孩子节点的左子节点
		rightChild.setLeftChild(targetNode);
	}

	/**
	 * 节点右旋
	 *
	 * @param targetNode	目标节点
	 */
	public static <T extends Comparable> void rightRotate(Node<T> targetNode) {
		// 如果目标节点的右孩子节点为null，将无法发生左旋
		if (targetNode.getLeftChild() == null) {
			return ;
		}
		Node<T> leftChild = targetNode.getLeftChild();
		// 首先替换目标节点的位置
		nodeReplace(targetNode, leftChild);
		// 设置当前节点的左子节点
		targetNode.setLeftChild(leftChild.getRightChild());
		// 设置左孩子节点的有子节点
		leftChild.setRightChild(targetNode);
	}

	/**
	 * 节点替换
	 *
	 * @param oldNode	将要被替换的旧节点
	 * @param newNode	新节点
	 */
	private static <T extends Comparable> void nodeReplace(Node<T> oldNode, Node<T> newNode) {
		if (oldNode != null && newNode != null) {
			Node<T> parent = oldNode.getParent();
			if (parent != null) {
				// 1、将目标节点的父节点的孩子节点设置为目标节点的右节点
				Node.Position position = oldNode.inParentPosition();
				if (position == Node.Position.LEFT) {
					parent.setLeftChild(newNode);
				} else {
					parent.setRightChild(newNode);
				}
			} else {
				// 加冕为王
				newNode.toRoot();
			}
		}
	}
}

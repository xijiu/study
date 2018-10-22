package com.lkn.algorithm.r_b_tree;

/**
 * 红黑树的添加操作
 *
 * 具体思路参见： https://blog.csdn.net/lj6020382/article/details/82195141
 *
 * @author likangning
 * @since 2018/7/11 上午8:20
 */
public class RBTreeAdd {

	/**
	 * 节点添加操作
	 *
	 * @param root 根节点
	 * @param newNode	新加入的节点
	 * @param <T>	节点数据的泛型
	 */
	public static <T extends Comparable> void add(Node<T> root, Node<T> newNode) {
		if (newNode == null) {
			return;
		}
		newNode.setNodeType(Node.NodeType.RED);
		if (root == null) {
			newNode.toRoot();
			newNode.setNodeType(Node.NodeType.BLACK);
			return;
		}
		doAdd(root, newNode);
	}

	/**
	 * 节点添加操作
	 * @param compareNode	与将要进行添加的节点进行比较的节点
	 * @param currNode	将要添加的节点
	 */
	private static <T extends Comparable> void doAdd(Node<T> compareNode, Node<T> currNode) {
		directAdd(compareNode, currNode);
		// 调整树结构
		adjustTree(currNode);
	}

	/**
	 * 调整树结构
	 * @param currNode
	 * @param <T>
	 */
	private static <T extends Comparable> void adjustTree(Node<T> currNode) {
		if (currNode.isRoot()) {
			currNode.setNodeType(Node.NodeType.BLACK);
			return;
		}
		Node<T> parent = currNode.getParent();
		// 如果父节点是根节点，或者父节点为黑色，那么满足红黑树的5个特性，故不需要调整
		if (parent.isRoot() || parent.getNodeType() == Node.NodeType.BLACK) {
			return;
		}
		Node.NodeType uncleNodeType = currNode.getUncleNodeType();
		if (parent.inParentPosition() == Node.Position.LEFT) {
			if (uncleNodeType == Node.NodeType.BLACK) {
				adjustLeftCurrentAndUncleBlack(currNode);
			} else if (uncleNodeType == Node.NodeType.RED) {
				adjustUncleRed(currNode);
			}
		} else if (parent.inParentPosition() == Node.Position.RIGHT) {
			if (uncleNodeType == Node.NodeType.BLACK) {
				adjustRightCurrentAndUncleBlack(currNode);
			} else if (uncleNodeType == Node.NodeType.RED) {
				adjustUncleRed(currNode);
			}
		}
	}

	/**
	 * 调整叔叔节点为红色的情况
	 * @param currNode 当前要插入的节点
	 */
	private static <T extends Comparable> void adjustUncleRed(Node<T> currNode) {
		Node<T> parent = currNode.getParent();
		Node<T> grandfather = parent.getParent();
		Node<T> uncle = currNode.getUncle();
		parent.setNodeType(Node.NodeType.BLACK);
		uncle.setNodeType(Node.NodeType.BLACK);
		grandfather.setNodeType(Node.NodeType.RED);
		adjustTree(grandfather);
	}

	/**
	 * 叔叔节点为黑色：要插入的节点在右侧
	 * @param currNode 当前节点
	 */
	private static <T extends Comparable> void adjustRightCurrentAndUncleBlack(Node<T> currNode) {
		Node<T> parent = currNode.getParent();
		if (currNode.inParentPosition() == Node.Position.LEFT) {
			// 将父节点右旋，变为要插入的节点在右侧的情况，然后重新进行调整
			RBTreeHandler.rightRotate(parent);
			adjustTree(parent);
		} else if (currNode.inParentPosition() == Node.Position.RIGHT) {
			// 要插入的节点是父节点的右孩子时
			Node<T> grandfather = parent.getParent();
			// 1、将祖父节点进行左旋
			RBTreeHandler.leftRotate(grandfather);
			// 2、将父节点与祖父节点的颜色进行互换
			changeNodeType(parent, grandfather);
		}
	}

	/**
	 * 叔叔节点为黑色：要插入节点的父节点在左侧
	 * @param currNode	当前要插入的节点
	 */
	private static <T extends Comparable> void adjustLeftCurrentAndUncleBlack(Node<T> currNode) {
		Node<T> parent = currNode.getParent();
		if (currNode.inParentPosition() == Node.Position.LEFT) {
			// 要插入的节点是父节点的左孩子时
			Node<T> grandfather = parent.getParent();
			// 1、将祖父节点进行右旋
			RBTreeHandler.rightRotate(grandfather);
			// 2、将父节点与祖父节点的颜色进行互换
			changeNodeType(parent, grandfather);
		} else if (currNode.inParentPosition() == Node.Position.RIGHT) {
			// 将父节点左旋，变为要插入的节点在左侧的情况，然后重新进行调整
			RBTreeHandler.leftRotate(parent);
			adjustTree(parent);
		}
	}

	/**
	 * 将节点的颜色互换
	 * @param node1	节点1
	 * @param node2	节点2
	 */
	private static <T extends Comparable> void changeNodeType(Node<T> node1, Node<T> node2) {
		if (node1 != null && node2 != null) {
			Node.NodeType node1Type = node1.getNodeType();
			node1.setNodeType(node2.getNodeType());
			node2.setNodeType(node1Type);
		}
	}

	/**
	 * 直接添加
	 */
	private static <T extends Comparable> void directAdd(Node<T> compareNode, Node<T> currNode) {
		if (compareNode.compareTo(currNode) > 0) {
			Node<T> leftChild = compareNode.getLeftChild();
			if (leftChild != null) {
				directAdd(leftChild, currNode);
			} else {
				compareNode.setLeftChild(currNode);
			}
		} else if (compareNode.compareTo(currNode) < 0) {
			Node<T> rightChild = compareNode.getRightChild();
			if (rightChild != null) {
				directAdd(rightChild, currNode);
			} else {
				compareNode.setRightChild(currNode);
			}
		} else {
			// do nothing
			System.out.println("相同节点不做覆盖操作");
		}
	}
}

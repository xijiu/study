package com.lkn.algorithm.r_b_tree;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * 树中的节点
 *
 * @author likangning
 * @since 2018/7/11 上午8:11
 */
public class Node<T extends Comparable> implements Comparable<Node<T>> {

	/**
	 * 节点中存放的内容
	 */
	@Getter
	@Setter
	private T data;

	/**
	 * 当前节点的左孩子节点
	 */
	@Getter
	private Node<T> leftChild;

	/**
	 * 当前节点右孩子节点
	 */
	@Getter
	private Node<T> rightChild;

	/**
	 * 当前节点的父节点
	 * 注：根节点的父节点为null
	 */
	@Getter
	private Node<T> parent;

	/**
	 * 红黑节点的标识
	 */
	@Getter
	@Setter
	private NodeType nodeType;

	public Node(T data) {
		setData(data);
		parent = null;
	}


	public void setLeftChild(Node<T> leftChild) {
		this.leftChild = leftChild;
		if (leftChild != null) {
			leftChild.parent = this;
		}
	}


	public void setRightChild(Node<T> rightChild) {
		this.rightChild = rightChild;
		if (rightChild != null) {
			rightChild.parent = this;
		}
	}

	/**
	 * 当前节点是否是根节点
	 * @return	是否为根节点
	 */
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * 将当前节点升级为根节点
	 */
	public void toRoot() {
		this.parent = null;
	}

	/**
	 * 在父节点中的哪个位置
	 *
	 * @return	左或右
	 */
	public Position inParentPosition() {
		if (isRoot()) {
			return null;
		} else {
			if (Objects.equal(parent.getLeftChild(), this)) {
				return Position.LEFT;
			} else {
				return Position.RIGHT;
			}
		}
	}

	/**
	 * 获取叔叔节点的类型
	 * @return	叔叔节点类型
	 */
	public Node<T> getUncle() {
		Node<T> uncle;
		Node<T> parent = getParent();
		Node<T> grandfather = parent.getParent();
		if (parent.inParentPosition() == Position.LEFT) {
			uncle = grandfather.getRightChild();
		} else {
			uncle = grandfather.getLeftChild();
		}
		return uncle;
	}

	/**
	 * 获取叔叔节点的类型
	 * @return	叔叔节点类型
	 */
	public NodeType getUncleNodeType() {
		Node<T> uncle = getUncle();
		// 如果叔叔节点为null，那么叔叔节点对应的颜色为黑色
		return uncle == null ? NodeType.BLACK : uncle.getNodeType();
	}

	@Override
	public int compareTo(Node<T> o) {
		return data.compareTo(o.getData());
	}

	/**
	 * 节点类型
	 */
	public enum NodeType {
		RED,
		BLACK
	}

	/**
	 * 节点类型
	 */
	public enum Position {
		LEFT,
		RIGHT
	}

	@Override
	public String toString() {
		return "Node{" +
				"data=" + data +
				", nodeType=" + nodeType +
				'}';
	}
}

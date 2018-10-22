package com.lkn.algorithm.jmc;

public class Node {
	/**
	 * 左子树
	 */
	private Node left;
	/**
	 * 右子树
	 */
	private Node right;
	/**
	 * 值
	 */
	private String value;

	/**
	 * 树高
	 */
	private Integer hight;

	/**
	 * x坐标
	 */
	private Integer x;

	/**
	 * y坐标
	 */
	private Integer y;

	/**
	 * 父节点x
	 */
	private Integer fatherX;

	/**
	 * 父节点y
	 */
	private Integer fatherY;

	/**
	 * 是父节点的左子树
	 */
	private boolean isLeftOfFather;

	/**
	 * 是父节点的右子树
	 */
	private boolean isRightOfFather;

	/**
	 * 间隔（子节点减半）
	 */
	private Integer interval;

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getHight() {
		return hight;
	}

	public void setHight(Integer hight) {
		this.hight = hight;
	}

	public Integer getFatherX() {
		return fatherX;
	}

	public void setFatherX(Integer fatherX) {
		this.fatherX = fatherX;
	}

	public Integer getFatherY() {
		return fatherY;
	}

	public void setFatherY(Integer fatherY) {
		this.fatherY = fatherY;
	}

	public boolean isLeftOfFather() {
		return isLeftOfFather;
	}

	public void setLeftOfFather(boolean leftOfFather) {
		isLeftOfFather = leftOfFather;
	}

	public boolean isRightOfFather() {
		return isRightOfFather;
	}

	public void setRightOfFather(boolean rightOfFather) {
		isRightOfFather = rightOfFather;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	/**
	 * 获取子节点个数
	 */
	public int getLeftSize() {
		int leftSize = 0;
		Node temp = this;
		while (temp != null) {
			Node left = temp.getLeft();
			if (left != null) {
				leftSize++;
			}
			temp = left;
		}
		return leftSize;
	}


	/**
	 * 获取子节点个数
	 */
	public int getRightSize() {
		int rightSize = 0;
		Node temp = this;
		while (temp != null) {
			Node right = temp.getRight();
			if (right != null) {
				rightSize++;
			}
			temp = right;
		}
		return rightSize;
	}
}

package com.lkn.algorithm.jmc;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestTree {

	/**
	 * 需要五个填充
	 */

	private static int padding = 6;

	/**
	 * 找到屏幕的中心
	 */
	private static int center = 80;


	public static void main(String[] args) {
		//创建节点关系
		Node n1 = createNode();
		//设置xy坐标
		setXY(n1);
		//创建打印结构 key：为y坐标，value：为所在行所有节点
		Map<Integer, List<Node>> map = new HashMap<>();
		createMap(n1, map);
		print(map);
	}

	private static Node createNode() {
		Node n1 = new Node();
		n1.setHight(1);
		n1.setValue("A");
		n1.setInterval(40);

		Node n2 = new Node();
		n2.setValue("B");

		Node n3 = new Node();
		n3.setValue("C");

		n1.setLeft(n2);
		n1.setRight(n3);

		Node n4 = new Node();
		n4.setValue("D");

		Node n5 = new Node();
		n5.setValue("E");

		n2.setLeft(n4);
		n2.setRight(n5);

		Node n6 = new Node();
		n6.setValue("F");

		Node n7 = new Node();
		n7.setValue("G");

		n3.setLeft(n6);
		n3.setRight(n7);

		Node n8 = new Node();
		n8.setValue("H");

		Node n9 = new Node();
		n9.setValue("I");

		n7.setLeft(n8);
		n7.setRight(n9);
		return n1;
	}

	private static void createMap(Node n, Map<Integer, List<Node>> map) {
		if (n != null) {
			List<Node> nodes = map.computeIfAbsent(n.getY(), k -> Lists.newArrayList());
			nodes.add(n);
			if (n.getLeft() != null) {
				//创建带"-" 的几个节点
				for (int i = 1; i < 6; i++) {
					List<Node> nodes2 = map.computeIfAbsent(n.getY() + i, k -> Lists.newArrayList());
					Node tempNode = new Node();
					tempNode.setX(n.getX() - (n.getLeft().getInterval() / 5 * i));
					tempNode.setY(n.getY());
					tempNode.setValue("-");
					nodes2.add(tempNode);
				}
				createMap(n.getLeft(), map);
			}
			if (n.getRight() != null) {
				//创建带"-" 的几个节点
				for (int i = 1; i < 6; i++) {
					List<Node> nodes2 = map.computeIfAbsent(n.getY() + i, k -> Lists.newArrayList());
					Node tempNode = new Node();
					tempNode.setX(n.getX() + (n.getLeft().getInterval() / 5 * i));
					tempNode.setY(n.getY());
					tempNode.setValue("-");
					nodes2.add(tempNode);
				}
				createMap(n.getRight(), map);
			}
		}
	}

	private static void setXY(Node n) {
		if (n != null) {
			setX(n);
			setY(n);
			Node left = n.getLeft();
			if (left != null) {
				left.setHight(n.getHight() + 1);
				left.setLeftOfFather(true);
				left.setFatherX(n.getX());
				left.setFatherY(n.getY());
				left.setInterval(n.getInterval() / 2);
				setXY(left);
			}

			Node right = n.getRight();
			if (right != null) {
				right.setHight(n.getHight() + 1);
				right.setRightOfFather(true);
				right.setFatherX(n.getX());
				right.setFatherY(n.getY());
				right.setInterval(n.getInterval() / 2);
				setXY(right);
			}
		}
	}

	private static void setY(Node n) {
		if (n.getHight() == 1) {
			n.setY(0);
		}
		if (n.isLeftOfFather()) {
			n.setY(n.getFatherY() + (padding));
		}
		if (n.isRightOfFather()) {
			n.setY(n.getFatherY() + (padding));
		}
	}

	private static void setX(Node n) {
		if (n.getHight() == 1) {
			int leftSize = n.getLeftSize();
			int rightSize = n.getRightSize();
			n.setX((rightSize - leftSize) + center);
		}
		if (n.isLeftOfFather()) {
			n.setX(n.getFatherX() - (n.getInterval()));
		}
		if (n.isRightOfFather()) {
			n.setX(n.getFatherX() + (n.getInterval()));
		}
	}


	private static void print(Map<Integer, List<Node>> map) {
		for (int y = 0; y < 30; y++) {
			List<Node> list = map.get(y);
			//key：x坐标，value：是值
			Map<Integer, String> xValueMap = Optional.ofNullable(list).orElse(Lists.newArrayList()).stream().collect(Collectors.toMap(Node::getX, Node::getValue));
			for (int x = 0; x < 200; x++) {
				if (xValueMap.get(x) != null) {
					System.out.print(xValueMap.get(x));
				} else {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
	}
}

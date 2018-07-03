package com.lkn.algorithm.b_tree_plus;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.lkn.algorithm.b_tree.Tree;
import com.lkn.algorithm.b_tree.TreeAdd;
import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
import com.lkn.algorithm.util.Parasite;
import lombok.Builder;

import java.util.Set;

/**
 * B树添加操作
 *
 * @author likangning
 * @since 2018/6/27 上午9:56
 */
public class BPlusTreeAdd {

	/**
	 * 添加一个新元素
	 * @param root 根节点
	 * @param element 新节点
	 */
	public static void add(Node root, Element element) {
		Node leafNode = findPrepareAddNode(root, element);
		// 如果发生了重复元素，那么直接返回
		if (leafNode.getElements().contains(element)) {
			return;
		}
		addNewElement(leafNode, element);
	}

	/**
	 * 添加新元素
	 * @param leafNode	将要添加进入的叶子节点
	 * @param newElement	新元素
	 */
	private static void addNewElement(Node leafNode, Element newElement) {
		leafNode.addElement(newElement);
		adjust(leafNode);
	}

	/**
	 * 添加完元素后进行调整
	 * 如果当前节点元素的数量已经超过阈值，那么进行分裂操作
	 * @param node	需要调整的节点
	 */
	private static void adjust(Node node) {
		// 如果已经超过最大的元素数量
		if (node.getElements().size() >= Node.ORDER_NUM) {
			// 将节点进行切割
			SplitNode splitNode = splitNode(node);
			if (node.isLeaf()) {
				// 叶子节点发生调整
				leafAdjust(node, splitNode);
			} else {
				// 内部节点调整，逻辑与B树一致
				TreeAdd.adjust(node);
			}
		}
	}

	/**
	 * 叶子节点的调整
	 * @param node	当前叶子节点
	 * @param splitNode	已经分裂完毕的对象
	 */
	private static void leafAdjust(Node node, SplitNode splitNode) {
		Node parent = node.getParent();
		Element middleElement = splitNode.middleElement;
		if (parent != null) {
			Element[] elementArr = getFatherElementArr(node);
			Node leftNode = new Node(parent);
			leftNode.addElements(splitNode.leftElements);

			Node rightNode = new Node(parent);
			rightNode.addElements(splitNode.rightElements);
			rightNode.removeElement(middleElement);
			rightNode.addElement(new Element(middleElement.getT()));

			leftNode.setNextLeafNode(rightNode);
			parent.addElement(middleElement);
			adjustFatherElement(elementArr, leftNode, rightNode, middleElement, node);
			adjust(parent);
		} else {
			Node leftNode = new Node(node);
			leftNode.addElements(splitNode.leftElements);
			middleElement.setLeftNode(leftNode);

			Node rightNode = new Node(node);
			rightNode.addElements(splitNode.rightElements);
			rightNode.removeElement(middleElement);
			rightNode.addElement(new Element(middleElement.getT()));
			middleElement.setRightNode(rightNode);

			leftNode.setNextLeafNode(rightNode);
			node.clearElements();
			node.addElement(middleElement);
		}
	}

	private static void adjustFatherElement(Element[] elementArr, Node leftNode, Node rightNode, Element middleElement, Node targetNode) {
		Element fatherLeftElement = elementArr[0];
		Element targetElement = elementArr[1];
		Element fatherRightElement = elementArr[2];
		if (fatherRightElement != null) {
			targetElement.setLeftNode(leftNode);
			targetElement.setRightNode(null);
			middleElement.setLeftNode(rightNode);
			middleElement.setRightNode(null);
		} else {
			if (Objects.equal(targetElement.getLeftNode(), targetNode)) {
				middleElement.setLeftNode(rightNode);
				middleElement.setRightNode(targetElement.getRightNode());
				targetElement.setLeftNode(leftNode);
				targetElement.setRightNode(null);
			} else if (Objects.equal(targetElement.getRightNode(), targetNode)) {
				middleElement.setLeftNode(leftNode);
				middleElement.setRightNode(rightNode);
				targetElement.setRightNode(null);
			}
		}
	}

	private static Element[] getFatherElementArr(Node node) {
		Element[] elementArr = new Element[3];
		Element fatherLeftElement = null;
		boolean mark = false;
		for (Element lookup : node.getParent().getElements()) {
			if (Objects.equal(lookup.getLeftNode(), node) || Objects.equal(lookup.getRightNode(), node)) {
				elementArr[1] = lookup;
				elementArr[0] = fatherLeftElement;
				mark = true;
			} else if (mark) {
				elementArr[2] = lookup;
				break;
			}
			fatherLeftElement = lookup;
		}

		return elementArr;
	}


	/**
	 * 节点进行切割
	 * @param node	将要被切割的节点
	 * @return	节点分裂后的对象
	 */
	private static SplitNode splitNode(Node node) {
		Set<Element> elements = node.getElements();
		Set<Element> leftElement = Sets.newTreeSet();
		Set<Element> rightElement = Sets.newTreeSet();
		Element midElement = findMiddleElement(elements);
		for (Element lookup : elements) {
			if (lookup.compareTo(midElement) < 0) {
				leftElement.add(lookup);
			} else if (lookup.compareTo(midElement) > 0) {
				rightElement.add(lookup);
			}
		}
		if (node.isLeaf()) {
			rightElement.add(midElement);
		}
		return SplitNode.builder()
				.leftElements(leftElement)
				.rightElements(rightElement)
				.middleElement(midElement)
				.leaf(node.isLeaf())
				.build();
	}

	private static Element findMiddleElement(Set<Element> elements) {
		Element mid = null;
		int index = 0;
		for (Element lookup : elements) {
			if (index == Node.MIDDLE_INDEX) {
				mid = lookup;
			}
			index++;
		}
		return mid;
	}

	/**
	 * 找到元素element隶属的叶子节点
	 * @param node	根节点
	 * @param newElement	新加元素
	 * @return	该新元素的归属节点
	 */
	private static Node findPrepareAddNode(Node node, Element newElement) {
		Parasite<Node> parasite = new Parasite<>();
		Tree.bfs(node, (currNode, level) -> {
			// 如果是叶子节点，并且还没有找到合适的归属节点
			if (currNode.isLeaf() && parasite.get() == null) {
				// 已经是最后一个叶子节点了
				if (currNode.getNextLeafNode() == null) {
					parasite.set(currNode);
				}
				for (Element lookup : currNode.getElements()) {
					if (newElement.compareTo(lookup) < 0) {
						parasite.set(currNode);
						break;
					}
				}
			}
		});
		return parasite.get();
	}

	/**
	 * 内部使用，方便参数传递
	 */
	@Builder
	private static class SplitNode {
		private boolean leaf;
		private Element middleElement;
		private Set<Element> leftElements;
		private Set<Element> rightElements;
	}

}

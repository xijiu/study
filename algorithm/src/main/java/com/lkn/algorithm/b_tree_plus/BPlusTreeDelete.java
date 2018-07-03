package com.lkn.algorithm.b_tree_plus;

import com.google.common.base.Objects;
import com.lkn.algorithm.b_tree.TreeDelete;
import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;

import java.util.Set;
import static com.lkn.algorithm.b_tree.TreeDelete.MIN_ELEMENT_NUM_PER_NODE;

/**
 * B+树的删除操作
 *
 * @author likangning
 * @since 2018/7/3 上午9:05
 */
public class BPlusTreeDelete {

	/**
	 * B+树的删除操作
	 * @param root	根节点
	 * @param element	要删除的元素
	 */
	public static void delete(Node root, Element element) {
		Node leafNode = findBelongNode(root, element);
		element = reDirectElement(leafNode, element);
		doDelete(leafNode, element);
	}

	/**
	 * 执行删除操作，分为2步骤：1、删除  2、调整
	 * @param leafNode  叶子节点
	 * @param deleteElement  要被删除的节点
	 */
	private static void doDelete(Node leafNode, Element deleteElement) {
		leafNode.removeElement(deleteElement);
		// 如果当前节点不是根节点，那么在执行调整操作
		if (leafNode.getParent() != null) {
			adjust(leafNode);
		}
	}

	/**
	 * 节点调整
	 * B+树的叶子节点的调整逻辑需要独立编写
	 * 内部节点直接复用B树的删除逻辑
	 *
	 * @param node  目标节点
	 */
	private static void adjust(Node node) {
		Set<Element> elements = node.getElements();
		if (elements.size() < MIN_ELEMENT_NUM_PER_NODE) {
			if (node.isLeaf()) {
				leafNodeAdjust(node);
			} else {
				TreeDelete.doAdjust(node);
			}
		}
	}

	/**
	 * 叶子节点的调整
	 * 优先在富有的兄弟节点中借元素
	 * 如果两侧的兄弟节点都很贫瘠，那么与父节点进行合并操作
	 *
	 * @param leafNode
	 */
	private static void leafNodeAdjust(Node leafNode) {
		// 如果当前节点为根节点，那么不需要做节点调整，直接返回
		if (leafNode.getParent() == null) {
			return;
		}
		NodeRelation nodeRelation = buildNodeRelation(leafNode);
		if (existRichBrotherNode(nodeRelation)) {
			// 树发生左旋或者右旋
			borrowElementFromBrother(nodeRelation);
		} else {
			// 需要与父节点中的元素合并
			nodeMerge(nodeRelation);
		}
	}

	/**
	 * 进行节点合并操作
	 * 1、有右兄弟节点的情况
	 * 		a、目标节点与兄弟节点不同源
	 * 		b、目标节点与兄弟节点同源
	 * 2、有左兄弟节点的情况
	 * 	  a、目标节点与兄弟节点不同源
	 * 		b、目标节点与兄弟节点同源
	 *
	 * @param nodeRelation	节点相关内容
	 */
	private static void nodeMerge(NodeRelation nodeRelation) {
		Node rightBrotherNode = nodeRelation.rightBrotherNode;
		Node targetNode = nodeRelation.targetNode;
		Node leftBrotherNode = nodeRelation.leftBrotherNode;
		Element leftBrotherParentElement = nodeRelation.leftBrotherParentElement;
		Element rightBrotherParentElement = nodeRelation.rightBrotherParentElement;
		Element targetParentElement = nodeRelation.targetParentElement;
		// 目标节点的父节点为根节点，且根节点只有一个元素
		if (targetNode.getParent().getParent() == null && targetNode.getParent().getElements().size() == 1) {
			mergeForRoot(nodeRelation);
		} else if (rightBrotherNode != null) {
			if (!Objects.equal(targetParentElement, rightBrotherParentElement)) {
				mergeWithRightNodeNotSameParent(targetNode, targetParentElement, rightBrotherNode);
			} else {
				mergeWithRightNodeSameParent(targetNode, targetParentElement, rightBrotherNode, leftBrotherParentElement);
			}
		} else if (leftBrotherNode != null) {
			if (!Objects.equal(targetParentElement, leftBrotherParentElement)) {
				mergeWithLeftNodeNotSameParent(targetNode, targetParentElement, leftBrotherNode, leftBrotherParentElement);
			} else {
				mergeWithLeftNodeSameParent(targetNode, targetParentElement, leftBrotherNode);
			}
		}
		adjust(targetNode.getParent());
	}

	/**
	 * 单个元素的根节点合并
	 * @param nodeRelation
	 */
	private static void mergeForRoot(NodeRelation nodeRelation) {
		Node targetNode = nodeRelation.targetNode;
		Node leftBrotherNode = nodeRelation.leftBrotherNode;
		Node rightBrotherNode = nodeRelation.rightBrotherNode;
		Node root = targetNode.getParent();
		root.clearElements();
		if (leftBrotherNode != null && leftBrotherNode.getElements().size() > 0) {
			root.addElements(leftBrotherNode.getElements());
		}
		if (rightBrotherNode != null && rightBrotherNode.getElements().size() > 0) {
			root.addElements(rightBrotherNode.getElements());
		}
		if (targetNode.getElements().size() > 0) {
			root.addElements(targetNode.getElements());
		}
	}

	private static void mergeWithLeftNodeSameParent(Node targetNode, Element targetParentElement, Node leftBrotherNode) {
		Node parent = targetNode.getParent();
		Element beforeCurrentElement = null;
		for (Element element : parent.getElements()) {
			if (Objects.equal(element, targetParentElement)) {
				break;
			}
			beforeCurrentElement = element;
		}
		parent.removeElement(targetParentElement);
		targetNode.addElements(leftBrotherNode.getElements());
		beforeCurrentElement.setRightNode(targetNode);

		// 维护叶子节点中的 nextLeafNode 变量
		for (Node child : parent.getChildrenNode()) {
			if (Objects.equal(child.getNextLeafNode(), leftBrotherNode)) {
				child.setNextLeafNode(targetNode);
				break;
			}
		}

	}

	private static void mergeWithLeftNodeNotSameParent(Node targetNode, Element targetParentElement,
																										 Node leftBrotherNode, Element leftBrotherParentElement) {
		Node parent = targetNode.getParent();
		parent.removeElement(leftBrotherParentElement);
		leftBrotherNode.addElements(targetNode.getElements());
		targetParentElement.setLeftNode(leftBrotherNode);

		// 维护叶子节点中的 nextLeafNode 变量
		leftBrotherNode.setNextLeafNode(targetNode.getNextLeafNode());
	}


	private static void mergeWithRightNodeSameParent(Node targetNode, Element targetParentElement,
																									 Node rightBrotherNode, Element leftBrotherParentElement) {
		Node parent = targetNode.getParent();
		parent.removeElement(targetParentElement);
		targetNode.addElements(rightBrotherNode.getElements());
		leftBrotherParentElement.setRightNode(targetNode);
		// 维护叶子节点中的 nextLeafNode 变量
		targetNode.setNextLeafNode(null);
	}


	private static void mergeWithRightNodeNotSameParent(Node targetNode, Element targetParentElement,
																											Node rightBrotherNode) {
		Node parent = targetNode.getParent();
		parent.removeElement(targetParentElement);
		rightBrotherNode.addElements(targetNode.getElements());

		// 维护叶子节点中的 nextLeafNode 变量
		for (Node child : parent.getChildrenNode()) {
			if (Objects.equal(child.getNextLeafNode(), targetNode)) {
				child.setNextLeafNode(rightBrotherNode);
				break;
			}
		}
	}

	/**
	 * 分4种情况：
	 * 1、从目标节点的右兄弟借元素，且右兄弟的父元素与目标节点的父元素不同
	 * 2、从目标节点的右兄弟借元素，且右兄弟的父元素与目标节点的父元素相同
	 * 3、从目标节点的左兄弟借元素，且左兄弟的父元素与目标节点的父元素不同
	 * 4、从目标节点的左兄弟借元素，且左兄弟的父元素与目标节点的父元素相同
	 *
	 * @param nodeRelation	相关节点对象
	 */
	private static void borrowElementFromBrother(NodeRelation nodeRelation) {
		Node rightBrotherNode = nodeRelation.rightBrotherNode;
		Node targetNode = nodeRelation.targetNode;
		Node leftBrotherNode = nodeRelation.leftBrotherNode;
		Element leftBrotherParentElement = nodeRelation.leftBrotherParentElement;
		Element rightBrotherParentElement = nodeRelation.rightBrotherParentElement;
		Element targetParentElement = nodeRelation.targetParentElement;
		// 从右兄弟节点借元素
		if (rightBrotherNode != null && rightBrotherNode.getElements().size() > MIN_ELEMENT_NUM_PER_NODE) {
			borrowFromRightBrother(targetNode, targetParentElement, rightBrotherNode);
		} else if (leftBrotherNode != null && leftBrotherNode.getElements().size() > MIN_ELEMENT_NUM_PER_NODE) {
			borrowFromLeftBrother(targetNode, targetParentElement, leftBrotherNode);
		}

	}

	private static void borrowFromLeftBrother(Node targetNode, Element targetParentElement, Node leftBrotherNode) {
		Element maxElement = leftBrotherNode.getMaxElement();
		leftBrotherNode.removeElement(maxElement);
		targetParentElement.setT(maxElement.getT());
		targetNode.addElement(maxElement);
	}

	/**
	 * 右侧节点借元素，且不同源
	 */
	private static void borrowFromRightBrother(Node targetNode, Element targetParentElement,
																						 Node rightBrotherNode) {
		Element rightBrotherMinElement = rightBrotherNode.getMinElement();
		rightBrotherNode.removeElement(rightBrotherMinElement);
		targetParentElement.setT(rightBrotherMinElement.getT());
		targetNode.addElement(rightBrotherMinElement);
	}

	/**
	 * 是否存在富有的兄弟节点
	 * @param nodeRelation	节点关系
	 * @return	true 存在   false 不存在
	 */
	private static boolean existRichBrotherNode(NodeRelation nodeRelation) {
		Node leftBrotherNode = nodeRelation.leftBrotherNode;
		Node rightBrotherNode = nodeRelation.rightBrotherNode;
		if (leftBrotherNode != null && leftBrotherNode.getElements().size() > MIN_ELEMENT_NUM_PER_NODE) {
			return true;
		}
		if (rightBrotherNode != null && rightBrotherNode.getElements().size() > MIN_ELEMENT_NUM_PER_NODE) {
			return true;
		}
		return false;
	}

	/**
	 * 构建当前节点的关系
	 * @param node
	 * @return
	 */
	private static NodeRelation buildNodeRelation(Node node) {
		NodeRelation nodeRelation = new NodeRelation();
		nodeRelation.targetNode = node;
		nodeRelation.rightBrotherNode = node.getNextLeafNode();
		for (Node lookup : node.getParent().getChildrenNode()) {
			if (lookup.getNextLeafNode() == node) {
				nodeRelation.leftBrotherNode = lookup;
			}
		}

		for (Element parentElement : node.getParent().getElements()) {
			if (Objects.equal(parentElement.getLeftNode(), node) || Objects.equal(parentElement.getRightNode(), node)) {
				nodeRelation.targetParentElement = parentElement;
			}
			if (nodeRelation.leftBrotherNode != null
					&& (Objects.equal(parentElement.getLeftNode(), nodeRelation.leftBrotherNode)
					|| Objects.equal(parentElement.getRightNode(), nodeRelation.leftBrotherNode))) {
				nodeRelation.leftBrotherParentElement = parentElement;
			}
			if (nodeRelation.rightBrotherNode != null
					&& (Objects.equal(parentElement.getLeftNode(), nodeRelation.rightBrotherNode)
					|| Objects.equal(parentElement.getRightNode(), nodeRelation.rightBrotherNode))) {
				nodeRelation.rightBrotherParentElement = parentElement;
			}
		}
		return nodeRelation;
	}

	private static class NodeRelation {
		private Node targetNode = null;
		private Element targetParentElement = null;
		private Node leftBrotherNode = null;
		private Element leftBrotherParentElement = null;
		private Node rightBrotherNode = null;
		private Element rightBrotherParentElement = null;
	}

	/**
	 * 找到要删除元素的正确的引用
	 * @param belongNode
	 * @param element
	 * @return
	 */
	private static Element reDirectElement(Node belongNode, Element element) {
		for (Element lookup : belongNode.getElements()) {
			if (Objects.equal(lookup, element)) {
				return lookup;
			}
		}
		throw new RuntimeException();
	}

	/**
	 * 寻找目标元素的记录节点
	 * @param node
	 * @param element
	 * @return
	 */
	private static Node findBelongNode(Node node, Element element) {
		if (node.isLeaf()) {
			if (node.getElements().contains(element)) {
				return node;
			} else {
				return findBelongNode(node.getNextLeafNode(), element);
			}
		} else {
			return findBelongNode(node.getMinElement().getLeftNode(), element);
		}
	}
}

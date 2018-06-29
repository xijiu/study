package com.lkn.algorithm.b_tree;

import com.google.common.base.Objects;
import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
import com.lkn.algorithm.util.Parasite;

import java.util.Set;

/**
 * @author likangning
 * @since 2018/6/27 下午8:37
 */
public class TreeDelete {

	// 每个节点中最少存在的元素个数
	private static final int MIN_ELEMENT_NUM_PER_NODE = (int) (Math.ceil((double) Node.ORDER_NUM / 2) - 1);


	/**
	 * 删除元素element，分两大部分：
	 * 1、删除
	 * 		a、如果是叶子节点的话，直接删除
	 * 		b、非叶子节点的话，寻找其后继节点（递归操作）进行替换，直至找到叶子节点
	 * 2、调整 （在进行了第一步删除操作后，某个叶子节点中的元素数量发生了变化，已经不符合B树的特征，故需要对数进行调整）
	 * 		a、如果叶子节点元素的数量>={@link this#MIN_ELEMENT_NUM_PER_NODE}，那么删除结束
	 * 		b、否则查看其左兄弟节点（如果没有则选择右兄弟节点）的元素是否>={@link this#MIN_ELEMENT_NUM_PER_NODE}
	 * 				成立：从兄弟节点中借出一个节点置于其父节点中，同时在父节点中将对应的元素下移
	 * 				不成立：两个兄弟节点合并为一个节点
	 *
	 * @param root    根节点
	 * @param element 将要删除的元素
	 */
	public static void delete(Node root, Element element) {
		Node belongToNode = findBelongToNode(root, element);
		element = replaceElement(belongToNode, element);
		// 执行删除
		Node actuallyDeletedNode = doDeleteElement(belongToNode, element);
		// 执行调整，只会调整，不发生元素的删除
		doAdjust(actuallyDeletedNode);
	}

	private static Element replaceElement(Node belongToNode, Element element) {
		for (Element lookup : belongToNode.getElements()) {
			if (Objects.equal(lookup, element)) {
				return lookup;
			}
		}
		throw new RuntimeException();
	}

	/**
	 * 发生删除节点的元素中的数量没达到阈值，那么不需要调整
	 *
	 * @param targetNode
	 */
	private static void doAdjust(Node targetNode) {
		if (targetNode.getParent() != null && targetNode.getElements().size() < MIN_ELEMENT_NUM_PER_NODE) {
			// 获取兄弟节点
			BrotherNodeBean brotherNodeBean = getMoreElementsBrotherNode(targetNode);
			Node brotherNode = brotherNodeBean.brotherNode;
			// 如果可以从兄弟节点处借来元素，那么不需要发生节点合并操作
			if (brotherNode.getElements().size() > MIN_ELEMENT_NUM_PER_NODE) {
				borrowElementFromBrotherNode(targetNode, brotherNodeBean);
			} else {
				// 两个兄弟节点合并
				nodeMerge(targetNode, brotherNodeBean);
			}
		}
	}

	/**
	 * 节点合并，即两个兄弟节点+父节点中的某个元素一起合并为新的节点
	 *
	 * @param targetNode	目标节点
	 * @param brotherNodeBean	该目标节点的兄弟节点
	 */
	private static void nodeMerge(Node targetNode, BrotherNodeBean brotherNodeBean) {
		Node parent = targetNode.getParent();
		Element brotherFatherElement = getFatherElement(brotherNodeBean.brotherNode);
		Element targetFatherElement = getFatherRelationElement(targetNode);
		Node leftNode = brotherNodeBean.left ? brotherNodeBean.brotherNode : targetNode;
		Node rightNode = brotherNodeBean.left ? targetNode : brotherNodeBean.brotherNode;
		Element leftElement = findLastElement(leftNode);

		// 如果父节点为跟节点，且父节点只剩下一个元素；树的高度降低
		if (parent.getParent() == null && parent.getElements().size() == 1) {
			// 调整两侧节点中的子节点对应的父节点
			transChildrenToTargetNode(leftNode, parent);
			transChildrenToTargetNode(rightNode, parent);
			parent.addElements(leftNode.getElements());
			parent.addElements(rightNode.getElements());
			targetFatherElement.setLeftNode(leftElement.getRightNode());
			targetFatherElement.setRightNode(null);
			leftElement.setRightNode(null);
		} else if (targetFatherElement.compareTo(brotherFatherElement) != 0) {
			// 目标节点在兄弟节点左侧
			brotherFatherElement.setLeftNode(targetFatherElement.getLeftNode());
			leftNode.addElement(targetFatherElement);
			leftNode.addElements(rightNode.getElements());
			// 转移孩子
			transChildrenToTargetNode(rightNode, leftNode);
			targetFatherElement.setLeftNode(leftElement.getRightNode());
			leftElement.setRightNode(null);
			parent.removeElement(targetFatherElement);
			doAdjust(parent);
		} else {
			// 两个元素相等，说明该元素是最右侧的元素，故需要获取其左边的元素
			Element targetLeftFatherElement = getLeftElement(targetNode.getParent(), targetFatherElement);
			targetLeftFatherElement.setRightNode(targetFatherElement.getRightNode());

			rightNode.addElement(targetFatherElement);
			rightNode.addElements(leftNode.getElements());
			// 转移孩子
			transChildrenToTargetNode(leftNode, rightNode);
			targetFatherElement.setLeftNode(leftElement.getRightNode());
			targetFatherElement.setRightNode(null);
			parent.removeElement(targetFatherElement);
			doAdjust(parent);
		}
	}

	/**
	 * 将节点的孩子转移给目标节点
	 *
	 * @param node	当前要转移孩子的节点
	 * @param targetNode	要接受孩子的节点
	 */
	private static void transChildrenToTargetNode(Node node, Node targetNode) {
		for (Node child : node.getChildrenNode()) {
			child.setParent(targetNode);
		}
	}

	private static Element getLeftElement(Node node, Element targetFatherElement) {
		Element returnElement = null;
		for (Element element : node.getElements()) {
			if (Objects.equal(element, targetFatherElement)) {
				break;
			}
			returnElement = element;
		}
		return returnElement;
	}

	/**
	 * 获取此次旋转时，与父元素有关的元素
	 *
	 * @param targetNode	目标节点
	 * @return 相关元素
	 */
	private static Element getFatherRelationElement(Node targetNode) {
		Node parent = targetNode.getParent();
		Set<Element> elements = parent.getElements();
		for (Element lookup : elements) {
			// 如果连接的左右节点中存在与目标节点相等的
			if (Objects.equal(lookup.getLeftNode(), targetNode) || Objects.equal(lookup.getRightNode(), targetNode)) {
				return lookup;
			}
		}
		throw new RuntimeException();
	}

	/**
	 * 从兄弟节点中借元素
	 * 此种情况，只会发生在目标节点为叶子节点中
	 *
	 * @param targetNode	目标节点，只能为叶子节点
	 * @param brotherNodeBean	该目标节点的兄弟节点，左右都有可能
	 */
	private static void borrowElementFromBrotherNode(Node targetNode, BrotherNodeBean brotherNodeBean) {
		Node parent = targetNode.getParent();
		Node brotherNode = brotherNodeBean.brotherNode;
		Element fatherElement = getFatherElement(brotherNode);
		// 父元素左子孩子
		Node fatherChildLeftNode = fatherElement.getLeftNode();
		// 父元素右子孩子
		Node fatherChildRightNode = fatherElement.getRightNode();
		boolean left = brotherNodeBean.left;
		Element brotherRemoveElement;
		// 如果是左边兄弟节点
		if (left) {
			// 将要删除的元素从兄弟节点中删除
			brotherRemoveElement = findLastElement(brotherNode);
			brotherNode.removeElement(brotherRemoveElement);
			Node brotherRightNode = brotherRemoveElement.getRightNode();
			// 设置兄弟节点最后侧节点的右指针
			Element onBrotherRemoveElementLeft = findLastElement(brotherNode);
			onBrotherRemoveElementLeft.setRightNode(brotherRemoveElement.getLeftNode());

			// 替换父节点元素的功能
			brotherRemoveElement.setLeftNode(fatherChildLeftNode);
			brotherRemoveElement.setRightNode(fatherChildRightNode);
			parent.removeElement(fatherElement);
			parent.addElement(brotherRemoveElement);

			// 处理目标节点
			fatherElement.setLeftNode(brotherRightNode);
			fatherElement.setRightNode(null);
			targetNode.addElement(fatherElement);
		} else {
			// 先处理兄弟节点
			brotherRemoveElement = findFirstElement(brotherNode);
			brotherNode.removeElement(brotherRemoveElement);
			Node brotherLeftNode = brotherRemoveElement.getLeftNode();

			// 处理父节点
			brotherRemoveElement.setLeftNode(fatherChildLeftNode);
			brotherRemoveElement.setRightNode(fatherChildRightNode);
			parent.removeElement(fatherElement);
			parent.addElement(brotherRemoveElement);

			// 处理目标节点
			Element leftLastElement = findLastElement(targetNode);
			fatherElement.setLeftNode(leftLastElement.getRightNode());
			leftLastElement.setRightNode(null);
			fatherElement.setRightNode(brotherLeftNode);
			targetNode.addElement(fatherElement);
		}

	}

	private static Element getFatherElement(Node node) {
		Node parent = node.getParent();
		Set<Element> elements = parent.getElements();
		for (Element element : elements) {
			Node leftNode = element.getLeftNode();
			Node rightNode = element.getRightNode();
			if (Objects.equal(node, leftNode) || Objects.equal(node, rightNode)) {
				return element;
			}
		}
		throw new RuntimeException("can not find parent element");
	}

	/**
	 * 获取兄弟节点中元素较多的兄弟节点
	 * @param targetNode
	 * @return
	 */
	private static BrotherNodeBean getMoreElementsBrotherNode(Node targetNode) {
		// 左兄弟节点
		Node leftBrotherNode = null;
		// 右兄弟节点
		Node rightBrotherNode = null;
		boolean isAfterTargetNode = false;
		Node brotherNode = null;
		Node parent = targetNode.getParent();
		for (Node lookup : parent.getChildrenNode()) {
			if (Objects.equal(targetNode, lookup)) {
				isAfterTargetNode = true;
				leftBrotherNode = brotherNode;
				continue;
			}
			if (isAfterTargetNode) {
				// 右兄弟节点
				rightBrotherNode = lookup;
				break;
			}
			brotherNode = lookup;
		}

		return assembleBrotherNodeBean(leftBrotherNode, rightBrotherNode);
	}

	/**
	 * 组装兄弟节点bean，比较左右两个兄弟节点，选取元素较多的节点返回
	 * 如果两个兄弟节点的长度一样，那么选取右兄弟节点
	 *
	 * @param leftBrotherNode	左兄弟节点
	 * @param rightBrotherNode	右兄弟节点
	 * @return	元素较多的兄弟节点
	 */
	private static BrotherNodeBean assembleBrotherNodeBean(Node leftBrotherNode, Node rightBrotherNode) {
		BrotherNodeBean brotherNodeBean = new BrotherNodeBean();
		if (leftBrotherNode == null) {
			brotherNodeBean.left = false;
			brotherNodeBean.brotherNode = rightBrotherNode;
		} else if (rightBrotherNode == null) {
			brotherNodeBean.left = true;
			brotherNodeBean.brotherNode = leftBrotherNode;
		} else {
			if (rightBrotherNode.getElements().size() >= leftBrotherNode.getElements().size()) {
				brotherNodeBean.left = false;
				brotherNodeBean.brotherNode = rightBrotherNode;
			} else if (rightBrotherNode.getElements().size() < leftBrotherNode.getElements().size()) {
				brotherNodeBean.left = true;
				brotherNodeBean.brotherNode = leftBrotherNode;
			}
		}
		return brotherNodeBean;
	}

	private static class BrotherNodeBean {
		private Node brotherNode;
		private boolean left;
	}

	/**
	 * 执行删除操作
	 * @return	真正发生了删除元素操作的节点
	 */
	private static Node doDeleteElement(Node node, Element deleteElement) {
		if (node.isLeaf()) {
			// 叶子节点，直接删除
			node.removeElement(deleteElement);
			return node;
		} else {
			// 非叶子节点，递归寻找其后继第一个节点
			Node descendantLeafNode = findDescendantNode(node, deleteElement);
			Element descendantElement = findFirstElement(descendantLeafNode);
			descendantElement.setLeftNode(deleteElement.getLeftNode());
			descendantElement.setRightNode(deleteElement.getRightNode());
			descendantLeafNode.removeElement(descendantElement);
			node.removeElement(deleteElement);
			node.addElement(descendantElement);
			return descendantLeafNode;
		}
	}

	/**
	 * 寻找后继节点中的第一个叶子节点
	 */
	private static Node findDescendantNode(Node node, Element element) {
		// 直接后继节点
		Node descendantNode = findDirectDescendantNode(node, element);
		while (!descendantNode.isLeaf()) {
			Element descendantNodeElement = findFirstElement(descendantNode);
			descendantNode = descendantNodeElement.getLeftNode();
		}
		return descendantNode;
	}

	private static Element findFirstElement(Node node) {
		Set<Element> elements = node.getElements();
		for (Element element : elements) {
			return element;
		}
		throw new RuntimeException();
	}

	private static Element findLastElement(Node node) {
		Element lastElement = null;
		Set<Element> elements = node.getElements();
		for (Element element : elements) {
			lastElement = element;
		}
		return lastElement;
	}

	/**
	 * 寻找当前节点的直接的后继节点
	 * @param node
	 * @param element
	 * @return
	 */
	private static Node findDirectDescendantNode(Node node, Element element) {
		// 直接后继节点
		Node directDescendantNode = null;
		if (element.getRightNode() != null) {
			directDescendantNode = element.getRightNode();
		} else {
			boolean isBegin = false;
			Set<Element> elements = node.getElements();
			for (Element lookup : elements) {
				if (Objects.equal(lookup, element)) {
					isBegin = true;
				} else {
					if (isBegin) {
						directDescendantNode = lookup.getLeftNode();
						break;
					}
				}
			}
		}
		return directDescendantNode;
	}


	/**
	 * 寻找该元素所属的节点
	 * @param root	根节点
	 * @param element	目标元素
	 * @return	该目标元素所属的节点
	 */
	private static Node findBelongToNode(Node root, Element element) {
		Parasite<Node> parasite = new Parasite<>();
		Tree.bfs(root, (node, level) -> {
			if (node.getElements().contains(element)) {
				parasite.set(node);
			}
		});
		return parasite.get();
	}
}

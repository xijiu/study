package com.lkn.algorithm.b_tree;

import com.google.common.base.Objects;
import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
import com.lkn.algorithm.index_file.ThreadHelper;
import lombok.Builder;

import java.util.Set;

/**
 * B树添加操作
 *
 * @author likangning
 * @since 2018/6/27 上午9:56
 */
public class TreeAdd {

	/**
	 * 添加一个新元素
	 * @param element 新节点
	 */
	public static void add(Node node, Element element) {
		addNewElementNormally(node, element);
		adjust(node);
	}

	/**
	 * 节点调整
	 * @param node	目标节点
	 */
	public static void adjust(Node node) {
		// 如果元素的数量已经大于等于阶数，那么对当前节点进行分裂操作
		if (node.getElements().size() >= Node.ORDER_NUM) {
			SplitBean splitBean = splitNode(node);
			// 当前节点为根节点
			if (node.getParent() == null) {
				node.clearElements();
				addNewElementToNode(node, splitBean.middleElement);
			} else {
				// 如果为非跟节点，那么继续执行递归方法
				ThreadHelper.putNode(node.getParent());
				add(node.getParent(), splitBean.middleElement);
			}
		}
	}

	/**
	 * 正常添加元素，不关心元素的数量是否已经超过阈值
	 *
	 * @param node 当前正操作的节点
	 * @param newElement 新增的元素
	 */
	private static void addNewElementNormally(Node node, Element newElement) {
		addNewElementToNode(node, newElement);
		Element[] bothSidesElements = getNewElementBothSidesElements(node, newElement);
		Element beforeNewElement = bothSidesElements[0];
		Element afterNewElement = bothSidesElements[1];
		if (afterNewElement != null) {
			afterNewElement.setLeftNode(newElement.getRightNode());
			newElement.setRightNode(null);
		}
		if (beforeNewElement != null) {
			beforeNewElement.setRightNode(null);
		}
	}

	/**
	 * 节点分裂
	 */
	private static SplitBean splitNode(Node node) {
		Element middleElement = null;
		Node leftNode = new Node(node.getParent() == null ? node : node.getParent());
		Node rightNode = new Node(node.getParent() == null ? node : node.getParent());
		int index = 0;
		Element lastElementInLeftNode = null;
		Set<Element> elements = node.getElements();
		for (Element element : elements) {
			if (index == Node.MIDDLE_INDEX) {
				middleElement = element;
				// 中间节点所连接的子node隶属于 leftNode
				lastElementInLeftNode.setRightNode(element.getLeftNode());
				// 将中间节点加入 leftNode 中
				resetFatherNodeForElement(leftNode, element);
				middleElement.setLeftNode(leftNode);
				middleElement.setRightNode(rightNode);
			} else if (index < Node.MIDDLE_INDEX) {
				addNewElementToNode(leftNode, element);
				lastElementInLeftNode = element;
			} else {
				addNewElementToNode(rightNode, element);
			}
			index++;
		}
		return SplitBean.builder()
				.middleElement(middleElement)
				.leftNode(leftNode)
				.rightNode(rightNode)
				.build();
	}

	/**
	 * 向节点中添加一个元素
	 * 1、将新的元素添加入{@code elements}中
	 * 2、将{@code elements}中的左右节点的父节点置为参数node
	 *
	 * @param node	节点对象
	 * @param element	元素
	 */
	private static void addNewElementToNode(Node node, Element element) {
		if (node != null && element != null) {
			node.addElement(element);
			resetFatherNodeForElement(node, element);
		}
	}

	/**
	 * 为元素的子node重置父节点
	 *
	 * @param node	将要被置为父节点的node
	 * @param element	当前元素
	 */
	private static void resetFatherNodeForElement(Node node, Element element) {
		Node leftNode = element.getLeftNode();
		Node rightNode = element.getRightNode();
		if (leftNode != null) {
			leftNode.setParent(node);
		}
		if (rightNode != null) {
			rightNode.setParent(node);
		}
	}

	/**
	 * 获取新添加元素后面的元素
	 * @param node 要操作的节点
	 * @param newAddElement	新增元素
	 * @return	新增元素后面的元素
	 */
	private static Element[] getNewElementBothSidesElements(Node node, Element newAddElement) {
		Element[] arrElement = new Element[2];
		Element beforeNewElement = null;
		Element afterNewElement = null;
		// 标记上一个元素是否是新增元素
		boolean valid = false;
		for (Element lookupElement : node.getElements()) {
			if (valid) {
				afterNewElement = lookupElement;
				break;
			} else {
				if (Objects.equal(lookupElement, newAddElement)) {
					valid = true;
				} else {
					beforeNewElement = lookupElement;
				}
			}
		}
		arrElement[0] = beforeNewElement;
		arrElement[1] = afterNewElement;
		return arrElement;
	}

	/**
	 * 内部使用，方便参数传递
	 */
	@Builder
	private static class SplitBean {
		private Element middleElement;
		private Node leftNode;
		private Node rightNode;
	}

}

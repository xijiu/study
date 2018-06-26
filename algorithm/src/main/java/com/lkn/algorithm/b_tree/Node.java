package com.lkn.algorithm.b_tree;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;

/**
 * B树的节点，一个节点中可能有多个元素
 * @author likangning
 * @since 2018/6/22 下午12:07
 */
public class Node {
	// 阶数
	public static final int ORDER_NUM = 5;
	private static final int MIDDLE_INDEX = (ORDER_NUM % 2 == 0 ? ORDER_NUM - 2 : ORDER_NUM - 1) / 2;

	/**
	 * 当前节点拥有的元素
 	 */
	private Set<Element> elements = Sets.newTreeSet();

	// 当前节点的父节点
	@Setter
	@Getter
	private Node parent;

	public Node(Node parent) {
		this.parent = parent;
	}

	public Set<Element> getElements() {
		return Collections.unmodifiableSet(elements);
	}

	/**
	 * 添加一个新元素
	 * @param element 新节点
	 */
	public void add(Element element) {
		addNewElementNormally(element);
		// 如果元素的数量已经大于等于阶数，那么对当前节点进行分裂操作
		if (elements.size() >= ORDER_NUM) {
			SplitBean splitBean = splitNode();
			// 当前节点为根节点
			if (parent == null) {
				elements.clear();
				addNewElementToNode(this, splitBean.middleElement);
			} else {
				parent.add(splitBean.middleElement);
			}
		}
	}

	/**
	 * 正常添加元素
	 *
	 * @param newElement 新增的元素
	 */
	private void addNewElementNormally(Element newElement) {
		addNewElementToNode(this, newElement);
		Element[] bothSidesElements = getNewElementBothSidesElements(newElement);
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
	private SplitBean splitNode() {
		Element middleElement = null;
		Node leftNode = new Node(parent == null ? this : this.parent);
		Node rightNode = new Node(parent == null ? this : this.parent);
		int index = 0;
		Element lastElementInLeftNode = null;
		for (Element element : elements) {
			if (index == MIDDLE_INDEX) {
				middleElement = element;
				// 中间节点所连接的子node隶属于 leftNode
				lastElementInLeftNode.setRightNode(element.getLeftNode());
				// 将中间节点加入 leftNode 中
				resetFatherNodeForElement(leftNode, element);
				middleElement.setLeftNode(leftNode);
				middleElement.setRightNode(rightNode);
			} else if (index < MIDDLE_INDEX) {
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
	private void addNewElementToNode(Node node, Element element) {
		if (node != null && element != null) {
			node.elements.add(element);
			resetFatherNodeForElement(node, element);
		}
	}

	/**
	 * 为元素的子node重置父节点
	 *
	 * @param node	将要被置为父节点的node
	 * @param element	当前元素
	 */
	private void resetFatherNodeForElement(Node node, Element element) {
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
	 * @param newAddElement	新增元素
	 * @return	新增元素后面的元素
	 */
	private Element[] getNewElementBothSidesElements(Element newAddElement) {
		Element[] arrElement = new Element[2];
		Element beforeNewElement = null;
		Element afterNewElement = null;
		// 标记上一个元素是否是新增元素
		boolean valid = false;
		for (Element lookupElement : elements) {
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

	@Builder
	private static class SplitBean {
		private Element middleElement;
		private Node leftNode;
		private Node rightNode;
	}

	@Override
	public String toString() {
		return elements.toString();
	}


}

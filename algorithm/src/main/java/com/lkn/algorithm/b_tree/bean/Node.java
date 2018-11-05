package com.lkn.algorithm.b_tree.bean;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lkn.algorithm.b_tree_plus.index_file.DefaultIndexFileOperation;
import com.lkn.algorithm.b_tree_plus.index_file.IndexFileOperation;
import com.lkn.algorithm.b_tree_plus.index_file.IndexHeaderDesc;
import com.lkn.algorithm.b_tree_plus.index_file.ThreadHelper;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * B树的节点，一个节点中可能有多个元素
 *
 * @author likangning
 * @since 2018/6/22 下午12:07
 */
public class Node {
	// 阶数
//	public static int ORDER_NUM = 254;
	public static volatile int ORDER_NUM = 5;
	public static volatile int MIDDLE_INDEX = (ORDER_NUM % 2 == 0 ? ORDER_NUM - 2 : ORDER_NUM - 1) / 2;

	private static IndexFileOperation indexFileOperation = DefaultIndexFileOperation.getSingleInstance();

	/**
	 * 当前节点拥有的元素
 	 */
	private Set<Element> elements = Sets.newTreeSet();

	/**
	 * 硬盘存储的唯一id
	 */
	@Setter
	@Getter
	private Integer hardDiskId;

	// 当前节点的父节点
	@Setter
	@Getter
	private Node parent;

	/**
	 * 当前节点的右侧节点
	 * 只有当前节点为叶子节点时，此项才有值，且最右侧的叶子节点此项也为null
	 * 注：此项内容只服务于B+树
	 */
	@Getter
	private Node nextLeafNode;

	/**
	 * 当前叶子节点的上一个叶子节点
	 */
	@Getter
	private Node beforeLeafNode;

	public Node(int hardDiskId) {
		this(hardDiskId, null);
	}

	public Node(Integer hardDiskId, Integer parentHardDiskId) {
		this.hardDiskId = hardDiskId;
		if (parentHardDiskId != null && !Objects.equal(parentHardDiskId, -1)) {
			this.parent = new Node(parentHardDiskId);
		}
	}

	/**
	 * 新分配节点
	 *
	 * @param parent	父节点
	 */
	public Node(Node parent) {
		this(parent, -1);
	}

	public Node(Node parent, int hardDiskId) {
		this.parent = parent;
		if (parent == null) {
			// 如果新节点为根节点，那么默认将其位置放在1上
			this.hardDiskId = 1;
		} else {
			// 如果未指定新节点开辟空间的位置，那么需要从硬盘中重新开辟新位置，并维护索引文件的描述信息
			if (hardDiskId == -1) {
				synchronized (Node.class) {
					// 1、从索引文件描述信息中，获取最大的node编号并+1
					// 2、向硬盘中写入索引描述信息
					IndexHeaderDesc desc = indexFileOperation.readIndexHeaderDesc();
					int currNumber = desc.getMaxNodeNumber() + 1;
					setHardDiskId(currNumber);
					desc.setMaxNodeNumber(currNumber);
					indexFileOperation.writeIndexHeaderDesc(desc);
				}
			} else {
				this.hardDiskId = hardDiskId;
			}
		}
		ThreadHelper.putNode(this);
	}

	public static void resetOrderNum(int orderNum) {
		ORDER_NUM = orderNum;
		MIDDLE_INDEX = (ORDER_NUM % 2 == 0 ? ORDER_NUM - 2 : ORDER_NUM - 1) / 2;
	}

	/**
	 * 设置当前叶子节点的下一个节点
	 * 同时将下一个节点的前置节点设置为当前节点
	 *
	 */
	public void setNextLeafNode(Node nextLeafNode) {
		this.nextLeafNode = nextLeafNode;
		if (nextLeafNode != null) {
			nextLeafNode.beforeLeafNode = this;
		}
	}

	public Set<Element> getElements() {
		return Collections.unmodifiableSet(elements);
	}

	public void clearElements() {
		elements.clear();
	}

	public void addElement(Element element) {
		elements.add(element);
	}

	public void addElements(Collection<Element> elements) {
		for (Element element : elements) {
			addElement(element);
		}
	}

	public void removeElement(Element element) {
		elements.remove(element);
	}

	public Element getMinElement() {
		for (Element element : elements) {
			return element;
		}
		throw new RuntimeException();
	}

	public Element getMaxElement() {
		Element max = null;
		for (Element element : elements) {
			max = element;
		}
		return max;
	}

	public List<Node> getChildrenNode() {
		List<Node> list = Lists.newArrayList();
		for (Element element : elements) {
			Node leftNode = element.getLeftNode();
			Node rightNode = element.getRightNode();
			if (leftNode != null) {
				list.add(leftNode);
			}
			if (rightNode != null) {
				list.add(rightNode);
			}
		}
		return list;
	}

	@Override
	public String toString() {
		return elements.toString();
	}

	/**
	 * 是否是叶子节点
	 */
	public boolean isLeaf() {
		if (elements.size() == 0) {
			return true;
		}
		for (Element element : elements) {
			return element.getLeftNode() == null;
		}
		throw new RuntimeException("节点中的元素为null");
	}


}

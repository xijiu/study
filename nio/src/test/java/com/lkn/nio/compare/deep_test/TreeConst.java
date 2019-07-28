package com.lkn.nio.compare.deep_test;

public interface TreeConst {

	/**
	 * 每个节点占用的空间（单位字节）
	 */
	int NODE_SPACE = 4096;

	/**
	 * 叶子节点的每个元素占用空间
	 * a(8) + t(8) = 16
	 */
	int LEAF_NODE_ELEMENT_SPACE = 16;

	/**
	 * 叶子节点，每个节点的元素个数
	 */
	int LEAF_NODE_SIZE = NODE_SPACE / LEAF_NODE_ELEMENT_SPACE;

	/**
	 * 非叶子节点的每个元素占用空间
	 * a(8) + t(8) + leftNode(4) = 20
	 */
	int INDEX_NODE_ELEMENT_SPACE = 20;

	/**
	 * 非叶子节点，每个节点的元素个数
	 */
	int INDEX_NODE_SIZE = NODE_SPACE / INDEX_NODE_ELEMENT_SPACE;

	/**
	 * 数据节点中，每个节点占用的空间为34字节
	 */
	int DATA_NODE_SIZE = 34;

}
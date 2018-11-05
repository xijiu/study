package com.lkn.algorithm.b_tree_plus.index_file;

import lombok.Data;

/**
 * 索引头部的描述信息
 *
 * @author likangning
 * @since 2018/10/29 上午10:57
 */
@Data
public class IndexHeaderDesc {

	/**
	 * 根节点的索引位置
	 */
	private int rootIndex;

	/**
	 * 最大节点编号
	 */
	private int maxNodeNumber;
}

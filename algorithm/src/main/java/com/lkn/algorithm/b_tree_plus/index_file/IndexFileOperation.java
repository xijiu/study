package com.lkn.algorithm.b_tree_plus.index_file;

import com.lkn.algorithm.b_tree.bean.Node;

/**
 * 操作索引文件的接口
 *
 * @author likangning
 * @since 2018/10/29 上午10:30
 */
public interface IndexFileOperation {

	/**
	 * 文件读取
	 *
	 * @param fileIndex	将要读取的文件索引
	 * @param parentFileIndex	该文件的父文件索引
	 * @return	当前节点对象
	 */
	Node read(int fileIndex, int parentFileIndex);

	/**
	 * 文件写入
	 *
	 * @param node	将要写入的节点
	 */
	void write(Node node);

	/**
	 * 读取根节点
	 *
	 * @return 根节点
	 */
	Node readRoot();

	/**
	 * 索引文件初始化
	 */
	void initIndexFile();

	/**
	 * 读取索引文件的头描述信息
	 *
	 * @return	索引文件头
	 */
	IndexHeaderDesc readIndexHeaderDesc();

	/**
	 * 写索引文件头
	 */
	void writeIndexHeaderDesc(IndexHeaderDesc indexHeaderDesc);

}

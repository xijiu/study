package com.lkn.algorithm.index_file;

import com.google.common.collect.Sets;
import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
import com.lkn.algorithm.b_tree_plus.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;

/**
 * @author likangning
 * @since 2018/10/29 上午10:30
 */
public class DefaultIndexFileOperation implements IndexFileOperation {

	/**
	 * 每个节点所占字节数
	 */
	public static final int PEER_NODE_SIZE_IN_HARD_DISK = 4096;

	/**
	 * 每个元素所占字节数
	 */
	public static final int PEER_ELEMENT_SIZE_IN_HARD_DISK = 16;

	private static RandomAccessFile RANDOM_ACCESS_FILE;

	private static IndexFileOperation INDEX_FILE_OPERATION = new DefaultIndexFileOperation();

	static {
		// 索引文件
		File indexFile = new File(Constant.INDEX_FILE_PATH);
//		if (indexFile.exists()) {
//			indexFile.delete();
//		}
		try {
			RANDOM_ACCESS_FILE = new RandomAccessFile(indexFile, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (indexFile.length() == 0) {
			IndexHeaderDesc desc = new IndexHeaderDesc();
			desc.setRootIndex(-1);
			desc.setMaxNodeNumber(0);
			INDEX_FILE_OPERATION.writeIndexHeaderDesc(desc);
		}
	}

	public static IndexFileOperation getSingleInstance() {
		return INDEX_FILE_OPERATION;
	}

	private DefaultIndexFileOperation() {}

	/**
	 * 文件读取
	 *
	 * @param fileIndex	将要读取的文件索引
	 * @param parentFileIndex	该文件的父文件索引
	 * @return	当前节点对象
	 */
	public synchronized Node read(int fileIndex, int parentFileIndex) {
		System.out.println("进行了一次硬盘随机读");
		// 跳跃至文件指定位置
		jumpToTargetNode(fileIndex);
		byte[] arr = readNodeByteArr();
		return transByteArrToBean(arr, fileIndex, parentFileIndex);
	}

	/**
	 * 将字节转换为Node对象
	 *
	 * @param arr	字节对象
	 * @param fileIndex	自身文件的编号
	 * @param parentFileIndex	该文件的父文件的编号
	 * @return	内存中的节点对象
	 */
	private Node transByteArrToBean(byte[] arr, int fileIndex, int parentFileIndex) {
		Node node = new Node(fileIndex, parentFileIndex);
		Set<Element> elements = transToElements(arr);
		node.addElements(elements);
		return node;
	}

	/**
	 * 将字节码转换为元素集合
	 *
	 * @param arr	字节码
	 * @return	元素有序集合
	 */
	private Set<Element> transToElements(byte[] arr) {
		ByteBuffer buffer = ByteBuffer.wrap(arr);
		buffer.order(ByteOrder.BIG_ENDIAN);

		// 当前节点中的元素的个数
		long elementSize = buffer.getLong();
		long stance = buffer.getLong();
		Set<Element> set = Sets.newTreeSet();
		for (int i = 0; i < elementSize; i++) {
			int leftId = buffer.getInt();
			long elementContent = buffer.getLong();
			int rightId = buffer.getInt();
			Element<Long> element = new Element<>(elementContent);
			if (leftId != -1) {
				element.setLeftNode(new Node(leftId));
			}
			if (rightId != -1) {
				element.setRightNode(new Node(rightId));
			}
			set.add(element);
		}
		return set;
	}

	/**
	 * 读取一个node的全部内容
	 */
	private byte[] readNodeByteArr() {
		byte[] arr = new byte[PEER_NODE_SIZE_IN_HARD_DISK];
		try {
			RANDOM_ACCESS_FILE.read(arr, 0, PEER_NODE_SIZE_IN_HARD_DISK);
		} catch (IOException e) {
			throw new RuntimeException("read index file error", e);
		}
		return arr;
	}

	/**
	 * 跳跃至指定位置
	 * @param fileIndex	文件模块编号
	 */
	private void jumpToTargetNode(int fileIndex) {
		try {
			RANDOM_ACCESS_FILE.seek(PEER_NODE_SIZE_IN_HARD_DISK * fileIndex);
		} catch (IOException e) {
			throw new RuntimeException("RandomAccessFile seek fail", e);
		}
	}

	/**
	 * 文件写入
	 * @param node	将要写入的节点
	 */
	public synchronized void write(Node node) {
		if (node != null && node.getHardDiskId() != null) {
			// 将节点转换为字节数组
			byte[] arr = transNodeToByteArr(node);
			// 跳跃至文件指定位置
			jumpToTargetNode(node.getHardDiskId());
			// 执行节点内容写入操作
			writeByteArrNode(arr);
		}
	}

	/**
	 * 将一个node的内容写入文件
	 */
	private void writeByteArrNode(byte[] arr) {
		try {
			RANDOM_ACCESS_FILE.write(arr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将节点对象转换为字节数组
	 */
	private byte[] transNodeToByteArr(Node node) {
		Set<Element> elements = node.getElements();
		ByteBuffer buf = ByteBuffer.allocate(PEER_NODE_SIZE_IN_HARD_DISK);
		buf.order(ByteOrder.BIG_ENDIAN);
		// 前16个字节为当前node的说明信息，目前只有前8个字节有用，后8个字节为预留字节
		buf.putLong(elements.size());
		buf.putLong(-1L);
		for (Element<Long> element : elements) {
			Node leftNode = element.getLeftNode();
			Node rightNode = element.getRightNode();
			buf.putInt(leftNode == null ? -1 : leftNode.getHardDiskId());
			buf.putLong(element.getT());
			buf.putInt(rightNode == null ? -1 : rightNode.getHardDiskId());
		}
		buf.flip();
		return buf.array();
	}

	@Override
	public synchronized Node readRoot() {
		IndexHeaderDesc indexHeaderDesc = readIndexHeaderDesc();
		int rootIndex = indexHeaderDesc.getRootIndex();
		if (rootIndex == -1) {
			return null;
		} else {
			return read(rootIndex, -1);
		}
	}

	@Override
	public void initIndexFile() {
		try {
			// 如果文件中有内容，那么直接返回，不做任何操作
			if (RANDOM_ACCESS_FILE.length() > 0) {
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		jumpToTargetNode(0);
		ByteBuffer buf = allocateByteBuffer(PEER_NODE_SIZE_IN_HARD_DISK);
		// 前16个字节为当前node的说明信息，目前只有前8个字节有用，后8个字节为预留字节
		buf.putInt(1);
		buf.putLong(1L);
		buf.flip();
		try {
			RANDOM_ACCESS_FILE.write(buf.array(), 0, PEER_NODE_SIZE_IN_HARD_DISK);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ByteBuffer allocateByteBuffer(int byteSize) {
		ByteBuffer buf = ByteBuffer.allocate(byteSize);
		buf.order(ByteOrder.BIG_ENDIAN);
		return buf;
	}


	@Override
	public IndexHeaderDesc readIndexHeaderDesc() {
		jumpToTargetNode(0);
		byte[] arr = new byte[PEER_NODE_SIZE_IN_HARD_DISK];
		try {
			RANDOM_ACCESS_FILE.read(arr, 0, PEER_NODE_SIZE_IN_HARD_DISK);
		} catch (IOException e) {
			throw new RuntimeException("read index file error", e);
		}

		ByteBuffer buffer = ByteBuffer.wrap(arr);
		buffer.order(ByteOrder.BIG_ENDIAN);
		int rootIndex = buffer.getInt();
		int maxNodeNumber = buffer.getInt();
		IndexHeaderDesc desc = new IndexHeaderDesc();
		desc.setRootIndex(rootIndex);
		desc.setMaxNodeNumber(maxNodeNumber);
		return desc;
	}

	@Override
	public void writeIndexHeaderDesc(IndexHeaderDesc indexHeaderDesc) {
		byte[] arr = transIndexHeaderToByteArr(indexHeaderDesc);
		jumpToTargetNode(0);
		writeByteArrNode(arr);
	}

	/**
	 * 将节点对象转换为字节数组
	 */
	private byte[] transIndexHeaderToByteArr(IndexHeaderDesc indexHeaderDesc) {
		ByteBuffer buf = allocateByteBuffer(PEER_NODE_SIZE_IN_HARD_DISK);
		buf.putInt(indexHeaderDesc.getRootIndex());
		buf.putInt(indexHeaderDesc.getMaxNodeNumber());
		buf.flip();
		return buf.array();
	}
}

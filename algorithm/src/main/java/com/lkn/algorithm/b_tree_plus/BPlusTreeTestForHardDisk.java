package com.lkn.algorithm.b_tree_plus;

import com.lkn.algorithm.b_tree.BtreeTest;
import com.lkn.algorithm.b_tree.PrintTree;
import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
import com.lkn.algorithm.b_tree_plus.index_file.DefaultIndexFileOperation;
import com.lkn.algorithm.b_tree_plus.index_file.IndexFileOperation;
import com.lkn.algorithm.b_tree_plus.index_file.IndexHeaderDesc;
import com.lkn.algorithm.b_tree_plus.index_file.ThreadHelper;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

/**
 * 说明：B+树在硬盘上的测试
 * 1、通过方法addTest()来生成索引文件
 * 2、通过方法readTest()来查询某个元素在索引中的准确位置，如果没有则返回null
 *
 * @author likangning
 * @since 2018/6/29 下午5:43
 */
public class BPlusTreeTestForHardDisk extends BtreeTest {

	private IndexFileOperation indexFileOperation = DefaultIndexFileOperation.getSingleInstance();

	private void addLongElement(long element) {
		try {
			if (root == null) {
				root = new Node(null);
			}
			System.out.println("添加元素 ====> " + element);
			BPlusTreeAdd.add(root, new Element<>(element));
			doWriteFile();
		} finally {
			// 最后清空上下文内容
			ThreadHelper.clear();
		}
	}

	/**
	 * 写索引文件
	 */
	private void doWriteFile() {
		Collection<Node> all = ThreadHelper.getAllNode();
		for (Node node : all) {
			indexFileOperation.write(node);
		}
		IndexHeaderDesc indexHeaderDesc = indexFileOperation.readIndexHeaderDesc();
		if (indexHeaderDesc.getMaxNodeNumber() < ThreadHelper.getMaxNodeNumber()) {
			indexHeaderDesc.setMaxNodeNumber(ThreadHelper.getMaxNodeNumber());
			if (indexHeaderDesc.getRootIndex() == -1) {
				indexHeaderDesc.setRootIndex(1);
			}
			indexFileOperation.writeIndexHeaderDesc(indexHeaderDesc);
		}
	}

	@Override
	protected void deleteElement(int element) {
		System.out.println("删除元素 ====> " + element);
		BPlusTreeDelete.delete(root, new Element<>(element));
		PrintTree.print(root);
	}

	@Test
	public void addTest() {
		Node.resetOrderNum(254);
		int begin = 1;
		int end = 1000000;
		for (int i = begin; i < end; i++) {
			addLongElement(i);
		}
	}

	@Test
	public void readTest() {
		long content = (long) 782394;
		Node root = indexFileOperation.readRoot();
		Node targetNode = findNode(root, content);
		System.out.println(targetNode);
	}

	private Node findNode(Node root, long content) {
		Set<Element> elements = root.getElements();
		Element last = null;
		for (Element element : elements) {
			last = element;
			if (element.getT().compareTo(content) > 0) {
				Node leftNode = element.getLeftNode();
				leftNode = indexFileOperation.read(leftNode.getHardDiskId(), root.getHardDiskId());
				return operateNode(content, leftNode);
			}
		}
		Node rightNode = last.getRightNode();
		rightNode = indexFileOperation.read(rightNode.getHardDiskId(), root.getHardDiskId());
		return operateNode(content, rightNode);
	}

	/**
	 * 如果是叶子节点
	 * 		包含了目标内容，那么直接返回当前节点
	 * 		否则返回null
	 * 如果是非叶子节点，那么继续递归
	 *
	 */
	private Node operateNode(long content, Node node) {
		if (node.isLeaf()) {
			if (node.getElements().contains(new Element(content))) {
				return node;
			} else {
				return null;
			}
		} else {
			return findNode(node, content);
		}
	}


}

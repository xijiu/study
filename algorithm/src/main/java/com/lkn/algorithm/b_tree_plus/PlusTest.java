package com.lkn.algorithm.b_tree_plus;

import com.lkn.algorithm.b_tree.BtreeTest;
import com.lkn.algorithm.b_tree.PrintTree;
import com.lkn.algorithm.b_tree.Tree;
import com.lkn.algorithm.b_tree.bean.Element;
import com.lkn.algorithm.b_tree.bean.Node;
import com.lkn.algorithm.index_file.DefaultIndexFileOperation;
import com.lkn.algorithm.index_file.IndexFileOperation;
import com.lkn.algorithm.index_file.IndexHeaderDesc;
import com.lkn.algorithm.index_file.ThreadHelper;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 说明：
 * 1、通过方法addTest()来生成索引文件
 * 2、通过方法readTest()来查询某个元素在索引中的准确位置，如果没有则返回null
 * @author likangning
 * @since 2018/6/29 下午5:43
 */
public class PlusTest extends BtreeTest {

	private IndexFileOperation indexFileOperation = DefaultIndexFileOperation.getSingleInstance();

	@Override
	protected void addElement(int ele) {
		long element = (long) ele;
		try {
			if (root == null) {
				root = new Node(null);
			}
			System.out.println("添加元素 ====> " + element);
			BPlusTreeAdd.add(root, new Element<>(element));
			doWriteFile();
//			PrintTree.print(root);
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
		int begin = 1;
		int end = 1000000;
		for (int i = begin; i < end; i++) {
			addElement(i);
		}
//		PrintTree.print(root);
//		AtomicInteger number = new AtomicInteger();
//		Tree.bfs(root, (node, level) -> {
//			number.incrementAndGet();
//		});
//		System.out.println("总数：" + number.get());
	}


	@Test
	public void readTest() {
		long content = (long) 782335;
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
				if (leftNode.isLeaf()) {
					if (leftNode.getElements().contains(new Element(content))) {
						return leftNode;
					} else {
						return null;
					}
				} else {
					return findNode(leftNode, content);
				}
			}
		}
		Node rightNode = last.getRightNode();
		rightNode = indexFileOperation.read(rightNode.getHardDiskId(), root.getHardDiskId());
		if (rightNode.isLeaf()) {
			if (rightNode.getElements().contains(new Element(content))) {
				return rightNode;
			} else {
				return null;
			}
		} else {
			return findNode(rightNode, content);
		}
	}


}

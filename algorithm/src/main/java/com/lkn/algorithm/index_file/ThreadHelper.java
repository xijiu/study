package com.lkn.algorithm.index_file;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lkn.algorithm.b_tree.bean.Node;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author likangning
 * @since 2018/10/29 下午3:39
 */
public class ThreadHelper {
	private static ThreadLocal<Map<Integer, Node>> SHALL_STORE_HARD_DISK = new ThreadLocal<>();
	private static ThreadLocal<Integer> MAX_NODE_NUMBER = new ThreadLocal<>();

	public static void putNode(Node node) {
		Map<Integer, Node> nodeMap = SHALL_STORE_HARD_DISK.get();
		if (nodeMap == null) {
			nodeMap = Maps.newHashMap();
		}
		nodeMap.put(node.getHardDiskId(), node);
		SHALL_STORE_HARD_DISK.set(nodeMap);
		Integer maxNumber = MAX_NODE_NUMBER.get();
		if (maxNumber == null || maxNumber < node.getHardDiskId()) {
			MAX_NODE_NUMBER.set(node.getHardDiskId());
		}
	}

	public static Collection<Node> getAllNode() {
		return SHALL_STORE_HARD_DISK.get().values();
	}

	public static Integer getMaxNodeNumber() {
		Integer number = MAX_NODE_NUMBER.get();
		return number == null ? -1 : number;
	}

	public static void clear() {
		SHALL_STORE_HARD_DISK.remove();
		MAX_NODE_NUMBER.remove();
	}
}

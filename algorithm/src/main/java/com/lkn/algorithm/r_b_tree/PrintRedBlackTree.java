package com.lkn.algorithm.r_b_tree;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import javafx.util.Pair;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 输出红黑树
 *
 * @author likangning
 * @since 2018/10/16 下午4:50
 */
public class PrintRedBlackTree {

	/** 每个节点输入的宽度 */
	private static int NODE_WIDTH = 5;

	/** 二叉树行距 */
	private static int LINE_SPACING = 5;

	public static <T extends Comparable> void print(Tree<T> tree) {
		if (tree != null && tree.getRoot() != null) {
			print(tree.getRoot());
		}
	}

	public static <T extends Comparable> void print(Node<T> node) {
		Node<T> root = RBTreeHandler.findRoot(node);
		ArrayListMultimap<Integer, Node<T>> nodeLevelMap = splitLevel(root);
		String result = appendContent(nodeLevelMap);
		System.out.println(result);
	}

	private static <T extends Comparable> String appendContent(ArrayListMultimap<Integer, Node<T>> nodeLevelMap) {
		StringBuilder sb = new StringBuilder();
		// 节点及纵向位置的映射关系
		Map<Node, Integer> nodeAndPositionMap = Maps.newHashMap();
		// 找到最大的层数
		int maxLevel = nodeLevelMap.keySet().stream().max(Integer::compareTo).get();
		int totalColSize = (int) Math.pow(2, maxLevel);
		for (Integer key : nodeLevelMap.keySet()) {
			List<Node<T>> nodeList = nodeLevelMap.get(key);
			appendLine(sb, nodeList, key, totalColSize, nodeAndPositionMap);
		}
		return sb.toString();
	}

	private static <T extends Comparable> void appendLine(StringBuilder sb, List<Node<T>> nodes, int currLevel,
																												int totalColSize, Map<Node, Integer> nodeAndPositionMap) {
		Set<PrintNode> set = Sets.newTreeSet();
		List<Pair<Integer, Integer>> pairList = Lists.newArrayList();
		for (Node node : nodes) {
			int position = caclNodeColumnPosition(node, totalColSize, currLevel, nodeAndPositionMap);
			set.add(new PrintNode(node, position));
			nodeAndPositionMap.put(node, position);
			Integer parentPosition = nodeAndPositionMap.get(node.getParent());
			if (parentPosition != null) {
				pairList.add(new Pair<>(parentPosition, position));
			}
		}
		paintLines(sb, pairList, totalColSize);
		doAppendLine(sb, set, totalColSize);
	}

	private static int caclNodeColumnPosition(Node node, int totalColSize, int currLevel,
																						Map<Node, Integer> nodeAndPositionMap) {
		if (node.isRoot()) {
			return totalColSize / 2;
		} else {
			Integer parentLevel = currLevel - 1;
			int diff = totalColSize / (int) Math.pow(2, parentLevel + 1);
			Node.Position position = node.inParentPosition();
			if (position == Node.Position.LEFT) {
				return nodeAndPositionMap.get(node.getParent()) - diff;
			} else if (position == Node.Position.RIGHT) {
				return nodeAndPositionMap.get(node.getParent()) + diff;
			}
			throw new RuntimeException("不应该出现的异常");
		}
	}

	private static void doAppendLine(StringBuilder sb, Set<PrintNode> set, int totalColSize) {
		Map<Integer, PrintNode> map = set.stream().collect(Collectors.toMap(PrintNode::getColumnNum, printNode -> printNode));
		for (int i = 1; i < totalColSize + 1; i++) {
			if (map.containsKey(i)) {
				PrintNode printNode = map.get(i);
				String prefix = printNode.node.getNodeType() == Node.NodeType.BLACK ? "b(" : "r(";
				String nodeTotalContent =  prefix + printNode.node.getData().toString() + ")";
				sb.append(fillWidthContent(nodeTotalContent));
			} else {
				sb.append(emptyContent());
			}
		}
		sb.append("\n");
	}

	private static String fillWidthContent(String nodeContent) {
		if (!Strings.isNullOrEmpty(nodeContent)) {
			int len = nodeContent.length();
			if (len >= NODE_WIDTH) {
				return nodeContent;
			}
			StringBuilder result = new StringBuilder();
			result.append(nodeContent);
			for (int i = 0; i < NODE_WIDTH - len; i++) {
				result.append(" ");
			}
			return result.toString();
		}
		return emptyContent();
	}

	private static void paintLines(StringBuilder sb, List<Pair<Integer, Integer>> linePairList, int totalColSize) {
		if (linePairList.size() == 0) {
			return;
		}
		char[][] linesArr = new char[LINE_SPACING][totalColSize * NODE_WIDTH];
		for (int i = 0; i < linesArr.length; i++) {
			for (int j = 0; j < linesArr[i].length; j++) {
				linesArr[i][j] = ' ';
			}
		}
		for (Pair<Integer, Integer> pair : linePairList) {
			Integer x1 = (pair.getKey() - 1) * NODE_WIDTH;
			Integer x2 = (pair.getValue() - 1) * NODE_WIDTH;
			int peerLineSpace = (x2 - x1) / LINE_SPACING;
			for (int i = 0; i < LINE_SPACING; i++) {
				x1 = x1 + peerLineSpace;
				linesArr[i][x1] = '-';
			}
		}
		for (char[] aLinesArr : linesArr) {
			for (char anALinesArr : aLinesArr) {
				sb.append(anALinesArr);
			}
			sb.append("\n");
		}
	}

	private static String emptyContent() {
		StringBuilder empty = new StringBuilder();
		for (int j = 0; j < NODE_WIDTH; j++) {
			empty.append(" ");
		}
		return empty.toString();
	}

	private static <T extends Comparable> ArrayListMultimap<Integer, Node<T>> splitLevel(Node<T> root) {
		ArrayListMultimap<Integer, Node<T>> map = ArrayListMultimap.create();
		RBTreeHandler.bfs(root, map::put);
		return map;
	}

	private static class PrintNode implements Comparable<PrintNode> {
		private Node node;
		/** 列编号 */
		@Getter
		private int columnNum;

		private PrintNode(Node node, int columnNum) {
			this.node = node;
			this.columnNum = columnNum;
		}

		@Override
		public int compareTo(PrintNode o) {
			return Integer.compare(o.columnNum, this.columnNum);
		}
	}
}

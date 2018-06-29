package com.lkn.algorithm.b_tree;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.lkn.algorithm.b_tree.base.Node;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author likangning
 * @since 2018/6/25 下午5:17
 */
public class PrintTree {
	private static final int ORDER_NUM = Node.ORDER_NUM;
	private static final int NODE_LENGTH = ORDER_NUM - 1 + ORDER_NUM - 2 + 2 + ORDER_NUM - 1;
	// 节点的水平间隔
	private static final int HORIZONTALLY = 2;
	// 节点的纵向间隔
	private static final int VERTICALLY = 8;

	private static final String VERTICALLY_STR;

	private static final String HORIZONTALLY_STR;

	static {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < VERTICALLY; i++) {
			sb.append(" ");
		}
		VERTICALLY_STR = sb.toString();

		sb = new StringBuilder();
		for (int i = 0; i < HORIZONTALLY; i++) {
			sb.append(" ");
		}
		HORIZONTALLY_STR = sb.toString();
	}

	/**
	 * 打印一颗B树
	 * @param node B树中的任意一个节点
	 */
	public static void print(Node node) {
		while (node.getParent() != null) {
			node = node.getParent();
		}
		printFromRoot(node);
	}

	private static void printFromRoot(Node root) {
		Multimap<Integer, PrintNode> map = ArrayListMultimap.create();
		Map<Integer, AtomicInteger> counter = Maps.newHashMap();
		Tree.bfs(root, (node, level) -> map.put(level, new PrintNode(node, level, counter)));
		print(map);
	}

	private static void print(Multimap<Integer, PrintNode> map) {
		// 向控制台输出的总list，需倒序输出
		List<String> totalPrintList = Lists.newArrayList();
		int levelNum = map.keySet().size();
		for (int i = levelNum - 1; i > 0; i--) {
			// 叶子节点，需要特殊处理
			if (i == levelNum - 1) {
				StringBuilder sb = new StringBuilder();
				Collection<PrintNode> nodes = map.get(i);
				// 将当前层级的所有节点当做字符串拼接起来，位数不够的用空格填充
				for (PrintNode printNode : nodes) {
					String nodeStr = fillBlank(printNode.node.toString());
					sb.append(nodeStr).append(VERTICALLY_STR);
				}
				totalPrintList.add(sb.toString());
			}

			appendFatherLevelNodes(map, i, totalPrintList);
		}
		// 只有根节点的情况
		if (totalPrintList.size() == 0) {
			Collection<PrintNode> printNodes = map.get(0);
			for (PrintNode printNode : printNodes) {
				totalPrintList.add(printNode.node.toString());
			}
		}
		printResult(totalPrintList);
	}

	/**
	 * 输出最终结果
	 * @param totalPrintList
	 */
	private static void printResult(List<String> totalPrintList) {
		for (int i = totalPrintList.size() - 1; i >= 0; i--) {
			System.out.println(totalPrintList.get(i));
		}
		System.out.println();
		System.out.println();
	}

	private static String appendLinesToString(List<List<Character>> lists) {
		StringBuilder sb = new StringBuilder();
		for (List<Character> line : lists) {
			for (Character c : line) {
				sb.append(c);
			}
			sb.append("\n");
		}
		// 将最后一个回车符号去掉
		sb = sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	/**
	 * 拼接父节点层级的所有节点，同事将父节点与子节点的连线画出
	 * @param map
	 * @param childrenLevel
	 * @return
	 */
	private static void appendFatherLevelNodes(Multimap<Integer, PrintNode> map, int childrenLevel,
																						 List<String> totalPrintList) {
		List<List<Character>> lineList = initLineList();
		StringBuilder sb = new StringBuilder();
		Collection<PrintNode> nodes = map.get(childrenLevel - 1);
		Collection<PrintNode> children = map.get(childrenLevel);
		// 遍历父节点们
		for (PrintNode printNode : nodes) {
			// 计算当前父节点的坐标位置
			Position position = calcParentPosition(printNode.node, children);
			printNode.position = position.parentPosition;
			fillShowList(position, lineList);
			int currLen = sb.length();
			for (int i = currLen; i < position.getParentPosition(); i++) {
				sb.append(" ");
			}
			String nodeStr = printNode.node.toString();
			nodeStr = fillBlank(nodeStr);
			sb.append(nodeStr).append(VERTICALLY_STR);
		}
		String lineString = appendLinesToString(lineList);
		// 组装最终的结果
		totalPrintList.add(lineString);
		totalPrintList.add(sb.toString());
	}

	/**
	 * 初始化画线的列表
	 */
	private static List<List<Character>> initLineList() {
		List<List<Character>> list = Lists.newArrayListWithExpectedSize(VERTICALLY);
		for (int i = 0; i < VERTICALLY; i++) {
			list.add(new ArrayList<>());
		}
		return list;
	}

	private static void fillShowList(Position position, List<List<Character>> lineList) {
		int parentPosition = position.getParentPosition();
		List<Integer> childrenPositions = position.getChildrenPositionList();
		for (Integer childPosition : childrenPositions) {
			calcTwoPoint(parentPosition, childPosition, lineList);
		}
	}

	private static void calcTwoPoint(int parentPosition, int childPosition, List<List<Character>> lineList) {
		int differ = childPosition - parentPosition;
		for (int i = 0; i < VERTICALLY; i++) {
			int currIndex = parentPosition + (new BigDecimal(differ).divide(new BigDecimal(VERTICALLY))
					.multiply(new BigDecimal(i + 1)).intValue());
			List<Character> list = lineList.get(i);
			addElement(list, '-', currIndex);
		}
	}

	private static void addElement(List<Character> list, Character element, int index) {
		int originSize = list.size();
		if (index >= list.size()) {
			for (int i = originSize; i <= index; i++) {
				list.add(' ');
			}
		}
		list.remove(index);
		list.add(index, element);
	}

	private static Position calcParentPosition(Node node, Collection<PrintNode> children) {
		if (isLeafNode(children)) {
			return calcParentPositionForLeafNode(node, children);
		} else {
			Position position = new Position();
			int minPosition = Integer.MAX_VALUE;
			int maxPosition = Integer.MIN_VALUE;
			for (PrintNode printNode : children) {
				if (printNode.node.getParent() == node) {
					minPosition = printNode.position < minPosition ? printNode.position : minPosition;
					maxPosition = printNode.position > maxPosition ? printNode.position : maxPosition;
					position.getChildrenPositionList().add(printNode.position);
				}
			}
			position.setParentPosition((maxPosition - minPosition) / 2 + minPosition);
			return position;
		}

	}

	/**
	 * 为叶子节点计算其父节点的坐标
	 * @param node
	 * @param children
	 * @return
	 */
	private static Position calcParentPositionForLeafNode(Node node, Collection<PrintNode> children) {
		Position position = new Position();
		int beginIndex = Integer.MAX_VALUE;
		int endIndex = Integer.MIN_VALUE;
		for (PrintNode child : children) {
			if (child.node.getParent() == node) {
				beginIndex = child.index < beginIndex ? child.index : beginIndex;
				endIndex = child.index > endIndex ? child.index : endIndex;
				position.getChildrenPositionList().add(child.index * (NODE_LENGTH + VERTICALLY));
			}
		}
		BigDecimal target = new BigDecimal(endIndex).subtract(new BigDecimal(beginIndex))
				.divide(new BigDecimal(2)).add(new BigDecimal(beginIndex))
				.multiply(new BigDecimal(NODE_LENGTH + VERTICALLY));
		position.setParentPosition(target.intValue());
		return position;
	}

	/**
	 * 是否是叶子节点
	 * @param children
	 * @return
	 */
	private static boolean isLeafNode(Collection<PrintNode> children) {
		for (PrintNode printNode : children) {
			if (printNode.position == -1) {
				return true;
			}
		}
		return false;
	}

	private static class Position {
		@Getter
		@Setter
		private int parentPosition;
		@Getter
		private List<Integer> childrenPositionList = Lists.newArrayList();
	}

	/**
	 * 如果节点的长度不够，那么用空格补充
	 * @param origin	原生的node字符
	 * @return	补充了空格的字符
	 */
	private static String fillBlank(String origin) {
		int originLength = origin.length();
		if (originLength < NODE_LENGTH) {
			for (int i = 0; i < NODE_LENGTH - originLength; i++) {
				origin = origin + " ";
			}
		}
		return origin;
	}

	private static class PrintNode {
		private Node node;
		private int level;
		private int index;
		// 在画图中该节点所在的横向位置，默认是-1
		private int position = -1;

		public PrintNode(Node node, int level, Map<Integer, AtomicInteger> counter) {
			this.node = node;
			this.level = level;
			AtomicInteger atomicInteger = counter.get(level);
			if (atomicInteger == null) {
				atomicInteger = new AtomicInteger(0);
				counter.put(level, atomicInteger);
			}
			this.index = atomicInteger.getAndIncrement();
		}
	}
}

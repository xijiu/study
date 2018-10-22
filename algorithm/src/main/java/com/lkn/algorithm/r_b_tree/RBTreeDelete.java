package com.lkn.algorithm.r_b_tree;

import com.google.common.base.Objects;

/**
 * 红黑树的删除
 * 参考文章：https://blog.csdn.net/goodluckwhh/article/details/12718233
 *
 * 1：N为根节点
 * 		这种情形非常简单，直接将其修改为黑色即可。因为如果N原来是黑色，则这样做不会改变其性质，如果原来为红色，我们需要保持红黑树的性质，
 * 		也要把它修改为黑色
 * 2：N节点为红色
 *		空节点不可能是红色的，而按照我们删除时所采取的算法，此时的N必定为被删除的黑色节点的唯一非空子节点，并且在删除后它替代了被删除节
 *		点的位置，那么要恢复红黑树的性质就非常简单，只要将其颜色修改为黑色即可。
 * 3：N、S、SL、SR、P都为黑色
 * 		此时的做法是将S的颜色修改为红色，则通过S和通过P的路径上的黑色节点数目变得相同了。
 * 		但是通过N的路径比不通过N的路径都少了一个黑色节点，因而需要将P节点看做N节点重新执行调整算法。
 * 4：N、S、SL、SR都为黑色，P为红色
 * 		此时的处理很简单，只需要将S和P的颜色互换即可完成调整。
 * 5：N是黑色，S是黑色，SR是红色，P与SL是什么颜色无所谓（考虑N为P的左孩子的情形，N为P的右孩子的情形与之对称）
 * 			a 将以P为根的子树进行左旋
 * 		  b 交换P和S的颜色
 * 		  c 将SR的颜色改为黑色
 * 6：N是黑色，S是黑色，SL是红色，SR是黑色，P是什么颜色无所谓（考虑N为P的左孩子的情形，N为P的右孩子的情形与之对称）
 * 			a 首先对以S为根节点的子树进行右旋
 * 			b 然后交换S和SL的颜色，即S改为黑色，SL改为红色
 * 		  c 变为情况5，故再需要一次情况5的调整
 * 7：N、SL、SR、P都为黑色，S为红色
 * 			a 首先对以P为根节点的子树进行左旋
 * 		  b 然后再将S和P的颜色互换，即修改S的颜色为黑色，P的颜色为红色
 * 		  c 这就把该情形转化成了N为黑色，其兄弟为黑色的情形，再根据具体的情形参考情形3,4,5,6进行处理即可
 * @author likangning
 * @since 2018/10/22 上午9:11
 */
public class RBTreeDelete {
	/**
	 * 红黑树的删除操作
	 * 分为两步：
	 * 	1、执行删除
	 * 	2、红黑树调整
	 *
	 */
	public static <T extends Comparable> void delete(Node<T> delNode) {
		// 如果只剩单个根节点，那么将其内容设置为null
		if (delNode.isRoot() && delNode.getLeftChild() == null && delNode.getRightChild() == null) {
			delNode.setData(null);
			return;
		}
		Node<T>[] arr = doDelete(delNode);
		Node<T> assistNode = arr[0];
		Node<T> targetNode = arr[1];
		Node<T> parentNode = arr[2];
		adjustTree(assistNode, targetNode, parentNode);
	}

	/**
	 * 红黑树删除节点后的调整
	 * 1、n为根节点
	 * 2、n为红色节点
	 *
	 * @param <T>	泛型
	 * @param assistNode 辅助节点
	 * @param n 目标节点
	 */
	private static <T extends Comparable> void adjustTree(Node<T> assistNode, Node<T> n, Node<T> p) {
		// 如果辅助节点是红色的，那么不需要调整
		if (getType(assistNode) == Node.NodeType.RED) {
			return;
		}
		// n节点的兄弟节点
		Node<T> s = n == null ? (p.getLeftChild() == null ? p.getRightChild() : p.getLeftChild()) : n.getBrother();
		// 兄弟节点的左孩子节点
		Node<T> sl = s == null ? null : s.getLeftChild();
		// 兄弟节点的右孩子节点
		Node<T> sr = s == null ? null : s.getRightChild();
		doAdjust(n, s, sl, sr, p);
	}

	/**
	 * 执行调整
	 */
	private static <T extends Comparable> boolean doAdjust(Node<T> n, Node<T> s, Node<T> sl, Node<T> sr, Node<T> p) {
		if (!condition1(n)) {
			if (!condition2(n)) {
				if (!condition3(n, s, sl, sr, p)) {
					if (!condition4(n, s, sl, sr, p)) {
						if (!condition5(n, s, sl, sr, p)) {
							if (!condition6(n, s, sl, sr, p)) {
								if (!condition7(n, s, sl, sr, p)) {
									throw new RuntimeException("红黑树结构异常");
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 情况7：N是黑色，S为红色（考虑N为P的左孩子的情形，N为P的右孩子的情形与之对称）
	 */
	private static <T extends Comparable> boolean condition7(Node<T> n, Node<T> s, Node<T> sl, Node<T> sr, Node<T> p) {
		if (getNodePosition(n, p) == Node.Position.LEFT && getType(n) == Node.NodeType.BLACK
				&& getType(s) == Node.NodeType.RED && getType(sl) == Node.NodeType.BLACK
				&& getType(sr) == Node.NodeType.BLACK) {
			RBTreeHandler.leftRotate(p);
			changeNodeType(s, p);
			return doAdjust(n, s, sl, sr, p);
		} else if (getNodePosition(n, p) == Node.Position.RIGHT && getType(n) == Node.NodeType.BLACK
				&& getType(s) == Node.NodeType.RED && getType(sl) == Node.NodeType.BLACK
				&& getType(sr) == Node.NodeType.BLACK) {
			RBTreeHandler.rightRotate(p);
			changeNodeType(s, p);
			return doAdjust(n, s, sl, sr, p);
		}
		return false;
	}

	/**
	 * 情况6：N是黑色，S是黑色，SL是红色，SR是黑色（考虑N为P的左孩子的情形，N为P的右孩子的情形与之对称）
	 */
	private static <T extends Comparable> boolean condition6(Node<T> n, Node<T> s, Node<T> sl, Node<T> sr, Node<T> p) {
		if (getNodePosition(n, p) == Node.Position.LEFT && getType(n) == Node.NodeType.BLACK
				&& getType(s) == Node.NodeType.BLACK && getType(sl) == Node.NodeType.RED
				&& getType(sr) == Node.NodeType.BLACK) {
			RBTreeHandler.rightRotate(s);
			changeNodeType(s, sl);
			// 已转换成condition5的情况
			s = sl;
			sl = s.getLeftChild();
			sr = s.getRightChild();
			return condition5(n, s, sl, sr, p);
		} else if (getNodePosition(n, p) == Node.Position.RIGHT && getType(n) == Node.NodeType.BLACK
				&& getType(s) == Node.NodeType.BLACK && getType(sr) == Node.NodeType.RED
				&& getType(sl) == Node.NodeType.BLACK) {
			RBTreeHandler.leftRotate(s);
			changeNodeType(s, sr);
			// 已转换成condition5的情况
			s = sr;
			sl = s.getLeftChild();
			sr = s.getRightChild();
			return condition5(n, s, sl, sr, p);
		}
		return false;
	}

	/**
	 * 情况5：N是黑色，S是黑色，SR是红色（考虑N为P的左孩子的情形，N为P的右孩子的情形与之对称）
	 * 		将以P为根的子树进行左旋
	 * 	 	交换P和S的颜色
	 * 	 	将SR的颜色改为黑色
	 */
	private static <T extends Comparable> boolean condition5(Node<T> n, Node<T> s, Node<T> sl, Node<T> sr, Node<T> p) {
		if (getNodePosition(n, p) == Node.Position.LEFT && getType(n) == Node.NodeType.BLACK
				&& getType(s) == Node.NodeType.BLACK && getType(sr) == Node.NodeType.RED) {
			RBTreeHandler.leftRotate(p);
			// 交换p s节点的颜色
			changeNodeType(p, s);
			if (sr != null) {
				sr.setNodeType(Node.NodeType.BLACK);
			}
			return true;
		} else if (getNodePosition(n, p) == Node.Position.RIGHT && getType(n) == Node.NodeType.BLACK
				&& getType(s) == Node.NodeType.BLACK && getType(sl) == Node.NodeType.RED) {
			RBTreeHandler.rightRotate(p);
			// 交换p s节点的颜色
			changeNodeType(p, s);
			if (sl != null) {
				sl.setNodeType(Node.NodeType.BLACK);
			}
			return true;
		}
		return false;
	}

	/**
	 * 情景4：父节点为红色，其余为黑色
	 * s、p 颜色互换
	 */
	private static <T extends Comparable> boolean condition4(Node<T> n, Node<T> s, Node<T> sl, Node<T> sr, Node<T> p) {
		if (getType(n) == Node.NodeType.BLACK && getType(s) == Node.NodeType.BLACK
				&& getType(sl) == Node.NodeType.BLACK && getType(sr) == Node.NodeType.BLACK
				&& getType(p) == Node.NodeType.RED) {
			s.setNodeType(Node.NodeType.RED);
			p.setNodeType(Node.NodeType.BLACK);
			return true;
		}
		return false;
	}

	/**
	 * 情况3：5个节点都为黑色
	 * 将兄弟节点染红后，将p节点作为n节点重新执行调整逻辑
	 */
	private static <T extends Comparable> boolean condition3(Node<T> n, Node<T> s, Node<T> sl, Node<T> sr, Node<T> p) {
		if (getType(n) == Node.NodeType.BLACK && getType(s) == Node.NodeType.BLACK
				&& getType(sl) == Node.NodeType.BLACK && getType(sr) == Node.NodeType.BLACK
				&& getType(p) == Node.NodeType.BLACK) {
			s.setNodeType(Node.NodeType.RED);
			n = p;
			s = n.getBrother();
			sl = s == null ? null : s.getLeftChild();
			sr = s == null ? null : s.getRightChild();
			p = n.getParent();
			return doAdjust(n, s, sl, sr, p);
		}
		return false;
	}

	/**
	 * 情况2：如果n节点为红色，直接染黑
	 */
	private static <T extends Comparable> boolean condition2(Node<T> n) {
		// n节点为红色，那么直接染黑
		if (getType(n) == Node.NodeType.RED) {
			n.setNodeType(Node.NodeType.BLACK);
			return true;
		}
		return false;
	}

	/**
	 * 情况1：如果n节点时根节点，那么染黑
	 * @return 	true 已经调整
	 * 					false 未调整
	 */
	private static <T extends Comparable> boolean condition1(Node<T> n) {
		if (n != null && n.isRoot()) {
			n.setNodeType(Node.NodeType.BLACK);
			return true;
		}
		return false;
	}

	/**
	 * 获取节点的位置
	 * @param node	目标节点
	 * @param parent	目标节点的父节点
	 * @param <T>	泛型
	 * @return	节点位置
	 */
	private static <T extends Comparable> Node.Position getNodePosition(Node<T> node, Node<T> parent) {
		if (node != null) {
			return node.inParentPosition();
		} else {
			if (parent.getLeftChild() != null) {
				return Node.Position.RIGHT;
			} else if (parent.getRightChild() != null) {
				return Node.Position.LEFT;
			} else {
				return null;
			}
		}
	}

	/**
	 * 交换两个节点的颜色
	 * @param node1	节点1
	 * @param node2	节点2
	 * @param <T>	泛型
	 */
	private static <T extends Comparable> void changeNodeType(Node<T> node1, Node<T> node2) {
		Node.NodeType type1 = getType(node1);
		Node.NodeType type2 = getType(node2);
		if (node1 != null) {
			node1.setNodeType(type2);
		}
		if (node2 != null) {
			node2.setNodeType(type1);
		}
	}

	/**
	 * 获取节点的类型
	 * @param node	目标节点
	 * @param <T>	泛型
	 * @return	节点的颜色
	 */
	private static <T extends Comparable> Node.NodeType getType(Node<T> node) {
		if (node == null) {
			return Node.NodeType.BLACK;
		} else {
			return node.getNodeType();
		}
	}

	/**
	 * 删除分为3种情况：
	 * 1、左右子节点都为null
	 * 2、左右子节点有一个为null
	 * 3、左右子节点都不为null
	 *
	 * @param <T>	泛型
	 * @param delNode  将要删除的节点
	 * @return 删除节点的辅助节点
	 */
	private static <T extends Comparable> Node[] doDelete(Node<T> delNode) {
		// 替换节点内容
		Node<T> assistNode = replaceNodeData(delNode);
		Node<T> parent;
		if (Objects.equal(assistNode, delNode)) {
			parent = delNode.getParent();
		} else {
			parent = delNode;
		}
		// 辅助节点的第一个子节点
		Node<T> target = assistNode.getLeftChild() == null ? assistNode.getRightChild() : assistNode.getLeftChild();
		// 删除辅助节点
		nodeAwayFromTree(assistNode);
		return new Node[]{assistNode, target, parent};
	}

	/**
	 * 只替换节点的内容，不替换颜色
	 * @param delNode	要删除的节点
	 * @param <T>	泛型
	 * @return 辅助节点
	 */
	private static <T extends Comparable> Node<T> replaceNodeData(Node<T> delNode) {
		Node<T> leftChild = delNode.getLeftChild();
		Node<T> rightChild = delNode.getRightChild();
		if (leftChild == null && rightChild == null) {
			// 左右子节点都为null
			return delNode;
		} else if (leftChild != null && rightChild != null) {
			// 左右子节点都不为null
			// 找到删除节点的前驱节点
			Node<T> precursorNode = findPrecursor(delNode);
			delNode.setData(precursorNode.getData());
			return precursorNode;
		} else {
			// 左右子节点有一个为null
			if (leftChild != null) {
				delNode.setData(leftChild.getData());
				return leftChild;
			} else {
				delNode.setData(rightChild.getData());
				return rightChild;
			}
		}
	}

	/**
	 * 找到当前节点的前驱节点
	 * @param targetNode	当前节点
	 * @param <T>	泛型
	 * @return	前驱节点
	 */
	private static <T extends Comparable> Node<T> findPrecursor(Node<T> targetNode) {
		Node<T> leftChild = targetNode.getLeftChild();
		while (leftChild.getRightChild() != null) {
			leftChild = leftChild.getRightChild();
		}
		return leftChild;
	}


	private static <T extends Comparable> void nodeAwayFromTree(Node<T> delNode) {
		Node<T> parent = delNode.getParent();
		Node<T> leftChild = delNode.getLeftChild();
		Node<T> rightChild = delNode.getRightChild();
		if (parent != null) {
			// 要删除的节点一定只有一个唯一的儿子
			Node<T> uniqueSon = null;
			if (leftChild == null && rightChild != null) {
				uniqueSon = delNode.getRightChild();
			} else if (rightChild == null && leftChild != null) {
				uniqueSon = delNode.getLeftChild();
			}
			// 删除当前节点，并将其唯一的儿子作为其替代
			Node.Position position = delNode.inParentPosition();
			if (position == Node.Position.LEFT) {
				parent.setLeftChild(uniqueSon);
			} else if (position == Node.Position.RIGHT) {
				parent.setRightChild(uniqueSon);
			}
		}
	}
}

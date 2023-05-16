package com.lkn.leetcode.algorithm;

/**
 * 耗时2ms，最终击败100%用户，结果比较满意哈
 *
 * @author likangning
 * @since 2020/4/14 下午2:22
 */
public class LeetCode_445 {
	public static void main(String[] args) {
		int[] first = {9};
		int[] second = {1,9,9,9,9,9,9,9,9,9};
//		int[] first =  {   8, 6};
//		int[] second = {6, 4, 8};

		ListNode l1 = null;
		ListNode l2 = null;
		ListNode l1Last = null;
		for (int i = 0; i < first.length; i++) {
			ListNode listNode = new ListNode(first[i]);
			l1 = l1 == null ? listNode : l1;
			if (l1Last != null) {
				l1Last.next = listNode;
			}
			l1Last = listNode;
		}

		ListNode l2Last = null;
		for (int i = 0; i < second.length; i++) {
			ListNode listNode = new ListNode(second[i]);
			l2 = l2 == null ? listNode : l2;
			if (l2Last != null) {
				l2Last.next = listNode;
			}
			l2Last = listNode;
		}

		Solution solution = new Solution();
		ListNode listNode = solution.addTwoNumbers(l1, l2);
		while (true) {
			System.out.println(listNode.val);
			if (listNode.next == null) {
				break;
			}
			listNode = listNode.next;
		}
	}

}

class Solution {
	public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
		int firstLen = 0;
		ListNode node = l1;
		while (node.next != null) {
			node = node.next;
			firstLen++;
		}
		int secondLen = 0;
		node = l2;
		while (node.next != null) {
			node = node.next;
			secondLen++;
		}
		if (firstLen == secondLen) {
			boolean plusOne = myTest(l1, l2);
			if (plusOne) {
				l2.val = 1;
				l2.next = l1;
				return l2;
			} else {
				return l1;
			}
		} else if (firstLen > secondLen) {
			int diff = firstLen - secondLen;
			ListNode tmpNode = l1;
			ListNode firstNot9Node = null;
			for (int i = 0; i < diff; i++) {
				firstNot9Node = tmpNode.val != 9 ? tmpNode : firstNot9Node;
				tmpNode = tmpNode.next;
			}
			boolean plusOne = myTest(tmpNode, l2);
			if (plusOne) {
				if (firstNot9Node == null) {
					ListNode tmpNode2 = l1;
					while (true) {
						if (tmpNode2 == tmpNode) {
							break;
						}
						tmpNode2.val = 0;
						tmpNode2 = tmpNode2.next;
					}
					l2.val = 1;
					l2.next = l1;
					return l2;
				} else {
					firstNot9Node.val = firstNot9Node.val + 1;
					ListNode tmpNode2 = firstNot9Node.next;
					while (true) {
						if (tmpNode2 == tmpNode) {
							break;
						} else {
							tmpNode2.val = 0;
							tmpNode2 = tmpNode2.next;
						}
					}
					return l1;
				}
			} else {
				return l1;
			}
		} else {
			int diff = secondLen - firstLen;
			ListNode tmpNode = l2;
			ListNode firstNot9Node = null;
			for (int i = 0; i < diff; i++) {
				firstNot9Node = tmpNode.val != 9 ? tmpNode : firstNot9Node;
				tmpNode = tmpNode.next;
			}
			boolean plusOne = myTest(l1, tmpNode);
			if (plusOne) {
				if (firstNot9Node == null) {
					ListNode tmpNode2 = l2;
					while (true) {
						if (tmpNode2 == tmpNode) {
							break;
						}
						if (tmpNode2.next == tmpNode) {
							tmpNode2.next = l1;
							tmpNode2.val = 0;
							break;
						}
						tmpNode2.val = 0;
						tmpNode2 = tmpNode2.next;
					}
					ListNode listNode = new ListNode(1);
					listNode.next = l2;
					return listNode;
				} else {
					firstNot9Node.val = firstNot9Node.val + 1;
					ListNode tmpNode2 = firstNot9Node;
//					if (firstNot9Node.next == tmpNode) {
//						firstNot9Node.next = l1;
//					}
					while (true) {
						if (tmpNode2.next == tmpNode) {
							tmpNode2.val = tmpNode2 == firstNot9Node ? tmpNode2.val : 0;
							tmpNode2.next = l1;
							break;
						} else if (tmpNode2 == firstNot9Node) {
							tmpNode2 = tmpNode2.next;
						} else {
							tmpNode2.val = 0;
							tmpNode2 = tmpNode2.next;
						}
					}
					return l2;
				}
			} else {
				ListNode tmpNode3 = l2;
				for (int i = 0; i < diff; i++) {
					if (i == diff - 1) {
						tmpNode3.next = l1;
					}
					tmpNode3 = tmpNode3.next;
				}
				return l2;
			}
		}
	}

	private boolean myTest(ListNode l1, ListNode l2) {
		if (l1.next != null) {
			boolean plusOne = myTest(l1.next, l2.next);
			int sum = l1.val + l2.val;
			sum = plusOne ? sum + 1 : sum;
			l1.val = sum >= 10 ? sum - 10 : sum;
			return sum >= 10;
		} else {
			int sum = l1.val + l2.val;
			l1.val = sum >= 10 ? sum - 10 : sum;
			return sum >= 10;
		}
	}
}

class ListNode {
	int val;
	ListNode next;

	ListNode(int x) {
		val = x;
	}

	ListNode(int x, ListNode next) {
		val = x;
		this.next = next;
	}
}

package com.lkn.leetcode.algorithm;

import org.junit.Test;


public class LeetCode_25 {

	private ListNode newRoot = null;

	@Test
	public void aaa() {
		Solution solution = new Solution();
		ListNode node5 = new ListNode(5, null);
		ListNode node4 = new ListNode(4, node5);
		ListNode node3 = new ListNode(3, node4);
		ListNode node2 = new ListNode(2, node3);
		ListNode node1 = new ListNode(1, node2);

		ListNode listNode = solution.reverseKGroup(node1, 2);
		printList(listNode);
	}

	private void printList(ListNode listNode) {
		while (listNode != null) {
			System.out.print(listNode.val + ", ");
			listNode = listNode.next;
		}
	}

	class Solution {

		public ListNode reverseKGroup(ListNode head, int k) {
			ListNode curr = head;
			ListNode prev = null;
			ListNode lastK_last = head;

			int index = 0;
			while (true) {
				ListNode[] arr = tryReverseK(curr, k, prev);
				if (arr == null) {
					if (lastK_last != prev) {
						lastK_last.next = prev;
						lastK_last = prev;
					}
					break;
				}
				prev = arr[0];
				curr = arr[1];
				if (lastK_last != prev) {
					lastK_last.next = prev;
					lastK_last = prev;
				}
				index++;
			}
			return null;
		}

		private ListNode[] tryReverseK(ListNode curr, int k, ListNode prev) {
			ListNode tmpCurr = curr;

			int num = 0;
			while (true) {
				if (curr.next == null) {
					curr.next = prev;
					break;
				}
				if (k >= num) {
					break;
				}
				ListNode next = curr.next;
				curr.next = prev;
				prev = curr;
				curr = next;
				num++;
			}
			if (num < k) {
				return null;
			}

			return new ListNode[] {tmpCurr, curr};

		}
	}
}

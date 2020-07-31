package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * @author likangning
 * @since 2020/7/31 上午10:42
 */
public class LeetCode_19 {

	@Test
	public void test() {
		ListNode listNode1 = new ListNode(1);
		ListNode listNode2 = new ListNode(2);
		ListNode listNode3 = new ListNode(3);
		ListNode listNode4 = new ListNode(4);
		ListNode listNode5 = new ListNode(5);
		listNode5.next = null;
		listNode1.next = listNode2;
		listNode2.next = listNode3;
		listNode3.next = listNode4;
		listNode4.next = listNode5;
		LeetCode_19.ListNode result = new LeetCode_19.Solution().removeNthFromEnd(listNode1, 2);
		while (true) {
			System.out.println(result.val);
			if (result.next == null) {
				break;
			} else {
				result = result.next;
			}
		}
	}

	class Solution {
		public ListNode removeNthFromEnd(ListNode head, int n) {
			ListNode node = head;
			int arrSize = n + 1;
			ListNode[] arr = new ListNode[arrSize];
			int number = 0;

			while (true) {
				arr[number++ % arrSize] = node;
				if (node.next == null) {
					break;
				}
				node = node.next;
			}

			if (number == n) {
				return head.next;
			} else {
				ListNode deleteNode = arr[(number - n) % arrSize];
				ListNode previousNode = arr[(number - 1 - n) % arrSize];
				previousNode.next = deleteNode.next;
				return head;
			}
		}
	}

	public class ListNode {
		int val;
		ListNode next;
		ListNode(int x) {
			val = x;
		}
	}
}

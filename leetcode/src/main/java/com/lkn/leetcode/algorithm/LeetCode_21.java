package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.util.LinkedList;

/**
 *  '('，')'，'{'，'}'，'['，']'
 */
public class LeetCode_21 {

	@Test
	public void test() {
	}


	class Solution {
		public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
			ListNode head = null;
			ListNode tmp = null;
			ListNode tmpLast = null;
			while (true) {
				if (list1 != null && list2 != null) {
					if (list1.val < list2.val) {
						tmp = list1;
						list1 = list1.next;
					} else {
						tmp = list2;
						list2 = list2.next;
					}
				} else if (list1 != null && list2 == null) {
					tmp = list1;
					list1 = list1.next;
				} else if (list1 == null && list2 != null) {
					tmp = list2;
					list2 = list2.next;
				} else {
					break;
				}

				if (head == null) {
					head = tmp;
				}

				if (tmpLast != null) {
					tmpLast.next = tmp;
				}
				tmpLast = tmp;
			}
			return head;
		}
	}
}

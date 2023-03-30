package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 中间部分的链表反转
 *
 * @author likangning
 */
public class LeetCode_92 {

	@Test
	public void test() {

	}

	/**
	 * Definition for singly-linked list.
	 * public class ListNode {
	 *     int val;
	 *     ListNode next;
	 *     ListNode() {}
	 *     ListNode(int val) { this.val = val; }
	 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
	 * }
	 */
	public ListNode reverseBetween(ListNode head, int left, int right) {
		int i = 1;
		ListNode prev = null;
		ListNode curr = head;
		ListNode littleHead = null;
		ListNode littleTail = null;
		ListNode wBegin = null;
		ListNode wEnd = null;
		while (true) {
			if (i == right + 1) {
				wEnd = curr;
			}

			if (curr == null || i > right) {
				break;
			}

			ListNode next = curr.next;


			if (i >= left && i <= right) {
				curr.next = prev;
			}
			if (i == left) {
				littleTail = curr;
			}
			if (i == right) {
				littleHead = curr;
			}

			if (i == left - 1) {
				wBegin = curr;
			}

			prev = curr;
			curr = next;
			i++;
		}

		if (wBegin != null) {
			wBegin.next = littleHead;
		}
		littleTail.next = wEnd;

		if (left > 1) {
			return head;
		} else {
			return littleHead;
		}

	}

	public class ListNode {
		int val;
		ListNode next;
		ListNode() {}
		ListNode(int val) { this.val = val; }
		ListNode(int val, ListNode next) { this.val = val; this.next = next; }
	}



}

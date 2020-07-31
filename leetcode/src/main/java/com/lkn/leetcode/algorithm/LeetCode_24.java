package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author likangning
 * @since 2020/4/26 上午11:13
 */
public class LeetCode_24 {

	/**
	 * [2,5,3,4,6,2,2]
	 * [5,2,2,6,2]
	 * [5,2,4,3,2,6,2]
	 */
	@Test
	public void aaa() {
		Solution solution = new Solution();
		solution.swapPairs(null);
	}

	class Solution {
		private ListNode previous = null;
		private	ListNode resultHead = null;
		public ListNode swapPairs(ListNode head) {
			if (head == null) {
				return resultHead;
			}
			ListNode first = head;
			ListNode second = head.next;
			if (second == null) {
				return resultHead == null ? head : resultHead;
			}
			if (previous == null) {
				first.next = second.next;
				second.next = first;
				previous = first;
				resultHead = second;
			} else {
				previous.next = second;
				first.next = second.next;
				second.next = first;
				previous = first;
			}
			return swapPairs(first.next);
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

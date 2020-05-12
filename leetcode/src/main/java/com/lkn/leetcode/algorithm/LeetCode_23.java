package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author likangning
 * @since 2020/4/26 上午11:13
 */
public class LeetCode_23 {

	@Test
	public void aaa() {
		Solution solution = new Solution();
		ListNode[] lists = new ListNode[3];
		ListNode node1 = new ListNode(1);
		ListNode node11 = new ListNode(4);
		ListNode node111 = new ListNode(5);
		node1.next = node11;
		node11.next = node111;
		lists[0] = node1;

		ListNode node2 = new ListNode(1);
		ListNode node22 = new ListNode(3);
		ListNode node222 = new ListNode(4);
		node2.next = node22;
		node22.next = node222;
		lists[1] = node2;

		ListNode node3 = new ListNode(2);
		ListNode node33 = new ListNode(6);
		node3.next = node33;
		lists[2] = node3;

		ListNode result = solution.mergeKLists(lists);
		while (result != null) {
			System.out.print(result.val + "、");
			result = result.next;
		}
	}
	/**
	 * [
	 * 1->4->5,
	 * 1->3->4,
	 * 2->6
	 * ]
	 * 输出: 1->1->2->3->4->4->5->6
	 */
	class Solution {

		public ListNode mergeKLists(ListNode[] lists) {
			if (lists.length == 0) {
				return null;
			}if (lists.length == 1) {
				return lists[0];
			}
			List<ListNode> sortedList = initSortedNodeArr(lists);
			if (sortedList.size() == 0) {
				return null;
			}
			ListNode result = sortedList.remove(0);
			ListNode lastNode = result;
			ListNode current = result.next;
			while (sortedList.size() > 0) {
				if (current == null) {
					current = sortedList.remove(0);
				} else {
					ListNode first = sortedList.get(0);
					ListNode last = sortedList.get(sortedList.size() - 1);
					if (current.val <= first.val) {

					} else if (current.val >= last.val) {
						sortedList.add(current);
						current = sortedList.remove(0);
					} else {
						int beginIndex = 0;
						int endIndex = sortedList.size() - 1;
						while (endIndex > beginIndex) {
							if (endIndex - beginIndex == 1) {
								break;
							}
							int middle = (beginIndex + endIndex) / 2;
							ListNode middleNode = sortedList.get(middle);
							if (middleNode.val == current.val) {
								break;
							} else if (middleNode.val > current.val) {
								endIndex = middle;
							} else {
								beginIndex = middle;
							}
						}
						sortedList.add((beginIndex + endIndex) / 2 + 1, current);
						current = sortedList.remove(0);
					}
				}
				lastNode.next = current;
				lastNode = current;
				current = current.next;
			}
			return result;
		}

		private List<ListNode> initSortedNodeArr(ListNode[] lists) {
			List<ListNode> linkedList = new ArrayList<>();
			for (int i = 0; i < lists.length; i++) {
				ListNode node = lists[i];
				if (node != null) {
					linkedList.add(node);
					lists[i] = node.next;
				}
			}
			linkedList.sort(Comparator.comparingInt(o -> o.val));
			return linkedList;
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

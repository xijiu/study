package com.lkn.leetcode.algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author likangning
 * @since 2020/5/13 下午2:10
 */
public class LeetCode_102 {

	/**
	 *
	 */
	class Solution {
		private Queue<TreeNode> queue = new LinkedList<>();
		private List<List<Integer>> result = new ArrayList<>();

		public List<List<Integer>> levelOrder(TreeNode root) {
			queue.clear();
			result.clear();
			queue.add(root);
			if (root == null) {
				return result;
			}
			boolean addNextLine = false;
			TreeNode startLineNode = root;
			List<Integer> singleList = null;
			while (!queue.isEmpty()) {
				TreeNode node = queue.poll();
				if (node == startLineNode) {
					singleList = new ArrayList<>();
					result.add(singleList);
					addNextLine = false;
				}
				singleList.add(node.val);
				if (node.left != null) {
					queue.add(node.left);
					if (!addNextLine) {
						startLineNode = node.left;
						addNextLine = true;
					}
				}
				if (node.right != null) {
					queue.add(node.right);
					if (!addNextLine) {
						startLineNode = node.right;
						addNextLine = true;
					}
				}
			}
			return result;
		}
	}

	public static class TreeNode {
		int val;
		TreeNode left;
		TreeNode right;

		TreeNode(int x) {
			val = x;
		}
	}
}

package com.lkn.leetcode.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author likangning
 * @since 2020/4/22 上午10:03
 */
public class LeetCode_199 {

	class Solution {
		public List<Integer> rightSideView(TreeNode root) {
			List<Integer> result = new ArrayList<>();
			if (root == null) {
				return result;
			}
			TreeNode current = root;
			Stack<TreeNode> stack = new Stack<>();
			Stack<Integer> levelStack = new Stack<>();
			int maxLevel = -1;
			int level = 0;
			while (true) {
				if (level > maxLevel) {
					result.add(current.val);
					maxLevel = level;
				}
				if (current.left != null && current.right != null) {
					stack.push(current.left);
					levelStack.push(level + 1);
					current = current.right;
				} else if (current.left == null && current.right != null) {
					current = current.right;
				} else if (current.left != null && current.right == null) {
					current = current.left;
				} else {
					// 左右都为空
					if (stack.isEmpty()) {
						break;
					}
					current = stack.pop();
					level = levelStack.pop();
					continue;
				}
				level++;
			}
			return result;
		}
	}

	public class TreeNode {
		int val;
		TreeNode left;
		TreeNode right;

		TreeNode(int x) {
			val = x;
		}
	}
}
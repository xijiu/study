package com.lkn.leetcode.algorithm;

/**
 * @author likangning
 * @since 2020/4/22 上午11:04
 */
public class LeetCode_41 {
	class Solution {
		/**
		 * 0ms，击败100%
		 */
		public int firstMissingPositive(int[] nums) {
			int[] helper = new int[nums.length];
			for (int num : nums) {
				if (num > 0 && num <= nums.length) {
					helper[num - 1] = 1;
				}
			}
			for (int i = 0; i < helper.length; i++) {
				if (helper[i] == 0) {
					return i + 1;
				}
			}
			return helper.length + 1;
		}

//		public int firstMissingPositive(int[] nums) {
//			Arrays.sort(nums);
//			int matchNum = 1;
//			for (int num : nums) {
//				if (num > 0) {
//					if (num < matchNum) {
//					} else if (num == matchNum) {
//						matchNum++;
//					} else {
//						break;
//					}
//				}
//			}
//			return matchNum;
//		}
	}
}



package com.lkn.leetcode.algorithm.interview;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author likangning
 * @since 2020/4/23 下午2:45
 */
public class LeetCode_08_03 {

	@Test
	public void test() {
		// 61 : 73
		Solution solution = new Solution();
//		int[] numbers = {0, 1, 2, 3, 4, 5};
		int[] numbers = {1, 1, 1};
		int trap = solution.findMagicIndex(numbers);
		System.out.println(trap);
	}


	class Solution {
		public int findMagicIndex(int[] nums) {
			for (int i = 0; i < nums.length; i++) {
				if (i == nums[i]) {
					return nums[i];
				} else if (nums[i] < i) {
					continue;
				} else {
					i = nums[i] - 1;
				}
			}
			return -1;
		}
	}
}

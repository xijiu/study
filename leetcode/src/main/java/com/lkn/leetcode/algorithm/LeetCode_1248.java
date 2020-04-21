package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * @author likangning
 * @since 2020/4/21 上午11:33
 */
public class LeetCode_1248 {

	@Test
	public void test() {
		Solution solution = new Solution();
//		int[] nums = {2,2,2,1,2,2,1,2,2,2};
		int[] nums = {2,2,2,1,2,2,1,2,2,2,1,2,2};
		int result = solution.numberOfSubarrays(nums, 2);
		System.out.println(result);
	}

	class Solution {
		public int numberOfSubarrays(int[] nums, int k) {
			int size = 0;
			for (int i = 0; i < nums.length; i++) {
				if ((nums[i] & 1) == 1) {
					nums[size++] = i;
				}
			}
			if (size < k) {
				return 0;
			}
			int total = 0;
			int endTempIndex = size - k + 1;
			for (int i = 0; i < endTempIndex; i++) {
				int beginIndex = nums[i];
				int tempIndex = i + k - 1;
				int endIndex = nums[tempIndex];
				int headNum = i == 0 ? beginIndex + 1 : beginIndex - nums[i - 1];
				int tailNum = tempIndex == size - 1 ? nums.length - endIndex : nums[tempIndex + 1] - endIndex;
				total += headNum * tailNum;
			}
			return total;
		}
	}
}

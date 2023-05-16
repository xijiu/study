package com.lkn.leetcode.algorithm;

import org.junit.Test;


public class LeetCode_26 {


	@Test
	public void aaa() {
		System.out.println(removeDuplicates(new int[] {0,0,1,1,1,2,2,3,3,4}));
	}

	public int removeDuplicates(int[] nums) {
		if (nums.length <= 1) {
			return nums.length;
		}
		int actualIndex = 1;
		int last = nums[0];
		for (int i = 1; i < nums.length; i++) {
			if (nums[i] == last) {
			} else {
				nums[actualIndex++] = nums[i];
				last = nums[i];
			}
		}
		return actualIndex;
	}
}

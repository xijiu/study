package com.lkn.leetcode.algorithm;

import org.junit.Test;


public class LeetCode_27 {


	@Test
	public void aaa() {
		System.out.println(removeElement(new int[] {3,2,2,3}, 3));
	}

	public int removeElement(int[] nums, int val) {
		int actualIndex = 0;
		for (int i = 0; i < nums.length; i++) {
			if (nums[i] != val) {
				nums[actualIndex++] = nums[i];
			}
		}
		return actualIndex;
	}
}

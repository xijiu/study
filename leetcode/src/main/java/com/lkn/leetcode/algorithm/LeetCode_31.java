package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.util.Arrays;


public class LeetCode_31 {


	@Test
	public void aaa() {
//		int[] arr = {1, 3, 2};  // 2, 1, 3
//		int[] arr = {1, 2, 3};
//		int[] arr = {3, 2, 1};
//		int[] arr = {2, 3, 1};  // 3, 1, 2
//		int[] arr = {1, 5, 1};  // 5, 1, 1
//		int[] arr = {5, 1, 1};  // 5, 1, 1
		int[] arr = {2,1,2,2,2,2,2,1};  // [2,2,1,1,2,2,2,2]
		nextPermutation(arr);
		System.out.println(Arrays.toString(arr));
	}

	public void nextPermutation(int[] nums) {
		if (nums.length == 1) {
			return;
		}
		if (nums.length == 2) {
			swap(nums, 0, 1);
			return;
		}
		int endIndex = nums.length - 1;
		while (endIndex > 0) {
			if (nums[endIndex - 1] >= nums[endIndex]) {
				endIndex--;
			} else {
				break;
			}
		}
		if (endIndex == 0) {
			revert(nums, 0, nums.length - 1);
			return;
		}

		int diff = Integer.MAX_VALUE;
		int swapIdx = -1;
		int leftVal = nums[endIndex - 1];
		for (int i = endIndex; i < nums.length; i++) {
			int diffTmp = nums[i] - leftVal;
			if (diffTmp > 0 && diffTmp <= diff) {
				swapIdx = i;
				diff = diffTmp;
			}
		}
		swap(nums, endIndex - 1, swapIdx);
		revert(nums, endIndex, nums.length - 1);
	}

	private void swap(int[] nums, int i1, int i2) {
		int tmp = nums[i1];
		nums[i1] = nums[i2];
		nums[i2] = tmp;
	}


	private void revert(int[] arr, int leftIdx, int rightIdx) {
		while (true) {
			if (rightIdx < leftIdx) {
				break;
			}
			if (arr[rightIdx] < arr[leftIdx]) {
				int tmp = arr[leftIdx];
				arr[leftIdx] = arr[rightIdx];
				arr[rightIdx] = tmp;
				leftIdx++;
				rightIdx--;
			} else {
				break;
			}
		}
	}
}

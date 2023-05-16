package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.util.Arrays;


public class LeetCode_34 {


	@Test
	public void aaa() {
		int[] arr = searchRange(new int[]{0,0,1,1,2,2,2,2,3,3,4,4,4,5,6,6,6,7,8,8}, 4);
		System.out.println(Arrays.toString(arr));
	}

	private int[] resultArr = new int[2];


	public int[] searchRange(int[] nums, int target) {
		reset();
		searchRange(nums, target, 0, nums.length - 1);
		return resultArr;
	}

	private void searchRange(int[] nums, int target, int left, int right) {
		if (right - left <= 3) {
			for (int i = left; i <= right; i++) {
				if (nums[i] == target) {
					find(nums, i, target);
				}
			}
			return;
		}
		if (target >= nums[left] && target <= nums[right]) {
			if (nums[left] == target) {
				find(nums, left, target);
				return;
			}
			if (nums[right] == target) {
				find(nums, right, target);
				return;
			}
			int mid = left + (right - left) / 2;
			if (nums[mid] > target) {
				searchRange(nums, target, left, mid);
			} else {
				searchRange(nums, target, mid, right);
			}
		} else {
			reset();
		}
	}

	private void find(int[] nums, int index, int target) {
		int min = index;
		int max = index;
		while (min >= 0) {
			if (nums[min] == target) {
				min--;
			} else {
				min++;
				break;
			}
		}

		while (max < nums.length) {
			if (nums[max] == target) {
				max++;
			} else {
				max--;
				break;
			}
		}
		min = Math.max(min, 0);
		max = max >= nums.length ? nums.length - 1 : max;
		resultArr[0] = min;
		resultArr[1] = max;
	}

	private void reset() {
		resultArr[0] = -1;
		resultArr[1] = -1;
	}
}

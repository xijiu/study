package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.util.Arrays;


public class LeetCode_33 {


	@Test
	public void aaa() {
//		System.out.println(search(new int[]{4,5,6,7,0,1,2}, 3));
		System.out.println(search(new int[]{1}, 1));
	}

	public int search(int[] nums, int target) {
		return find(nums, target, 0, nums.length - 1);
	}

	private int find(int[] nums, int target, int left, int right) {
		if (right - left <= 3) {
			for (int i = left; i <= right; i++) {
				if (nums[i] == target) {
					return i;
				}
			}
			return -1;
		}
		if (nums[right] > nums[left]) {
			if (target >= nums[left] && target <= nums[right]) {
				if (right - left <= 3) {
					for (int i = left; i <= right; i++) {
						if (nums[i] == target) {
							return i;
						}
					}
					return -1;
				} else {
					int midIndex = left + (right - left) / 2;
					if (target >= nums[left] && target <= nums[midIndex]) {
						return find(nums, target, left, midIndex);
					} else {
						return find(nums, target, midIndex, right);
					}
				}
			} else {
				return -1;
			}
		} else {
			int midIndex = left + (right - left) / 2;
			if (nums[midIndex] < nums[right]) {
				if (target >= nums[midIndex] && target <= nums[right]) {
					return find(nums, target, midIndex, right);
				}
				return find(nums, target, left, midIndex);
			} else {
				if (target >= nums[left] && target <= nums[midIndex]) {
					return find(nums, target, left, midIndex);
				} else {
					return find(nums, target, midIndex, right);
				}
			}
		}
	}
}

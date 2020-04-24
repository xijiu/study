package com.lkn.leetcode.algorithm.interview;

import org.junit.Test;

/**
 * 在数组中的两个数字，如果前面一个数字大于后面的数字，则这两个数字组成一个逆序对。输入一个数组，求出这个数组中的逆序对的总数。
 * 输入: [7,5,6,4]
 * 输出: 5
 *
 * 归并排序典型案例
 *
 * @author likangning
 * @since 2020/4/24 下午2:37
 */
public class LeetCode_51 {

	@Test
	public void aaa() {
		Solution solution = new Solution();
		int[] nums = {7,5,6,4};
		int result = solution.reversePairs(nums);
		System.out.println(result);
	}

	class Solution {
		private int times = 0;
		private int flag = 0;
		private int[] cache1 = new int[1];
		private int[] cache2 = new int[1];
//		private int[] cacheTwo = new int[2];

		public int reversePairs(int[] nums) {
			times = 0;
			if (nums.length > 1) {
				mergeSort(nums, 0, nums.length - 1);
//			System.out.println(Arrays.toString(result));
			}
			return times;
		}

		private int[] mergeSort(int[] nums, int beginIndex, int endIndex) {
			if (endIndex - beginIndex == 1) {
				if (nums[beginIndex] > nums[endIndex]) {
					times++;
					return new int[]{nums[endIndex], nums[beginIndex]};
				}
				return new int[]{nums[beginIndex], nums[endIndex]};
			} else if (endIndex != beginIndex) {
				int middleIndex = (beginIndex + endIndex) / 2;
				int[] leftArr = mergeSort(nums, beginIndex, middleIndex);
				int[] rightArr = mergeSort(nums, middleIndex + 1, endIndex);
				int[] resultArr = beginIndex == 0 && endIndex == nums.length - 1 ? nums : new int[endIndex - beginIndex + 1];
				int leftIndex = 0;
				int rightIndex = 0;
				int index = 0;
				while (index < resultArr.length) {
					if (leftIndex < leftArr.length && rightIndex < rightArr.length) {
						if (leftArr[leftIndex] <= rightArr[rightIndex]) {
							resultArr[index++] = leftArr[leftIndex++];
						} else {
							times += leftArr.length - leftIndex;
							resultArr[index++] = rightArr[rightIndex++];
						}
					} else if (leftIndex >= leftArr.length) {
						resultArr[index++] = rightArr[rightIndex++];
					} else {
						resultArr[index++] = leftArr[leftIndex++];
					}
				}
				return resultArr;
			} else {
				if (flag++ % 2 == 0) {
					cache1[0] = nums[beginIndex];
					return cache1;
				} else {
					cache2[0] = nums[beginIndex];
					return cache2;
				}
			}
		}
	}
}

package com.lkn.leetcode;

import java.util.*;

/**
 * @author likangning
 * @since 2020/5/13 下午4:04
 */
public class LeetCode_18 {

	public static void main(String[] args) {
		new LeetCode_18().begin();
	}

	private void begin() {
		Solution solution = new Solution();
		// [[-5,-4,-1,1],[-5,-4,0,0],[-5,-2,-2,0],[-4,-2,-2,-1]]
		List<List<Integer>> result = solution.fourSum(new int[]{-1,0,-5,-2,-2,-4,0,1,-2}, -9);
		System.out.println(result);
	}

	class Solution {
		public List<List<Integer>> fourSum(int[] nums, int target) {
			List<List<Integer>> result = new ArrayList<>();
			Arrays.sort(nums);
			for (int i = 0; i < nums.length - 3; i++) {
				if (i >= 1 && nums[i] == nums[i - 1]) {
					continue;
				}
				if (target >= 0 && nums[i] * 2 > target) {
					break;
				}
				for (int j = i + 1; j < nums.length - 2; j++) {
					if (j >= i + 2 && nums[j] == nums[j - 1]) {
						continue;
					}
					if (target >= 0 && nums[j] * 2 + nums[i] > target) {
						break;
					}
					for (int z = j + 1; z < nums.length - 1; z++) {
						if (z >= j + 2 && nums[z] == nums[z - 1]) {
							continue;
						}
						if (target >= 0 && nums[z] * 2 + nums[i] + nums[j] > target) {
							break;
						}
						int sum3 = nums[i] + nums[j] + nums[z];
						if (sum3 > target && nums[z] >= 0) {
							break;
						} else {
							int leave = target - sum3;
							int beginIndex = z + 1;
							int endIndex = nums.length - 1;
							if (nums[beginIndex] <= leave && nums[endIndex] >= leave) {
								while (beginIndex <= endIndex) {
									if (beginIndex == endIndex || endIndex - beginIndex == 1) {
										if (nums[beginIndex] == leave || nums[endIndex] == leave) {
											List<Integer> single = new ArrayList<>();
											single.add(nums[i]);
											single.add(nums[j]);
											single.add(nums[z]);
											single.add(leave);
											result.add(single);
										}
										break;
									}
									int middleIndex = beginIndex + (endIndex - beginIndex) / 2;
									if (nums[middleIndex] == leave) {
										List<Integer> single = new ArrayList<>();
										single.add(nums[i]);
										single.add(nums[j]);
										single.add(nums[z]);
										single.add(leave);
										result.add(single);
										break;
									} else if (nums[middleIndex] > leave) {
										endIndex = middleIndex;
									} else {
										beginIndex = middleIndex;
									}
								}
							}
						}
					}
				}
			}
			return result;
		}
	}
}

package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author likangning
 * @since 2020/1/19 下午5:13
 */
public class LeetCode_16 {

	@Test
	public void test() {
//		Object result = new LeetCode_16.Solution().threeSumClosest(new int[]{-1, 2, 1, -4}, 1);
		Object result = new LeetCode_16.Solution().threeSumClosest(new int[]{1,6,9,14,16,70}, 81);
		System.out.println(result);
	}

	class Solution {
		public int threeSumClosest(int[] nums, int target) {
			Arrays.sort(nums);
			Integer totalTargetVal = null;
			Integer totalLastDiff = null;
			for (int i = 0; i < nums.length; i++) {
				for (int j = i + 1; j < nums.length; j++) {
					Integer lastDiff = null;
					Integer targetVal = null;
					for (int k = j + 1; k < nums.length; k++) {
						int sum = nums[i] + nums[j] + nums[k];
						if (lastDiff == null) {
							lastDiff = Math.abs(sum - target);
							targetVal = sum;
						} else {
							int currDiff = Math.abs(sum - target);
							if (currDiff > lastDiff) {
								break;
							}
							lastDiff = currDiff;
							targetVal = sum;
						}
					}
					if (totalTargetVal == null) {
						totalTargetVal = targetVal;
						totalLastDiff = lastDiff;
					} else if (lastDiff != null) {
						if (lastDiff < totalLastDiff) {
							totalLastDiff = lastDiff;
							totalTargetVal = targetVal;
						}
					}
					if (totalLastDiff == target) {
						return totalLastDiff;
					}
				}
			}
			return totalTargetVal;
		}
	}

	@Test
	public void test2() throws Exception {
		Set<String> set = new TreeSet<>();
		set.add("2020-03-11 11:22:33");
		set.add("2020-03-12 11:22:33");
		set.add("2020-03-10 11:22:04");
		set.add("2020-03-10 11:22:03");
		set.add("2020-03-13 11:22:33");
		System.out.println(set);
	}
}

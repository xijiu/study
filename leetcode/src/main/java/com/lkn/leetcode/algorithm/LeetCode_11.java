package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 *
 * @author likangning
 * @since 2019/12/25 上午11:12
 */
public class LeetCode_11 {

	@Test
	public void test() {
		Object result = new Solution().maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7});
		System.out.println(result);
	}

	class Solution {
		public int maxArea(int[] height) {
			int maxArea = 0;
			int maxIndex = -1;
			for (int i = 0; i < height.length; i++) {
				if (maxArea > 0 && height[i] < height[maxIndex]) {
					continue;
				}
				for (int j = i + 1; j < height.length; j++) {
					int area = (j - i) * (height[i] < height[j] ? height[i] : height[j]);
					if (area > maxArea) {
						maxArea = area;
						maxIndex = i;
					}
				}
			}
			return maxArea;
		}
	}

}

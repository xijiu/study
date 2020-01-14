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
//		Object result = new Solution().maxArea(new int[]{1, 2, 3, 4});
		System.out.println(result);
	}

//	class Solution {
//		public int maxArea(int[] height) {
//			int maxArea = 0;
//			for (int i = 0; i < height.length; i++) {
//				if (i > 0) {
//					int flag = maxArea / (height.length - i);
//					if (height[i] < flag) {
//						continue;
//					}
//				}
//
//				for (int j = i + 1; j < height.length; j++) {
//					int area = (j - i) * (height[i] < height[j] ? height[i] : height[j]);
//					if (area > maxArea) {
//						maxArea = area;
//					}
//				}
//			}
//			return maxArea;
//		}
//	}

	class Solution {
		public int maxArea(int[] height) {
			int maxArea = 0;
			int i = 0;
			int j = height.length - 1;
			while (j > i) {
				int area = (j - i) * (height[i] > height[j] ? height[j--] : height[i++]);
				maxArea = area > maxArea ? area : maxArea;
			}
			return maxArea;
		}
	}

}

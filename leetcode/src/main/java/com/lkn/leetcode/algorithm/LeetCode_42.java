package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * java 1ms，击败 99.99%
 *
 * @author likangning
 * @since 2020/4/22 下午3:23
 */
public class LeetCode_42 {

	@Test
	public void test() {
		Solution solution = new Solution();
		int[] heightArr = {0,1,0,2,1,0,1,3,2,1,2,1};
		int trap = solution.trap(heightArr);
		System.out.println(trap);
	}


	class Solution {
		public int trap(int[] heightArr) {
			if (heightArr.length <= 1) {
				return 0;
			}
			int result = 0;
			boolean leftFixed = false;
			int leftHeight = -1;
			int leftIndex = -1;
			int selfArea = 0;
			for (int i = 0; i < heightArr.length; i++) {
				int height = heightArr[i];
				if (!leftFixed) {
					if (height > 0) {
						leftFixed = true;
						leftHeight = height;
						leftIndex = i;
						selfArea = height;
					}
				} else {
					// 左侧已经固定
					if (height >= leftHeight) {
						if (i == leftIndex + 1) {
							leftFixed = true;
							leftHeight = height;
							leftIndex = i;
							selfArea = height;
						} else {
							int actualHeight = leftHeight > height ? height : leftHeight;
							int totalArea = actualHeight * (i - leftIndex + 1);
							result += totalArea - (selfArea + height - (Math.abs(leftHeight - height)));

							leftFixed = true;
							leftHeight = height;
							leftIndex = i;
							selfArea = height;
						}
					} else {
						selfArea += height;
					}
				}
				if (leftIndex >= heightArr.length) {
					break;
				}
			}

			boolean rightFixed = false;
			int rightHeight = -1;
			int rightIndex = -1;
			selfArea = 0;
			// 从右往左开始寻找
			for (int i = heightArr.length - 1; i >= leftIndex; i--) {
				int height = heightArr[i];
				if (!rightFixed) {
					if (height > 0) {
						rightFixed = true;
						rightHeight = height;
						rightIndex = i;
						selfArea = height;
					}
				} else {
					// 右侧已经固定
					if (height >= rightHeight) {
						if (i == rightIndex - 1) {
							rightHeight = height;
							rightIndex = i;
							selfArea = height;
						} else {
							int actualHeight = rightHeight > height ? height : rightHeight;
							int totalArea = actualHeight * (rightIndex - i + 1);
							result += totalArea - (selfArea + height - (Math.abs(rightHeight - height)));

							rightHeight = height;
							rightIndex = i;
							selfArea = height;
						}
					} else {
						selfArea += height;
					}
				}
			}
			return result;
		}
	}
}

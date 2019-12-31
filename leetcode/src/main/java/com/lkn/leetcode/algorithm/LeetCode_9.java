package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * 判断是否是回文数字
 * @author likangning
 * @since 2019/12/25 上午11:12
 */
public class LeetCode_9 {

	@Test
	public void test() {
		Object result = new Solution().isPalindrome(6);
		System.out.println(result);
	}

	class Solution {
		public boolean isPalindrome(int x) {
			if (x < 0) {
				return false;
			}
			int first = x;
			int second = 0;
			while (true) {
				second += first % 10;
				first /= 10;
				if (first > 0) {
					second *= 10;
				} else {
					break;
				}
			}
			return second == x;
		}
	}
}

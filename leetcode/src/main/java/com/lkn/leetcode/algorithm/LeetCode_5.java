package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * 最长回文子串
 * @author likangning
 * @since 2019/12/25 上午11:12
 */
public class LeetCode_5 {

	@Test
	public void test() {
		String testStr = "bbb";
		String result = new Solution().longestPalindrome(testStr);
		System.out.println(result);
	}

	class Solution {
		private int maxLen = 0;
		private int beginIndex = 0;

		public String longestPalindrome(String s) {
			if (s == null || "".equals(s) || s.length() == 1) {
				return s;
			}
			maxLen = 0;
			beginIndex = 0;
			char[] charsArr = s.toCharArray();
			for (int i = 1; i <= charsArr.length - 1; i++) {
				calculate(charsArr, i, true);
				calculate(charsArr, i, false);
			}
			if (maxLen > 0) {
				return s.substring(beginIndex, beginIndex + maxLen);
			}
			return String.valueOf(charsArr[0]);
		}

		private void calculate(char[] charsArr, int index, boolean pickupMiddle) {
			int leftIndex = index - 1;
			int validBeginIndex = leftIndex;
			int rightIndex = index;

			if (pickupMiddle) {
				rightIndex++;
			}

			int len = 0;
			while (true) {
				if (leftIndex < 0 || rightIndex >= charsArr.length) {
					break;
				}
				if (charsArr[leftIndex--] == charsArr[rightIndex++]) {
					validBeginIndex--;
					len++;
				} else {
					break;
				}
			}

			len = pickupMiddle ? len * 2 + 1 : len * 2;
			if (len <= maxLen) {
				return;
			}

			beginIndex = validBeginIndex + 1;
			maxLen = len;
		}
	}
}

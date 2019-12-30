package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * 最长回文子串
 * @author likangning
 * @since 2019/12/25 上午11:12
 */
public class Leetcode_8 {

	@Test
	public void test() {
//		System.out.println((int)' '); // 32
//		System.out.println((int)'-');	// 45
//		System.out.println((int)'+');	// 43
		Object result = new Solution().myAtoi("-000123");
		System.out.println(result);
	}

	class Solution {
		public int myAtoi(String str) {
			int beginIndex = -1;
			boolean isNegative = false;
			char[] chars = str.toCharArray();
			int endIndex = chars.length;
			if (chars.length == 0) {
				return 0;
			}
			for (int i = 0; i < chars.length; i++) {
				if (beginIndex == -1 && (chars[i] < 48 || chars[i] > 57) && chars[i] != 32 && chars[i] != 45 && chars[i] != 43) {
					return 0;
				}
				if (beginIndex == -1 && (chars[i] == 45 || chars[i] == 43 || (chars[i] >= 48 && chars[i] <= 57))) {
					beginIndex = chars[i] >= 48 && chars[i] <= 57 ? i : i + 1;
					isNegative = chars[i] == '-';
					continue;
				}
				if (beginIndex != -1 && (chars[i] < 48 || chars[i] > 57)) {
					endIndex = i;
					break;
				}
			}

			if (beginIndex == -1) {
				return 0;
			}

			boolean flag = true;
			for (int i = beginIndex; i < endIndex; i++) {
				if (flag && chars[i] == 48) {
					beginIndex = i + 1;
				} else {
					flag = false;
				}
			}

			if (endIndex < beginIndex || beginIndex == -1) {
				return 0;
			}

			String resultStr = str.substring(beginIndex, endIndex);
			if (beginIndex == endIndex && (resultStr.equals("-") || resultStr.equals("+") || resultStr.equals(""))) {
				return 0;
			}
			if (endIndex - beginIndex > 15) {
				if (isNegative) {
					return Integer.MIN_VALUE;
				} else {
					return Integer.MAX_VALUE;
				}
			}
			long result = Long.parseLong(resultStr);
			result = isNegative ? -result : result;
			if (result > Integer.MAX_VALUE) {
				return Integer.MAX_VALUE;
			} else if (result < Integer.MIN_VALUE) {
				return Integer.MIN_VALUE;
			} else {
				return (int) result;
			}
		}
	}

}

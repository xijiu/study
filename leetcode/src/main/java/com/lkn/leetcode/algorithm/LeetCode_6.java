package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * 最长回文子串
 * @author likangning
 * @since 2019/12/25 上午11:12
 */
public class LeetCode_6 {

	@Test
	public void test() {
		String testStr = "LEETCODEISHIRING";
		String result = new Solution().convert(testStr, 3);
		System.out.println(result);
	}

	class Solution {
		public String convert(String str, int numRows) {
			if (numRows == 1) {
				return str;
			}
			StringBuilder sb = new StringBuilder();
			char[] chars = str.toCharArray();
			for (int i = 0; i < numRows; i++) {
				appendByRow(numRows, i, chars, sb);
			}
			return sb.toString();
		}

		private void appendByRow(int numRows, int index, char[] chars, StringBuilder sb) {
			int totalLen = chars.length;
			boolean single = false;
			if (index == 0 || index == numRows - 1) {
				single = true;
			}
			int gap = numRows * 2 - 2;
			if (single) {
				while (index < totalLen) {
					sb.append(chars[index]);
					index += gap;
				}
			} else {
				int firstIndex = index;
				int secondIndex = gap - index;
				while (true) {
					if (firstIndex < totalLen) {
						sb.append(chars[firstIndex]);
						firstIndex += gap;
					} else {
						break;
					}
					if (secondIndex < totalLen) {
						sb.append(chars[secondIndex]);
						secondIndex += gap;
					} else {
						break;
					}
				}
			}

		}
	}

}

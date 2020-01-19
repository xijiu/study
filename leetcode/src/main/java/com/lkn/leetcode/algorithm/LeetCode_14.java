package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * 最长公共前缀
 *
 * @author likangning
 * @since 2020/1/14 下午8:29
 */
public class LeetCode_14 {

	@Test
	public void test() {
//		Object result = new LeetCode_14.Solution().longestCommonPrefix(new String[]{"flower", "flow", "flight"});
//		Object result = new LeetCode_14.Solution().longestCommonPrefix(new String[]{"a"});
//		Object result = new LeetCode_14.Solution().longestCommonPrefix(new String[]{"", "b"});
		Object result = new LeetCode_14.Solution().longestCommonPrefix(new String[]{"a", "ac"});
		System.out.println(result);
	}

	class Solution {
		public String longestCommonPrefix(String[] strs) {
			if (strs.length == 0) {
				return "";
			}
			int baseLen = strs[0].length();
			if (baseLen == 0) {
				return "";
			}
			int endIndex = baseLen;
			for (int i = 1; i < strs.length; i++) {
				int charsLen = strs[i].length();
				int index = 0;
				while (index <= endIndex && index < charsLen && index < baseLen) {
					if (strs[i].charAt(index) != strs[0].charAt(index)) {
						endIndex = index;
						break;
					} else {
						index++;
					}
				}
				endIndex = endIndex > charsLen ? charsLen : endIndex;
			}
			if (endIndex < 0) {
				return "";
			} else {
				return strs[0].substring(0, endIndex);
			}
		}
	}
}

package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * 正则表达式
 *
 * 	mississippi
 *	mis*is*p*.
 * @author likangning
 * @since 2019/12/25 上午11:12
 */
public class LeetCode_10 {

	@Test
	public void test() {
		Object result = new Solution().isMatch("mississippi", "mis*is*p*.");
//		Object result = new Solution().isMatch("aa", "a");
//		Object result = new Solution().isMatch("aa", "a*");
//		Object result = new Solution().isMatch("a", "ab*");
//		Object result = new Solution().isMatch("", "c*c*");
		System.out.println(result);
		System.out.println((int) '*');
		System.out.println((int) '.');
	}

	class Solution {

		private boolean isMatch = false;
		private char[] baseChars;
		private char[] exprChars;
		private int XING = 42;
		private int DOT = 46;
		public boolean isMatch(String s, String p) {
			isMatch = false;
			baseChars = s.toCharArray();
			exprChars = p.toCharArray();
			traverseMatch(0, 0);
			return isMatch;
		}

		private void traverseMatch(int baseIndex, int exprIndex) {
			if (isMatch) {
				return;
			}
			if (baseIndex >= baseChars.length || exprIndex >= exprChars.length) {
				if (baseIndex == baseChars.length && exprIndex == exprChars.length) {
					isMatch = true;
				} else if (baseIndex == baseChars.length && (exprChars.length - exprIndex) % 2 == 0) {
					boolean flag = true;
					for (int i = exprIndex + 1; i < exprChars.length; i = i + 2) {
						if (exprChars[i] != XING) {
							flag = false;
						}
					}
					if (flag) {
						isMatch = true;
					}
				}
				return;
			}
			char baseChar = baseChars[baseIndex];
			char exprChar = exprChars[exprIndex];
			// 最后一个字符
			if (exprIndex == exprChars.length - 1) {
				if ((baseChar == exprChar || exprChar == DOT) && baseIndex == baseChars.length - 1) {
					isMatch = true;
				}
			} else {
				char nextExprChar = exprChars[exprIndex + 1];
				if (nextExprChar == XING) {
					// 认为*的匹配次数为0
					traverseMatch(baseIndex, exprIndex + 2);
					boolean isMatchAll = exprChar == DOT;

					// 认为*的匹配次数为多个
					int matchNum = 1;
					while (!isMatch) {
						if (baseIndex + matchNum > baseChars.length) {
							break;
						}
						boolean isEqual = true;
						if (!isMatchAll) {
							for (int i = baseIndex; i < baseIndex + matchNum; i++) {
								if (baseChars[i] != exprChar) {
									isEqual = false;
									break;
								}
							}
						}
						if (isEqual) {
							traverseMatch( baseIndex + matchNum, exprIndex + 2);
						} else {
							break;
						}
						matchNum++;
					}
				} else {
					if (baseChar == exprChar || exprChar == DOT) {
						if (baseIndex == baseChars.length - 1 && exprIndex == exprChars.length - 1) {
							isMatch = true;
						} else {
							traverseMatch( baseIndex + 1, exprIndex + 1);
						}
					}
				}
			}
		}
	}
}

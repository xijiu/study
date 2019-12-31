package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.util.Stack;

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
	}

	class Solution {
		boolean isMatch = false;
		private Stack<Integer> stack = new Stack<>();
		public boolean isMatch(String s, String p) {
			isMatch = false;
			char[] baseChars = s.toCharArray();
			char[] matchChars = p.toCharArray();
			stack.clear();
			stack.push(0);
			stack.push(0);
			traverseMatch(baseChars, matchChars, stack);
			return isMatch;
		}

		private void traverseMatch(char[] baseChars, char[] exprChars, Stack<Integer> stack) {
			if (stack.isEmpty() || isMatch) {
				return;
			}
			Integer baseIndex = stack.pop();
			Integer exprIndex = stack.pop();
			if (baseIndex >= baseChars.length || exprIndex >= exprChars.length) {
				if (baseIndex == baseChars.length && exprIndex == exprChars.length) {
					isMatch = true;
				} else if (baseIndex == baseChars.length && (exprChars.length - exprIndex) % 2 == 0) {
					boolean flag = true;
					for (int i = exprIndex + 1; i < exprChars.length; i = i + 2) {
						if (exprChars[i] != '*') {
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
				if ((baseChar == exprChar || exprChar == '.') && baseIndex == baseChars.length - 1) {
					isMatch = true;
					return;
				}
			} else {
				char nextExprChar = exprChars[exprIndex + 1];
				if (nextExprChar == '*') {
					// 认为*的匹配次数为0
					stack.push(exprIndex + 2);
					stack.push(baseIndex);
					traverseMatch(baseChars, exprChars, stack);
					boolean isMatchAll = exprChar == '.';

					// 认为*的匹配次数为多个
					int matchNum = 1;
					while (true) {
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
							stack.push(exprIndex + 2);
							stack.push(baseIndex + matchNum);
							traverseMatch(baseChars, exprChars, stack);
						} else {
							break;
						}
						matchNum++;
					}
				} else {
					if (baseChar == exprChar || exprChar == '.') {
						if (baseIndex == baseChars.length - 1 && exprIndex == exprChars.length - 1) {
							isMatch = true;
							return;
						} else {
							stack.push(exprIndex + 1);
							stack.push(baseIndex + 1);
							traverseMatch(baseChars, exprChars, stack);
						}
					} else {
						// 已经不能继续匹配
						return;
					}
				}
			}
		}
	}
}

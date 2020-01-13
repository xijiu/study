package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * 正则表达式
 * 采用了2种解决思路：递归、动态规划
 * 结论：还是动态规划性能好，递归的最好成绩是15ms，动态规划最好成绩2ms
 *
 * 	mississippi
 *	mis*is*p*.
 * @author likangning
 * @since 2019/12/25 上午11:12
 */
public class LeetCode_10 {

//	@Test
//	public void test() {
//		Object result = new Solution().isMatch("mississippi", "mis*is*p*.");
////		Object result = new Solution().isMatch("aa", "a");
////		Object result = new Solution().isMatch("aa", "a*");
////		Object result = new Solution().isMatch("a", "ab*");
////		Object result = new Solution().isMatch("", "c*c*");
//		System.out.println(result);
//		System.out.println((int) '*');
//		System.out.println((int) '.');
//	}

//	/**
//	 * 递归解决方案
//	 */
//	class Solution {
//
//		private boolean isMatch = false;
//		private char[] baseChars;
//		private char[] exprChars;
//		private int XING = 42;
//		private int DOT = 46;
//		public boolean isMatch(String s, String p) {
//			isMatch = false;
//			baseChars = s.toCharArray();
//			exprChars = p.toCharArray();
//			traverseMatch(0, 0);
//			return isMatch;
//		}
//
//		private void traverseMatch(int baseIndex, int exprIndex) {
//			if (isMatch) {
//				return;
//			}
//			if (baseIndex >= baseChars.length || exprIndex >= exprChars.length) {
//				if (baseIndex == baseChars.length && exprIndex == exprChars.length) {
//					isMatch = true;
//				} else if (baseIndex == baseChars.length && (exprChars.length - exprIndex) % 2 == 0) {
//					boolean flag = true;
//					for (int i = exprIndex + 1; i < exprChars.length; i = i + 2) {
//						if (exprChars[i] != XING) {
//							flag = false;
//						}
//					}
//					if (flag) {
//						isMatch = true;
//					}
//				}
//				return;
//			}
//			char baseChar = baseChars[baseIndex];
//			char exprChar = exprChars[exprIndex];
//			// 最后一个字符
//			if (exprIndex == exprChars.length - 1) {
//				if ((baseChar == exprChar || exprChar == DOT) && baseIndex == baseChars.length - 1) {
//					isMatch = true;
//				}
//			} else {
//				char nextExprChar = exprChars[exprIndex + 1];
//				if (nextExprChar == XING) {
//					// 认为*的匹配次数为0
//					traverseMatch(baseIndex, exprIndex + 2);
//					boolean isMatchAll = exprChar == DOT;
//
//					// 认为*的匹配次数为多个
//					int matchNum = 1;
//					while (!isMatch) {
//						if (baseIndex + matchNum > baseChars.length) {
//							break;
//						}
//						boolean isEqual = true;
//						if (!isMatchAll) {
//							for (int i = baseIndex; i < baseIndex + matchNum; i++) {
//								if (baseChars[i] != exprChar) {
//									isEqual = false;
//									break;
//								}
//							}
//						}
//						if (isEqual) {
//							traverseMatch( baseIndex + matchNum, exprIndex + 2);
//						} else {
//							break;
//						}
//						matchNum++;
//					}
//				} else {
//					if (baseChar == exprChar || exprChar == DOT) {
//						if (baseIndex == baseChars.length - 1 && exprIndex == exprChars.length - 1) {
//							isMatch = true;
//						} else {
//							traverseMatch( baseIndex + 1, exprIndex + 1);
//						}
//					}
//				}
//			}
//		}
//	}

	/**
	 * 采用动态规划思路，用二维数组来记录状态
	 */
	@Test
	public void test3() {
		// aaa  ab*ac*a
//		Object result = new Solution().isMatch("aaa", "ab*ac*a");
//		Object result = new Solution().isMatch("aa", "a*");
		Object result = new Solution().isMatch("", ".*");
		System.out.println("final result is : " + result);
	}

	class Solution {
		public boolean isMatch(String s, String p) {
			char[] baseArr = s.toCharArray();
			char[] exprArr = p.toCharArray();
			if (baseArr.length == 0) {
				return emptyBaseMatch(exprArr);
			} else if (exprArr.length == 0) {
				return false;
			}
			boolean[][] result = new boolean[baseArr.length][exprArr.length];
			// 首选处理第一行
			int placeholderNum = 0;
			for (int j = 0; j < exprArr.length; j++) {
				if (baseArr[0] == exprArr[j] || exprArr[j] == '.') {
					if (placeholderNum == 0) {
						result[0][j] = true;
					}
				} else {
					if (exprArr[j] == '*') {
						result[0][j] = result[0][j - 1] || (j >= 2 && result[0][j - 2]);
					}
				}
				if (exprArr[j] == '*') {
					placeholderNum--;
				} else {
					placeholderNum++;
				}
			}

			for (int i = 1; i < baseArr.length; i++) {
				for (int j = 0; j < exprArr.length; j++) {
					if (exprArr[j] == '*') {
						result[i][j] = result[i][j - 1] || result[i - 1][j] && (exprArr[j - 1] == baseArr[i] || baseArr[i] == '.' || exprArr[j - 1] == '.');
						if (j >= 2 && !result[i][j]) {
							result[i][j] = result[i][j - 2];
						}
					} else {
						result[i][j] = j > 0 && result[i - 1][j - 1] && (baseArr[i] == exprArr[j] || exprArr[j] == '.');
					}
				}
			}
			return result[baseArr.length - 1][exprArr.length - 1];
		}

		private boolean emptyBaseMatch(char[] exprArr) {
			int placeholder = 0;
			for (char c : exprArr) {
				if (c == '*') {
					placeholder--;
				} else {
					placeholder++;
				}
			}
			return placeholder == 0;
		}
	}
}

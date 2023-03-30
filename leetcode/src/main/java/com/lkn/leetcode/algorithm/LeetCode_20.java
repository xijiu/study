package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.util.LinkedList;

/**
 *  '('，')'，'{'，'}'，'['，']'
 */
public class LeetCode_20 {

	@Test
	public void test() {
		System.out.println(new Solution().isValid("([)]"));
	}


	class Solution {
		public boolean isValid(String s) {
			LinkedList<Character> list = new LinkedList<>();
			char[] chars = s.toCharArray();
			for (char aChar : chars) {
				if (list.size() == 0) {
					if (isLeft(aChar)) {
						list.push(aChar);
					} else {
						return false;
					}
				} else {
					if (isLeft(aChar)) {
						list.push(aChar);
					} else {
						Character peek = list.poll();
						if ((peek == '(' && aChar == ')') || (peek == '[' && aChar == ']') || (peek == '{' && aChar == '}')) {
							continue;
						} else {
							return false;
						}
					}

				}
			}
			return list.size() == 0;
		}

		private boolean isLeft(char aChar) {
			return aChar == '(' || aChar == '{' || aChar == '[';
		}

		private boolean isRight(char aChar) {
			return aChar == ')' || aChar == '}' || aChar == ']';
		}
	}
}

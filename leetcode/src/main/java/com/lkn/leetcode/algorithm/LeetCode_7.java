package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * 字符串反转
 * @author likangning
 * @since 2019/12/25 上午11:12
 */
public class LeetCode_7 {

	@Test
	public void test() {
		Object result = new Solution().reverse(-12345678);
		System.out.println(result);
	}

	class Solution {

		public int reverse(int num) {
			long result = 0;
			int base = 1;
			while (true) {
				result *= 10;
				int i = num % 10;
				num = num / 10;
				result += base * i;
				if (num == 0) {
					break;
				}
			}
			if (result >= Integer.MIN_VALUE && result <= Integer.MAX_VALUE) {
				return (int) result;
			}
			return 0;
		}
	}

}

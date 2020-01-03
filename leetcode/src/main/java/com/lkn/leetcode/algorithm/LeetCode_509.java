package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * 斐波那契数列
 *
 * @author likangning
 * @since 2019/12/25 上午11:12
 */
public class LeetCode_509 {

	@Test
	public void test() {
		Object result = new Solution().fib(8);
		System.out.println(result);
	}

	class Solution {
		public int fib(int N) {
			if (N == 0) {
				return 0;
			} else if (N == 1) {
				return 1;
			} else {
				int[] result = new int[N + 1];
				result[0] = 0;
				result[1] = 1;
				for (int i = 2; i <= N; i++) {
					result[i] = result[i - 1] + result[i - 2];
				}
				return result[N];
			}
		}

		public int fib1(int N) {
			if (N == 0) {
				return 0;
			} else if (N == 1) {
				return 1;
			}
			return fib(N - 1) + fib(N -2);
		}
	}

}

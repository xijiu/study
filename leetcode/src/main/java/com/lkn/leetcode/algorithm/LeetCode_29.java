package com.lkn.leetcode.algorithm;

import org.junit.Test;


public class LeetCode_29 {


	@Test
	public void aaa() {
		System.out.println(divide(-2147483648, -1));
	}

	public int divide(int dividend, int divisor) {
		boolean same = (dividend < 0 && divisor < 0) || (dividend > 0 && divisor > 0);
		dividend = Math.abs(dividend);
		divisor = Math.abs(divisor);
		long num = 0;
		while (true) {
			if ((dividend = dividend - divisor) >= 0) {
				num++;
			} else {
				break;
			}
		}
		num = same ? num : -num;
		num = num > Integer.MAX_VALUE ? Integer.MAX_VALUE : num;
		num = num < Integer.MIN_VALUE ? Integer.MIN_VALUE : num;
		return (int) num;
	}
}

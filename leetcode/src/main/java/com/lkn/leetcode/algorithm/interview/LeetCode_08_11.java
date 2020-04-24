package com.lkn.leetcode.algorithm.interview;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author likangning
 * @since 2020/4/23 下午2:45
 */
public class LeetCode_08_11 {

	@Test
	public void test() {
		// 61 : 73
		Solution solution = new Solution();
		int trap = solution.waysToChange(50);
		System.out.println(trap);
	}

	class Solution {
		private final int mod = 1000000007;
//		private final int[] coins = {25,10,5,1};
		private final int[] coins = {1,5,10,25};

		public int waysToChange(int n) {
			int[] res = new int[n + 1];
			res[0] = 1;
			for(int coin : coins){
				for(int i = coin;i <= n;i++){
					res[i] = (res[i] + res[i - coin]) % mod;
				}
				System.out.println(Arrays.toString(res));
			}
			return res[n];
		}
	}
}

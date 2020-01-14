package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * 整数转罗马数字
 * https://leetcode-cn.com/problems/integer-to-roman/
 *
 * @author likangning
 * @since 2020/1/14 上午8:52
 */
public class LeetCode_12 {


	@Test
	public void test() {
		// 1994 : MCMXCIV
		// 58   : LVIII
		Object result = new LeetCode_12.Solution().intToRoman(58);
		System.out.println(result);
	}


	class Solution {
		public String intToRoman(int num) {
			StringBuilder sb = new StringBuilder();
			// 千
			int thousand = num / 1000;
			append(sb, thousand, 4);
			num = num % 1000;

			// 百
			int hundred = num / 100;
			append(sb, hundred, 3);
			num = num % 100;

			// 十
			int ten = num / 10;
			append(sb, ten, 2);
			num = num % 10;

			// 个
			append(sb, num, 1);
			return sb.toString();
		}

		/**
		 *
		 * @param sb
		 * @param num
		 * @param flag	1 : 个位
		 *              2 ： 十位
		 *              3 ： 百位
		 */
		private void append(StringBuilder sb, int num, int flag) {
			if (num < 5) {
				if (num == 4) {
					switch (flag) {
						case 1 :
							sb.append("IV");
							break;
						case 2 :
							sb.append("XL");
							break;
						case 3 :
							sb.append("CD");
							break;
					}
				} else {
					switch (flag) {
						case 1 :
							for (int i = 0; i < num; i++) {
								sb.append('I');
							}
							break;
						case 2 :
							for (int i = 0; i < num; i++) {
								sb.append('X');
							}
							break;
						case 3 :
							for (int i = 0; i < num; i++) {
								sb.append('C');
							}
							break;
						case 4 :
							for (int i = 0; i < num; i++) {
								sb.append('M');
							}
							break;
					}
				}
			} else {
				if (num == 9) {
					switch (flag) {
						case 1 :
							sb.append("IX");
							break;
						case 2 :
							sb.append("XC");
							break;
						case 3 :
							sb.append("CM");
							break;
					}
				} else {
					switch (flag) {
						case 1 :
							sb.append('V');
							for (int i = 5; i < num; i++) {
								sb.append('I');
							}
							break;
						case 2 :
							sb.append('L');
							for (int i = 5; i < num; i++) {
								sb.append('X');
							}
							break;
						case 3 :
							sb.append('D');
							for (int i = 5; i < num; i++) {
								sb.append('C');
							}
							break;
					}
				}
			}
		}
	}
}

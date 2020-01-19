package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * 罗马数字转整数
 *
 * @author likangning
 * @since 2020/1/14 上午9:50
 */
public class LeetCode_13 {

	@Test
	public void test() {
		// 1994 : MCMXCIV
		// 58   : LVIII
		// MMCCCXCIX
		Object result = new LeetCode_13.Solution().romanToInt("MCMXCIV");
//		Object result = new LeetCode_13.Solution().romanToInt("IV");
		System.out.println(result);
	}

	class Solution {
		// 1  IV
		// 2  IX
		// 3  XL
		// 4  XC
		// 5  CD
		// 6  CM
		private char[] arr = {'I', 'V', 1, 2, 'X', 'L', 3, 4, 'C',   'D', 5, 6, 'M' };
		private int[] value =  {1,    5,  4,    9,  	10,    50,  40,  90,  100,   500, 400, 900,  1000 };
		public int romanToInt(String s) {
			int result = 0;
			char[] array = s.toCharArray();
			int index = arr.length - 1;
			for (int i = 0; i < array.length; ) {
				if (i == array.length - 1 && (int) arr[index] <= 6) {
					index--;
					continue;
				}
				boolean equal = false;
				if ((int) arr[index] <= 6) {
					switch ((int) arr[index]) {
						case 1 :
							equal = array[i] == 'I' && array[i + 1] == 'V';
							break;
						case 2 :
							equal = array[i] == 'I' && array[i + 1] == 'X';
							break;
						case 3 :
							equal = array[i] == 'X' && array[i + 1] == 'L';
							break;
						case 4 :
							equal = array[i] == 'X' && array[i + 1] == 'C';
							break;
						case 5 :
							equal = array[i] == 'C' && array[i + 1] == 'D';
							break;
						case 6 :
							equal = array[i] == 'C' && array[i + 1] == 'M';
							break;
					}
				} else {
					equal = arr[index] == array[i];
				}
				if (equal) {
					result += value[index];
					i = i + (((int) arr[index]) <= 6 ? 2 : 1);
				} else {
					index--;
				}
			}
			return result;
		}
	}

//	class Solution {
//		private String[] arr = {"I", "V", "IV", "IX", "X", "L", "XL", "XC", "C",   "D", "CD", "CM", "M" };
//		private int[] value =  {1,    5,  4,    9,  	10,    50,  40,  90,  100,   500, 400, 900,  1000 };
//		public int romanToInt(String s) {
//			int result = 0;
//			char[] array = s.toCharArray();
//			int index = arr.length - 1;
//			for (int i = 0; i < array.length; ) {
//				if (i == array.length - 1 && arr[index].length() == 2) {
//					index--;
//					continue;
//				}
//				String tmp = arr[index].length() == 1 ? String.valueOf(array[i]) : array[i] + "" + array[i + 1];
//				if (arr[index].equals(tmp)) {
//					result += value[index];
//					i = i + arr[index].length();
//				} else {
//					index--;
//				}
//			}
//			return result;
//		}
//	}
}

package com.lkn.leetcode.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author likangning
 * @since 2020/4/21 下午3:43
 */
public class LeetCode_17 {

	class Solution {
		private Map<Character, char[]> map = new HashMap<>(8);
		{
			map.put('2', new char[]{'a', 'b', 'c'});
			map.put('3', new char[]{'d', 'e', 'f'});
			map.put('4', new char[]{'g', 'h', 'i'});
			map.put('5', new char[]{'j', 'k', 'l'});
			map.put('6', new char[]{'m', 'n', 'o'});
			map.put('7', new char[]{'p', 'q', 'r', 's'});
			map.put('8', new char[]{'t', 'u', 'v'});
			map.put('9', new char[]{'w', 'x', 'y', 'z'});
		}

		public List<String> letterCombinations(String digits) {
			List<String> list = new ArrayList<>();
			char[] chars = digits.toCharArray();
//			char[][] array = new char[][];
			for (char key : chars) {
				char[] ele = map.get(key);
			}
			return list;
		}
	}
}

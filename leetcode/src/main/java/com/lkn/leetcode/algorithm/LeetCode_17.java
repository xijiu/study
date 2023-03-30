package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author likangning
 * @since 2020/4/21 下午3:43
 */
public class LeetCode_17 {


	@Test
	public void test() {
		System.out.println(new Solution().letterCombinations("23"));
	}


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
			if (digits.equals("")) {
				return new ArrayList<>();
			}
			digits = digits.replaceAll("1", "");
			List<String> list = new ArrayList<>();
			char[] chars = digits.toCharArray();

			char[] arr = new char[digits.length()];

			List<char[]> tmpList = new ArrayList<>();

			for (char aChar : chars) {
				char[] ele = map.get(aChar);
				tmpList.add(ele);
			}

			aaa(tmpList, 0, chars.length, arr, list);

			return list;
		}

		private void aaa(List<char[]> tmpList, int level, int totalLevel, char[] arr, List<String> result) {
			char[] key = tmpList.get(level);
			if (level == totalLevel - 1) {
				for (char c : key) {
					char[] arrTmp = new char[arr.length];
					System.arraycopy(arr, 0, arrTmp, 0, arr.length - 1);
					arrTmp[arrTmp.length - 1] = c;
					result.add(new String(arrTmp));
				}
			} else {
				for (char c : key) {
					arr[level] = c;
					aaa(tmpList, level + 1, totalLevel, arr, result);
				}
			}
		}
	}




}

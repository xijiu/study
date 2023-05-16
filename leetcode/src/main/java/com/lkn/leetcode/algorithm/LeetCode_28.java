package com.lkn.leetcode.algorithm;

import org.junit.Test;


public class LeetCode_28 {


	@Test
	public void aaa() {
	}

	public int strStr(String haystack, String needle) {
		char[] haystackArr = haystack.toCharArray();
		char[] needleArr = needle.toCharArray();
		for (int i = 0; i <= haystackArr.length - needleArr.length; i++) {
			int num = 0;
			for (int j = 0; j < needleArr.length; j++) {
				if (needleArr[j] == haystackArr[i + j]) {
					num++;
				} else {
					break;
				}
			}
			if (num == needleArr.length) {
				return i;
			}
		}
		return -1;
	}
}

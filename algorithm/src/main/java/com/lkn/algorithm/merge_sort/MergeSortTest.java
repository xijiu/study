package com.lkn.algorithm.merge_sort;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author likangning
 * @since 2018/11/5 下午3:57
 */
public class MergeSortTest {

	@Test
	public void test() {
		int[] arr = {10, 2, 3, 5, 100, 1, 4, 9, 15, 14, 17, 16, 20};
		MergeSort.sort(arr);
		System.out.println(Arrays.toString(arr));
	}
}

package com.lkn.algorithm.merge_sort;

import lombok.ToString;
import org.junit.Test;

/**
 * 合并排序
 * @author likangning
 * @since 2018/11/5 下午2:34
 */
public class MergeSort {

	public static void sort(int[] arr) {
		if (arr == null || arr.length <= 1) {
			return;
		}
		int[] copy = new int[arr.length];
		TwoArraySortIndex sortIndex = genericIndex(0, arr.length - 1);
		compareForTwoArray(arr, copy, sortIndex);
	}

	private static void compareForTwoArray(int[] arr, int[] copy, TwoArraySortIndex sortIndex) {
		if (sortIndex.end1 > sortIndex.begin1) {
			TwoArraySortIndex sortIndexTemp = genericIndex(sortIndex.begin1, sortIndex.end1);
			compareForTwoArray(arr, copy, sortIndexTemp);
		}
		if (sortIndex.end2 > sortIndex.begin2) {
			TwoArraySortIndex sortIndexTemp = genericIndex(sortIndex.begin2, sortIndex.end2);
			compareForTwoArray(arr, copy, sortIndexTemp);
		}
		doTwoArrayMergeSort(arr, copy, sortIndex);
	}

	private static void doTwoArrayMergeSort(int[] arr, int[] copy, TwoArraySortIndex sortIndex) {
		int totalIndex = sortIndex.begin1;
		int index1 = sortIndex.begin1;
		int index2 = sortIndex.begin2;
		while (index1 <= sortIndex.end1 || index2 <= sortIndex.end2) {
			if (index1 > sortIndex.end1) {
				copy[totalIndex++] = arr[index2++];
			} else if (index2 > sortIndex.end2) {
				copy[totalIndex++] = arr[index1++];
			} else {
				if (arr[index1] <= arr[index2]) {
					copy[totalIndex++] = arr[index1++];
				} else {
					copy[totalIndex++] = arr[index2++];
				}
			}
		}
		// 排序完毕，执行拷贝
		System.arraycopy(copy, sortIndex.begin1, arr, sortIndex.begin1, sortIndex.end2 - sortIndex.begin1 + 1);
	}

	private static TwoArraySortIndex genericIndex(int beginIndex, int endIndex) {
		if (endIndex > 0 && beginIndex >= 0 && endIndex - beginIndex >= 1) {
			int middle = (endIndex - beginIndex) / 2;
			return new TwoArraySortIndex(
					beginIndex,
					beginIndex + middle,
					beginIndex + middle + 1,
					endIndex
					);
		}
		return null;
	}

	@ToString
	private static class TwoArraySortIndex {
		int begin1;
		int end1;
		int begin2;
		int end2;
		private TwoArraySortIndex(int begin1, int end1, int begin2, int end2) {
			this.begin1 = begin1;
			this.end1 = end1;
			this.begin2 = begin2;
			this.end2 = end2;
		}
	}

	@Test
	public void abc() {
		int beginIndex = 100;
		int endIndex = 102;
		int middle = (endIndex - beginIndex) / 2;
		System.out.println(beginIndex + " : " + (beginIndex + middle));
		System.out.println((beginIndex + middle + 1) + " : " + endIndex);
	}
}

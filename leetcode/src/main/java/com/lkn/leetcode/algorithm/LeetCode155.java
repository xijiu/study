package com.lkn.leetcode.algorithm;

import java.util.Arrays;

/**
 * @author likangning
 * @since 2020/5/12 上午10:52
 */
public class LeetCode155 {

	public static void main(String[] args) {
		new LeetCode155().aaaaa();
	}

	/**
	 * ["MinStack",[]
	 * "push",		[-10]
	 * "push",		[14]
	 * "getMin",	[]
	 * "getMin",	[]
	 * "push",		[-20]
	 * "getMin",
	 * "getMin",
	 * "top",
	 * "getMin",
	 * "pop",
	 * "push",		[10]
	 * "push",		[-7]
	 * "getMin",
	 * "push",		[-7]
	 * "pop",
	 * "top",
	 * "getMin",
	 * "pop"]
	 * [,,,[],[],[],[],[],[10],[-7],[],[-7],[],[],[],[]]
	 */
	private void aaaaa() {
		MinStack minStack = new MinStack();
		minStack.push(-10);
		minStack.push(14);
		minStack.push(-20);
		System.out.println(minStack.getMin());
		System.out.println(minStack.getMin());
		minStack.top();
		minStack.pop();
		minStack.push(10);
		minStack.push(-7);
		System.out.println(Arrays.toString(minStack.arr));
		System.out.println(minStack.getMin());
	}

//	class MinStack {
//		private List<Integer> list;
//		private TreeSet<Integer> set;
//
//		/** initialize your data structure here. */
//		public MinStack() {
//			list = new ArrayList<>();
//			set = new TreeSet<>((o1, o2) -> {
//				int result = o1.compareTo(o2);
//				if (result == 0) {
//					return 1;
//				}
//				return result;
//			});
//		}
//
//		public void push(int x) {
//			list.add(x);
//			set.add(x);
//		}
//
//		public void pop() {
//			Integer ele = list.remove(list.size() - 1);
//			set.remove(ele);
//		}
//
//		public int top() {
//			return list.get(list.size() - 1);
//		}
//
//		public int getMin() {
//			return set.first();
//		}
//	}

	class MinStack {
		private int[] arr;
		private int index = -1;
		private int min = Integer.MAX_VALUE;

		/** initialize your data structure here. */
		public MinStack() {
			arr = new int[7500];
		}

		public void push(int x) {
			arr[++index] = x;
			if (min < Integer.MAX_VALUE) {
				min = x < min ? x : min;
			}
		}

		public void pop() {
			if (arr[index] <= min) {
				min = Integer.MAX_VALUE;
			}
			index--;
		}

		public int top() {
			return arr[index];
		}

		public int getMin() {
			if (min == Integer.MAX_VALUE) {
				for (int i = 0; i <= index; i++) {
					min = arr[i] < min ? arr[i] : min;
				}
				return min;
			}
			return min;
		}
	}
}

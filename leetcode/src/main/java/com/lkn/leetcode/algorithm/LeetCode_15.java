package com.lkn.leetcode.algorithm;

import org.junit.Test;

import java.util.*;

/**
 * 三数之和
 *
 * @author likangning
 * @since 2020/1/15 上午8:45
 */
public class LeetCode_15 {

	@Test
	public void test() {
		/**
		 * [[-4,-2,6],[-2,-2,4],[-4,-2,6],[-2,-2,4],[-4,-2,6],[-2,0,2],[-4,0,4],[-4,1,3],[-4,2,2]]
		 * [[-4,-2,6],[-4,0,4],[-4,1,3],[-4,2,2],[-2,-2,4],[-2,0,2]]
		 */
		Object result = new LeetCode_15.Solution().threeSum(new int[]{0,0,0});
		System.out.println(result);
	}

	class Solution {
		public List<List<Integer>> threeSum(int[] nums) {
			Arrays.sort(nums);
			List<List<Integer>> result = new ArrayList<>();
			if (nums.length >= 3) {
				for (int i = 0; i < nums.length; i++) {
					if (nums[i] > 0) {
						break;
					}
					if (i > 0 && nums[i] == nums[i - 1]) {
						continue;
					}
					int leftIndex = i + 1;
					int rightIndex = nums.length - 1;
					int lastLeftIndex = -1;
					int lastRightIndex = -1;
					while (leftIndex < rightIndex) {
						int sum = nums[leftIndex] + nums[rightIndex] + nums[i];
						if (sum == 0) {
							if (lastLeftIndex == -1 || nums[lastLeftIndex] != nums[leftIndex] || nums[lastRightIndex] != nums[rightIndex]) {
								result.add(Arrays.asList(nums[i], nums[leftIndex], nums[rightIndex]));
							}
							lastLeftIndex = leftIndex;
							lastRightIndex = rightIndex;
							leftIndex++;
							rightIndex--;
						} else if (sum < 0) {
							leftIndex++;
						} else {
							rightIndex--;
						}
					}
				}
			}
			return result;
		}
	}

//	class Solution {
//		public List<List<Integer>> threeSum(int[] nums) {
//			Arrays.sort(nums);
//			List<List<Integer>> result = new ArrayList<>();
//			if (nums.length >= 3) {
//				for (int i = 1; i < nums.length - 1; i++) {
//					int leftIndex = 0;
//					int rightIndex = nums.length - 1;
//					if (i >= 2 && nums[i] == nums[i - 1]) {
//						if (nums[i] < 0) {
//							if (nums[i - 1] == nums[i - 2]) {
//								continue;
//							} else {
//								leftIndex = i - 1;
//							}
//						} else {
//							continue;
//						}
//					}
//					while (true) {
//						if (nums[i] < 0) {
//							if (leftIndex == i || nums[rightIndex] < 0) {
//								break;
//							}
//						} else {
//							if (nums[leftIndex] >= 0 || rightIndex == i) {
//								break;
//							}
//						}
//
//						int sum = nums[leftIndex] + nums[i] + nums[rightIndex];
//						if (sum == 0) {
//							if (result.size() > 0) {
//								List<Integer> last = result.get(result.size() - 1);
//								if (last.get(0) == nums[leftIndex] && last.get(1) == nums[i] && last.get(2) == nums[rightIndex]) {
//									leftIndex++;
//									rightIndex--;
//									continue;
//								}
//							}
//							result.add(Arrays.asList(nums[leftIndex], nums[i], nums[rightIndex]));
//							leftIndex++;
//							rightIndex--;
//						} else if (sum < 0) {
//							leftIndex++;
//						} else {
//							rightIndex--;
//						}
//					}
//
//					if (nums[i] == 0) {
//						int zeroNum = 1;
//						int tmp = i - 1;
//						while (tmp >= 0 && nums[tmp] == 0) {
//							zeroNum++;
//							tmp--;
//						}
//						tmp = i + 1;
//						while (tmp < nums.length && nums[tmp] == 0) {
//							zeroNum++;
//							tmp++;
//						}
//						if (zeroNum >= 3) {
//							result.add(Arrays.asList(0, 0, 0));
//							i = tmp - 1;
//						}
//					}
//				}
//			}
//			return result;
//		}
//	}

//	class Solution {
//		public List<List<Integer>> threeSum(int[] nums) {
//			Arrays.sort(nums);
//			List<List<Integer>> result = new ArrayList<>();
//			if (nums.length >= 3) {
//				int positiveIndex = -1;
//				int zeroNum = 0;
//				Set<Integer> set = new HashSet<>(nums.length);
//				for (int i = 0; i < nums.length; i++) {
//					if (nums[i] >= 0 && positiveIndex == -1) {
//						positiveIndex = i;
//					}
//					set.add(nums[i]);
//					if (nums[i] == 0) {
//						zeroNum++;
//					}
//				}
//				if (zeroNum >= 3) {
//					List<Integer> single = new ArrayList<>(3);
//					single.add(0);
//					single.add(0);
//					single.add(0);
//					result.add(single);
//				}
//				if (positiveIndex <= 0) {
//					return result;
//				}
//				for (int cycle = 0; cycle < 2; cycle++) {
//					int begin = cycle == 0 ? 0 : positiveIndex;
//					int end = cycle == 0 ? positiveIndex : nums.length;
//					boolean targetPositive = cycle == 0;
//					for (int i = begin; i < end; i++) {
//						if (i > 0 && nums[i] == nums[i - 1]) {
//							continue;
//						}
//						for (int j = i + 1; j < end; j++) {
//							int targetNum = -(nums[i] + nums[j]);
//							if (set.contains(targetNum) && (targetPositive ? targetNum >= 0 : targetNum < 0)) {
//								if (result.size() > 0) {
//									List<Integer> last = result.get(result.size() - 1);
//									if (last.get(0) == nums[i] && last.get(1) == nums[j] && last.get(2) == targetNum) {
//										continue;
//									}
//								}
//								List<Integer> single = new ArrayList<>(3);
//								single.add(nums[i]);
//								single.add(nums[j]);
//								single.add(targetNum);
//								result.add(single);
//							}
//						}
//					}
//				}
//			}
//			return result;
//		}
//	}

//	class Solution {
//		public List<List<Integer>> threeSum(int[] nums) {
//			Arrays.sort(nums);
//			List<List<Integer>> result = new ArrayList<>();
//			if (nums.length >= 3) {
//				for (int i = 1; i < nums.length - 1; i++) {
//					int left = i - 1;
//					int right = i + 1;
//					do {
//						int sum = nums[left] + nums[right] + nums[i];
//						if (sum == 0) {
//							boolean containsSame = false;
//							for (List<Integer> list : result) {
//								if (list.get(0) == nums[left] && list.get(1) == nums[i]) {
//									containsSame = true;
//									break;
//								}
//							}
//							if (!containsSame) {
//								List<Integer> single = new ArrayList<>();
//								single.add(nums[left]);
//								single.add(nums[i]);
//								single.add(nums[right]);
//								result.add(single);
//							}
//							left--;
//							right++;
//						} else if (sum < 0) {
//							right++;
//						} else {
//							left--;
//						}
//					} while (left >= 0 && right < nums.length);
//				}
//			}
//			return result;
//		}
//	}
}

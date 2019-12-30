package com.lkn;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * @author likangning
 * @since 2019/12/23 上午8:41
 */
public class LeedCodeTest {

	@Test
	public void test() throws Exception {
		FizzBuzz fizzBuzz = new FizzBuzz(15);
		Runnable thread3 = new Thread(() -> System.out.println("fizz, "));
		Runnable thread5 = new Thread(() -> System.out.println("buzz, "));
		Runnable thread15 = new Thread(() -> System.out.println("fizzbuzz, "));
		Thread thread33 = new Thread(() -> {
			try {
				fizzBuzz.fizz(thread3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		thread33.start();

		Thread thread55 = new Thread(() -> {
			try {
				fizzBuzz.buzz(thread5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		thread55.start();

		Thread thread1515 = new Thread(() -> {
			try {
				fizzBuzz.fizzbuzz(thread15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		thread1515.start();

		Thread threadMain = new Thread(() -> {
			try {
				fizzBuzz.number();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		threadMain.start();

		thread33.join();
		thread55.join();
		thread1515.join();
		threadMain.join();

	}

	private class FizzBuzz {
		private int n;

		private volatile int currentN = 1;
		private Semaphore numberSemaphore = new Semaphore(1);
		private Semaphore threeSemaphore = new Semaphore(0);
		private Semaphore fiveSemaphore = new Semaphore(0);
		private Semaphore threeFiveSemaphore = new Semaphore(0);

		public FizzBuzz(int n) {
			this.n = n;
		}

		// printFizz.run() outputs "fizz".
		public void fizz(Runnable printFizz) throws InterruptedException {
			while (n >= 3) {
				threeSemaphore.acquire();
				int tmp = currentN++;
				printFizz.run();
				numberSemaphore.release();
				if (tmp + 3 > n) {
					break;
				} else if (tmp + 3 == n && n % 5 == 0) {
					break;
				}
			}
			System.out.println("fizz over");
		}

		// printBuzz.run() outputs "buzz".
		public void buzz(Runnable printBuzz) throws InterruptedException {
			while (n >= 5) {
				fiveSemaphore.acquire();
				int tmp = currentN++;
				printBuzz.run();
				numberSemaphore.release();
				if (tmp + 5 > n) {
					break;
				} else if (tmp + 5 == n && n % 3 == 0) {
					break;
				}
			}
			System.out.println("buzz over");
		}

		// printFizzBuzz.run() outputs "fizzbuzz".
		public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
			while (n >= 15) {
				threeFiveSemaphore.acquire();
				int tmp = currentN++;
				printFizzBuzz.run();
				numberSemaphore.release();
				if (tmp + 15 > n) {
					break;
				}
			}
			System.out.println("fizzbuzz over");
		}

		// printNumber.accept(x) outputs "x", where x is an integer.
		public void number() throws InterruptedException {
			while (true) {
				numberSemaphore.acquire();
				if (currentN > n) {
					break;
				}
				if (currentN % 3 == 0 && currentN % 5 == 0) {
					threeFiveSemaphore.release();
				} else if (currentN % 3 == 0) {
					threeSemaphore.release();
				} else if (currentN % 5 == 0) {
					fiveSemaphore.release();
				} else {
					System.out.println(currentN + ", ");
					numberSemaphore.release();
					currentN++;
				}
			}
			System.out.println("number over");
		}
	}






//	class DiningPhilosophers {
//
//		private volatile Semaphore[] locks = {new Semaphore(1), new Semaphore(1),
//				new Semaphore(1), new Semaphore(1), new Semaphore(1)};
//
//		public DiningPhilosophers() {
//
//		}
//
//		// call the run() method of any runnable to execute its code
//		public void wantsToEat(int philosopher,
//													 Runnable pickLeftFork,
//													 Runnable pickRightFork,
//													 Runnable eat,
//													 Runnable putLeftFork,
//													 Runnable putRightFork) throws InterruptedException {
//
//			int nextNum = (philosopher + 1) % 5;
//			Semaphore leftSemaphore = locks[philosopher > nextNum ? nextNum : philosopher];
//			Semaphore rightSemaphore = locks[philosopher > nextNum ? philosopher : nextNum];
//			Runnable firstPickUp = philosopher == 4 ? pickRightFork : pickLeftFork;
//			Runnable secondPickUp = philosopher == 4 ? pickLeftFork : pickRightFork;
//			Runnable firstPutDown = philosopher == 4 ? putRightFork : putLeftFork;
//			Runnable secondPutDown = philosopher == 4 ? putLeftFork : putRightFork;
//			leftSemaphore.acquire();
//			firstPickUp.run();
//			rightSemaphore.acquire();
//			secondPickUp.run();
//			eat.run();
//			firstPutDown.run();
//			rightSemaphore.release();
//			secondPutDown.run();
//			leftSemaphore.release();
//		}
//	}

	class DiningPhilosophers {

		private volatile Semaphore[] locks = {new Semaphore(1), new Semaphore(1),
				new Semaphore(1), new Semaphore(1), new Semaphore(1)};

		public DiningPhilosophers() {

		}

		// call the run() method of any runnable to execute its code
		public void wantsToEat(int philosopher,
													 Runnable pickLeftFork,
													 Runnable pickRightFork,
													 Runnable eat,
													 Runnable putLeftFork,
													 Runnable putRightFork) throws InterruptedException {

			synchronized (DiningPhilosophers.class) {
				pickLeftFork.run();
				pickRightFork.run();
				eat.run();
				putLeftFork.run();
				putRightFork.run();
			}

			if (philosopher != 4) {
				locks[philosopher].acquire();
				locks[philosopher + 1].acquire();
				pickLeftFork.run();
				pickRightFork.run();
				eat.run();
				putLeftFork.run();
				putRightFork.run();
				locks[philosopher + 1].release();
				locks[philosopher].release();
			} else {
				locks[0].acquire();
				locks[philosopher].acquire();
				pickRightFork.run();
				pickLeftFork.run();
				eat.run();
				putRightFork.run();
				putLeftFork.run();
				locks[philosopher].release();
				locks[0].release();
			}



			int nextNum = (philosopher + 1) % 5;
			Semaphore firstSemaphore = locks[nextNum > philosopher ? philosopher : nextNum];
			Semaphore secondSemaphore = locks[nextNum > philosopher ? nextNum : philosopher];
			Runnable firstPickUp = philosopher == 4 ? pickRightFork : pickLeftFork;
			Runnable secondPickUp = philosopher == 4 ? pickLeftFork : pickRightFork;
			Runnable firstPutDown = philosopher == 4 ? putRightFork : putLeftFork;
			Runnable secondPutDown = philosopher == 4 ? putLeftFork : putRightFork;
			firstSemaphore.acquire();
			firstPickUp.run();
			secondSemaphore.acquire();
			secondPickUp.run();
			eat.run();
			firstPutDown.run();
			secondSemaphore.release();
			secondPutDown.run();
			firstSemaphore.release();
		}
	}

//	class Solution {
//		private Map<Integer, Integer> map = new HashMap<>();
//		public int[] twoSum(int[] nums, int target) {
//			for (int i = 0; i < nums.length; i++) {
//				int num = nums[i];
//				if (map.containsKey(num)) {
//					return new int[]{map.get(num), i};
//				} else {
//					map.put(target - num, i);
//				}
//			}
//			return null;
//		}
//	}

	public class ListNode {
		int val;
		ListNode next;
		ListNode(int x) { val = x; }
	}
	/**
	 * Definition for singly-linked list.
	 * public class ListNode {
	 *     int val;
	 *     ListNode next;
	 *     ListNode(int x) { val = x; }
	 * }
	 */
//	class Solution {
//		private int flag = 0;
//		public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
//			ListNode result = null;
//			ListNode next = null;
//			while (true) {
//				if (l1 == null && l2 == null && flag == 0) {
//					break;
//				}
//				int l1Val = l1 == null ? 0 : l1.val;
//				int l2Val = l2 == null ? 0 : l2.val;
//				int sum = l1Val + l2Val + flag;
//				if (sum < 10) {
//					flag = 0;
//				} else {
//					flag = 1;
//					sum = sum - 10;
//				}
//				ListNode tmp = new ListNode(sum);
//				if (result == null) {
//					result = tmp;
//					next = tmp;
//				} else {
//					next.next = tmp;
//					next = tmp;
//				}
//				if (l1 != null) {
//					l1 = l1.next;
//				}
//				if (l2 != null) {
//					l2 = l2.next;
//				}
//			}
//			return result;
//		}
//	}

//	@Test
//	public void myTest() {
//		String str = "pwwkew";
//		int len = new Solution().lengthOfLongestSubstring(str);
//		System.out.println(len);
//	}
//	class Solution {
//
//		public int lengthOfLongestSubstring(String s) {
//			int result = 0;
//			int beginIndex = 0;
//			int endIndex = 0;
//			int invalid = 0;
//			char[] chars = s.toCharArray();
//			int[] exist = new int[127];
//			for (int i = 0; i < chars.length; i++) {
//				char currChar = chars[i];
//				endIndex = i;
//				if (exist[currChar] == 0 || exist[currChar] <= invalid) {
//					result = endIndex - beginIndex + 1 > result ? endIndex - beginIndex + 1 : result;
//				} else {
//					beginIndex = exist[currChar];
//					invalid = beginIndex;
//				}
//				exist[currChar] = i + 1;
//			}
//
//			return result;
//		}
//	}

	/**
	 * 1 2 3
	 * 1 2 3 4
	 */
	@Test
	public void myTest() {
		int[] nums1 = {1};
		int[] nums2 = {};
		double result = new Solution().findMedianSortedArrays(nums1, nums2);
		System.out.println(result);
	}

	class Solution {
		public double findMedianSortedArrays(int[] nums1, int[] nums2) {
			int totalLen = nums1.length + nums2.length;
			if (totalLen == 1) {
				return nums1.length == 1 ? nums1[0] : nums2[0];
			} else if (totalLen == 2) {
				int sum = 0;
				for (int i : nums1) {
					sum += i;
				}
				for (int i : nums2) {
					sum += i;
				}
				return (double)sum / (double)2;
			}
			boolean singleMidd = totalLen % 2 == 1;
			int middleNum = singleMidd ? totalLen / 2 + 1 : totalLen / 2;
			int num1Index = 0;
			int num2Index = 0;
			int middleBefore = 0;
			int middleAfter = 0;
			boolean arr1Over = nums1.length == 0;
			boolean arr2Over = nums2.length == 0;
			for (int i = 0; i < middleNum; i++) {
				if (arr1Over) {
					num2Index++;
				} else if (arr2Over) {
					num1Index++;
				} else {
					if (nums1[num1Index] <= nums2[num2Index]) {
						num1Index++;
						if (num1Index >= nums1.length) {
							arr1Over = true;
						}
					} else {
						num2Index++;
						if (num2Index >= nums2.length) {
							arr2Over = true;
						}
					}
				}
				if (i == middleNum - 2) {
					if (arr1Over) {
						middleBefore = nums2[num2Index];
					} else if (arr2Over) {
						middleBefore = nums1[num1Index];
					} else {
						middleBefore = nums1[num1Index] < nums2[num2Index] ? nums1[num1Index] : nums2[num2Index];
					}
				} else if (i == middleNum - 1) {
					if (arr1Over) {
						middleAfter = nums2[num2Index];
					} else if (arr2Over) {
						middleAfter = nums1[num1Index];
					} else {
						middleAfter = nums1[num1Index] < nums2[num2Index] ? nums1[num1Index] : nums2[num2Index];
					}
				}
			}
			return singleMidd ? middleBefore : (double) (middleBefore + middleAfter) / 2;
		}
	}
}



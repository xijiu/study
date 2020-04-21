//package com.lkn.leetcode.algorithm;
//
//import org.junit.Test;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * @author likangning
// * @since 2020/4/20 下午5:10
// */
//public class LeetCode_200_2 {
//
//	@Test
//	public void test() {
//		Solution solution = new Solution();
////		char[][] grid = {
////				{'1','1','1','1','0'},
////				{'1','1','0','1','0'},
////				{'1','1','0','0','0'},
////				{'0','0','0','0','0'},
////		};
////		char[][] grid = {
////				{'1','1','0','0','0'},
////				{'1','1','0','0','0'},
////				{'0','0','1','0','0'},
////				{'0','0','0','1','1'},
////		};
////		char[][] grid = {
////				{'1','1','1'},
////				{'0','1','0'},
////				{'1','1','1'}
////		};
//		char[][] grid = {
//				{'1','0','1','1','1'},
//				{'1','0','1','0','1'},
//				{'1','1','1','0','1'},
//		};
//		int num = solution.numIslands(grid);
//		System.out.println(num);
//		System.out.println((int)'0');
//		System.out.println((int)'1');
//		System.out.println((int)'2');
//	}
//
//	class Solution {
//		private int xNum = 0;
//		private int yNum = 0;
//		private Set<Integer> set = new HashSet<>();
//		public int numIslands(char[][] grid) {
//			int islandNum = 0;
//			xNum = grid.length;
//			if (xNum == 0) {
//				return 0;
//			}
//			yNum = grid[0].length;
//
//			for (int i = 0; i < xNum; i++) {
//				for (int j = 0; j < yNum; j++) {
//					if (grid[i][j] == 49) {
//						set.add((i + 1) * (j + 1));
//					}
//				}
//			}
//
//
//		}
//
//		private void markIsland(char[][] grid, int i, int j) {
//			if (i >= xNum || j >= yNum || i < 0 || j < 0) {
//				return;
//			}
//			if (grid[i][j] == 50 || grid[i][j] == 48) {
//				return;
//			}
//			grid[i][j] = 50;
////			print(grid);
//			if (i >= 1) {
//				markIsland(grid, i - 1, j);
//			}
//			if (i + 1 < xNum) {
//				markIsland(grid, i + 1, j);
//			}
//			if (j >= 1) {
//				markIsland(grid, i, j - 1);
//			}
//			if (j + 1 < yNum) {
//				markIsland(grid, i, j + 1);
//			}
//		}
//
//		private void print(char[][] grid) {
//			for (int i = 0; i < xNum; i++) {
//				for (int j = 0; j < yNum; j++) {
//					System.out.print(grid[i][j]);
//				}
//				System.out.println();
//			}
//			System.out.println("*************");
//		}
//	}
//}

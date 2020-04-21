package com.lkn.leetcode.algorithm;

import org.junit.Test;

/**
 * @author likangning
 * @since 2020/4/20 下午5:10
 */
public class LeetCode_200 {

	@Test
	public void test() {
		Solution solution = new Solution();
//		char[][] grid = {
//				{'1','1','1','1','0'},
//				{'1','1','0','1','0'},
//				{'1','1','0','0','0'},
//				{'0','0','0','0','0'},
//		};
//		char[][] grid = {
//				{'1','1','0','0','0'},
//				{'1','1','0','0','0'},
//				{'0','0','1','0','0'},
//				{'0','0','0','1','1'},
//		};
//		char[][] grid = {
//				{'1','1','1'},
//				{'0','1','0'},
//				{'1','1','1'}
//		};
//		char[][] grid = {
//				{'1','0','1','1','1'},
//				{'1','0','1','0','1'},
//				{'1','1','1','0','1'},
//		};
		char[][] grid = {
				{'0','1','0'},
				{'1','0','1'},
				{'0','1','0'}
		};
		int num = solution.numIslands(grid);
		System.out.println(num);
	}

	class Solution {
		private int xNum = 0;
		private int yNum = 0;
		private char[][] grid = null;
		public int numIslands(char[][] grid) {
			int islandNum = 0;
			xNum = grid.length;
			if (xNum == 0) {
				return 0;
			}
			yNum = grid[0].length;
			this.grid = grid;
			int x = 0;
			int y = 0;
			while (true) {
				boolean isContinue = false;
				for (int i = x; i < xNum && !isContinue; i++) {
					for (int j = 0; j < yNum; j++) {
						if (grid[i][j] == 49) {
							x = i;
							y = j;
							isContinue = true;
							break;
						}
					}
				}
				if (!isContinue) {
					return islandNum;
				} else {
					markIsland(x, y);
					islandNum++;
				}
			}
		}

		private void markIsland(int i, int j) {
			if (i >= xNum || j >= yNum || i < 0 || j < 0) {
				return;
			}
			if (grid[i][j] == 50 || grid[i][j] == 48) {
				return;
			}
			grid[i][j] = 50;
			if (i >= 1) {
				markIsland(i - 1, j);
			}
			if (i + 1 < xNum) {
				markIsland(i + 1, j);
			}
			if (j >= 1) {
				markIsland(i, j - 1);
			}
			if (j + 1 < yNum) {
				markIsland(i, j + 1);
			}
		}
	}
}

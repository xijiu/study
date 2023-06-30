package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.Conf;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 炮
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Cannons extends AbstractChessPiece {
	public static final int RED_TYPE = 6;
	public static final int BLACK_TYPE = 13;

	public Cannons(Role role) {
		this(null, role);
	}

	public Cannons(String id, Role role) {
		super(id, role);
		this.setDefaultVal(240);
		setValues();
		this.setName("炮");
		this.setShowName("炮");
		initNum(role, RED_TYPE, BLACK_TYPE);
	}

	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{100,100, 96, 91, 90, 91, 96,100,100},
				{ 98, 98, 96, 92, 89, 92, 96, 98, 98},
				{ 97, 97, 96, 91, 92, 91, 96, 97, 97},
				{ 96, 99, 99, 98,100, 98, 99, 99, 96},
				{ 96, 96, 96, 96,100, 96, 96, 96, 96},
				{ 95, 96, 99, 96,100, 96, 99, 96, 95},
				{ 96, 96, 96, 96, 96, 96, 96, 96, 96},
				{ 97, 96,100, 99,101, 99,100, 96, 97},
				{ 96, 97, 98, 98, 98, 98, 98, 97, 96},
				{ 96, 96, 97, 99, 99, 99, 97, 96, 96}
		};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}





	public static void main(String[] args) {
		Integer[] arr = {5, 1, 2, 3, 4, 7, 6};
		Arrays.sort(arr, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return 0;
			}
		});



		int[] nums={4,5,7,8,0,3};
		Integer[] numsArr = new Integer[nums.length];
		for (int i = 0; i < nums.length; i++) {
			numsArr[i] = nums[i];
		}
		Arrays.sort(numsArr,new Comparator<Integer>(){
			public int compare(Integer num1,Integer num2){
				return num1-num2;
			}
		});
		for (Integer integer : numsArr) {
			System.out.println(integer);
		}


	}




	@Override
	public byte[] getReachablePositions(int currPosition, ChessBoard board, boolean containsProtectedPiece, int level) {
		reachablePositions = reachableHelper[level];
		reachableNum = 0;
		int currX = ChessTools.fetchX(currPosition);
		int currY = ChessTools.fetchY(currPosition);

		addAllCase(currX, currY, containsProtectedPiece, board.getBoard(), board.getInvertBoard());
		reachablePositions[0] = (byte) reachableNum;
		return reachablePositions;
	}

	@Override
	public boolean kingCheck(ChessBoard board, int position) {
		int currX = ChessTools.fetchX(position);
		int currY = ChessTools.fetchY(position);
		if (isRed()) {
			if (currX >= 7 || (currY >= 3 && currY <= 5)) {
				return isKingCheck(board, currX, currY);
			}
		} else {
			if (currX <= 2 || (currY >= 3 && currY <= 5)) {
				return isKingCheck(board, currX, currY);
			}
		}
		return false;
	}

	private boolean isKingCheck(ChessBoard board, int currX, int currY) {
		byte[][] boardArr = board.getBoard();
		int kingPos = findKingPos(boardArr, getPLAYER_ROLE().nextRole());
		int kingX = ChessTools.fetchX(kingPos);
		int kingY = ChessTools.fetchY(kingPos);
		if (kingX == currX) {
			int startY = Math.min(kingY, currY);
			int endY = Math.max(kingY, currY);
			int num = 0;
			for (int y = startY + 1; y < endY; y++) {
				if (boardArr[currX][y] != -1) {
					num++;
					if (num >= 2) {
						break;
					}
				}
			}
			return num == 1;
		}
		if (kingY == currY) {
			int startX = Math.min(kingX, currX);
			int endX = Math.max(kingX, currX);
			int num = 0;
			for (int x = startX + 1; x < endX; x++) {
				if (boardArr[currX][x] != -1) {
					num++;
					if (num >= 2) {
						break;
					}
				}
			}
			return num == 1;
		}
		return false;
	}

	/**
	 * 炮跟将中间如果没有棋子，那么威力将大增
	 */
	@Override
	public int valuation(ChessBoard board, int position, Role role) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		if (Conf.SIMPLE_VALUE) {
			return 20 + arr[x][y];
		}
		int hollowVal = calcHollow(x, y, board.getBoard(), board.getInvertBoard());
		int eatenVal = eatenValue(board, position, role);
//		int eatenVal = 0;
		return Math.max(0, hollowVal + 20 + arr[x][y] - eatenVal);
	}

	/**
	 * 计算是否存在空心炮，以及其威力
	 */
	private int calcHollow(int currX, int currY, byte[][] board, byte[][] invertBoard) {
		int targetPos = findKingPositionByName(board);
		int targetX = ChessTools.fetchX(targetPos);
		int targetY = ChessTools.fetchY(targetPos);

		if (targetY == currY) {
			int beginX = Math.min(targetX, currX);
			int endX = Math.max(targetX, currX);
			for (int x = beginX + 1; x < endX; x++) {
				byte type = invertBoard[currY][x];
				if (type != -1) {
					return 0;
				}
			}
			int abs = Math.abs(targetX - currX);
			if (abs >= 2) {
				return calcHollowValue(abs);
			}
			return 0;
		}
		if (targetX == currX) {
			int beginY = Math.min(targetY, currY);
			int endY = Math.max(targetY, currY);
			for (int y = beginY + 1; y < endY; y++) {
				byte type = board[currX][y];
				if (type != -1) {
					return 0;
				}
			}
			int abs = Math.abs(targetY - currY);
			if (abs >= 2) {
				return calcHollowValue(abs);
			}
			return 0;
		}
		return 0;
	}

	private int calcHollowValue(int gap) {
		return 20 * gap;
	}

	@Override
	public int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2) {
		int x = ChessTools.fetchX(startPos);
		int y = ChessTools.fetchY(startPos);
		int num = ChessTools.transToNum(cmd2);
		if (cmd1 == '进') {
			x = this.isRed() ? x + num : x - num;
		} else if (cmd1 == '退') {
			x = this.isRed() ? x - num : x + num;
		} else if (cmd1 == '平') {
			y = ChessTools.transLineToY(cmd2, this.getPLAYER_ROLE());
		} else {
			throw new RuntimeException();
		}
		return ChessTools.toPosition(x, y);
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? RED_TYPE : BLACK_TYPE);
	}

	private void addAllCase(int currX, int currY, boolean containsProtectedPiece,
							byte[][] board, byte[][] invertBoard) {
		boolean hasRack = false;
		// 向上找
		for (int x = currX + 1; x < 10; x++) {
			int position = ChessTools.toPosition(x, currY);
			if (hasRack) {
				byte type = invertBoard[currY][x];
				if (type != -1) {
					if (isEnemy(this, type)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				}
			} else {
				if (invertBoard[currY][x] == -1) {
					recordReachablePosition(position);
				} else {
					hasRack = true;
				}
			}
		}

		// 向下找
		hasRack = false;
		for (int x = currX - 1; x >= 0; x--) {
			int position = ChessTools.toPosition(x, currY);
			if (hasRack) {
				byte type = invertBoard[currY][x];
				if (type != -1) {
					if (isEnemy(this, type)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				}
			} else {
				if (invertBoard[currY][x] == -1) {
					recordReachablePosition(position);
				} else {
					hasRack = true;
				}
			}
		}

		// 向右找
		hasRack = false;
		for (int y = currY + 1; y < 9; y++) {
			int position = ChessTools.toPosition(currX, y);
			if (hasRack) {
				byte type = board[currX][y];
				if (type != -1) {
					if (isEnemy(this, type)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				}
			} else {
				if (board[currX][y] == -1) {
					recordReachablePosition(position);
				} else {
					hasRack = true;
				}
			}
		}

		// 向左找
		hasRack = false;
		for (int y = currY - 1; y >= 0; y--) {
			int position = ChessTools.toPosition(currX, y);
			if (hasRack) {
				byte type = board[currX][y];
				if (type != -1) {
					if (isEnemy(this, type)) {
						recordReachablePosition(position);
					} else {
						if (containsProtectedPiece) {
							recordReachablePosition(position);
						}
					}
					break;
				}
			} else {
				if (board[currX][y] == -1) {
					recordReachablePosition(position);
				} else {
					hasRack = true;
				}
			}
		}
	}
}

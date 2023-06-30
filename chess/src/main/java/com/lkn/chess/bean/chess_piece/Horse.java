package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.Conf;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;

/**
 * 马
 * @author:likn1	Jan 5, 2016  3:54:47 PM
 */
public class Horse extends AbstractChessPiece {
	public static final int RED_TYPE = 2;
	public static final int BLACK_TYPE = 9;

	public Horse(Role role) {
		this(null, role);
	}
	
	public Horse(String id, Role role) {
		super(id, role);
		setValues();
		this.setDefaultVal(220);
		this.setName("马");
		this.setShowName("馬");
		initNum(role, RED_TYPE, BLACK_TYPE);
	}
	
	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{90, 90, 90, 96, 90, 96, 90, 90, 90},
				{90, 96,103, 97, 94, 97,103, 96, 90},
				{92, 98, 99,103, 99,103, 99, 98, 92},
				{93,108,100,107,100,107,100,108, 93},
				{90,100, 99,103,104,103, 99,100, 90},
				{90, 98,101,102,103,102,101, 98, 90},
				{92, 94, 98, 95, 98, 95, 98, 94, 92},
				{93, 92, 94, 95, 92, 95, 94, 92, 93},
				{85, 90, 92, 93, 78, 93, 92, 90, 85},
				{88, 85, 90, 88, 90, 88, 90, 85, 88}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? RED_TYPE : BLACK_TYPE);
	}

	@Override
	public boolean kingCheck(ChessBoard board, int position) {
		int kingPos = findKingPos(board.getAllPiece(), getPLAYER_ROLE().nextRole());
		int kingX = ChessTools.fetchX(kingPos);
		int kingY = ChessTools.fetchY(kingPos);
		int currX = ChessTools.fetchX(position);
		int currY = ChessTools.fetchY(position);
		byte[][] boardArr = board.getBoard();
		if (Math.abs(kingX - currX) == 1 && Math.abs(kingY - currY) == 2) {
			byte type = kingY > currY ? boardArr[currX][currY + 1] : boardArr[currX][currY - 1];
			return type == -1;
		}
		if (Math.abs(kingX - currX) == 2 && Math.abs(kingY - currY) == 1) {
			byte type = kingX > currX ? boardArr[currX + 1][currY] : boardArr[currX - 1][currY];
			return type == -1;
		}
		return false;
	}

	@Override
	public int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2) {
		int x = ChessTools.fetchX(startPos);
		int y = ChessTools.fetchY(startPos);
		int targetY = ChessTools.transLineToY(cmd2, this.isRed() ? Role.RED : Role.BLACK);
		if (cmd1 == '进') {
			if (this.isRed()) {
				if (Math.abs(y - targetY) == 1) {
					x = x + 2;
				} else {
					x = x + 1;
				}
			} else {
				if (Math.abs(y - targetY) == 1) {
					x = x - 2;
				} else {
					x = x - 1;
				}
			}
		} else if (cmd1 == '退') {
			if (this.isRed()) {
				if (Math.abs(y - targetY) == 1) {
					x = x - 2;
				} else {
					x = x - 1;
				}
			} else {
				if (Math.abs(y - targetY) == 1) {
					x = x + 2;
				} else {
					x = x + 1;
				}
			}
		} else {
			throw new RuntimeException();
		}
		return ChessTools.toPosition(x, targetY);
	}


	@Override
	public byte[] getReachablePositions(int currPosition, ChessBoard board, boolean containsProtectedPiece, int level) {
		reachablePositions = reachableHelper[level];
		reachableNum = 0;
		int currX = ChessTools.fetchX(currPosition);
		int currY = ChessTools.fetchY(currPosition);

		findReachablePositions(currX, currY, board.getBoard(), containsProtectedPiece);
		reachablePositions[0] = (byte) reachableNum;
		return reachablePositions;
	}

	/**
	 * 8个方向均需要检查
	 */
	private void findReachablePositions(int currX, int currY, byte[][] board, boolean containsProtectedPiece) {
		int targetX = currX - 1;
		int targetY = currY - 2;
		int legX = currX;
		int legY = currY - 1;
		tryJump(targetX, targetY, legX, legY, board, containsProtectedPiece);

		targetX = currX + 1;
		targetY = currY + 2;
		legX = currX;
		legY = currY + 1;
		tryJump(targetX, targetY, legX, legY, board, containsProtectedPiece);

		targetX = currX - 2;
		targetY = currY - 1;
		legX = currX - 1;
		legY = currY;
		tryJump(targetX, targetY, legX, legY, board, containsProtectedPiece);

		targetX = currX + 2;
		targetY = currY + 1;
		legX = currX + 1;
		legY = currY;
		tryJump(targetX, targetY, legX, legY, board, containsProtectedPiece);

		targetX = currX - 1;
		targetY = currY + 2;
		legX = currX;
		legY = currY + 1;
		tryJump(targetX, targetY, legX, legY, board, containsProtectedPiece);

		targetX = currX - 2;
		targetY = currY + 1;
		legX = currX - 1;
		legY = currY;
		tryJump(targetX, targetY, legX, legY, board, containsProtectedPiece);

		targetX = currX + 1;
		targetY = currY - 2;
		legX = currX;
		legY = currY - 1;
		tryJump(targetX, targetY, legX, legY, board, containsProtectedPiece);

		targetX = currX + 2;
		targetY = currY - 1;
		legX = currX + 1;
		legY = currY;
		tryJump(targetX, targetY, legX, legY, board, containsProtectedPiece);
	}

	private void tryJump(int targetX, int targetY, int legX, int legY, byte[][] board, boolean containsProtectedPiece) {
		if (isValid(targetX, targetY) && isValid(legX, legY)) {
			// 不拌马腿
			if (board[legX][legY] == -1) {
				byte type = board[targetX][targetY];
				if (type == -1 || isEnemy(this, type)) {
					recordReachablePosition(ChessTools.toPosition(targetX, targetY));
				}
				if (type != -1 && isFriend(this, type) && containsProtectedPiece) {
					recordReachablePosition(ChessTools.toPosition(targetX, targetY));
				}
			}
		}
	}

	@Override
	public int valuation(ChessBoard board, int position, Role role) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		if (Conf.SIMPLE_VALUE) {
			return arr[x][y];
		}
		byte num = getReachablePositions(position, board, true, 10)[0];
		int eatenVal = eatenValue(board, position, role);
//		int eatenVal = 0;
		return Math.max(0, arr[x][y] + num * 10 - eatenVal);
	}


}

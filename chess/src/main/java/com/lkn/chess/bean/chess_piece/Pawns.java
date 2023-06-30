package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.Conf;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;


/**
 * 兵
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Pawns extends AbstractChessPiece {
	public static final int RED_TYPE = 7;
	public static final int BLACK_TYPE = 14;
	public static final int BASE_VAL = 30;

	public Pawns(Role role) {
		this(null, role);
	}

	public Pawns(String id, Role role) {
		super(id, role);
		setValues();
		this.setDefaultVal(BASE_VAL);
		if(role == Role.RED) {	// 先手
			this.setName("兵");
			this.setShowName("兵");
		}else {
			this.setName("卒");
			this.setShowName("卒");
		}
		initNum(role, RED_TYPE, BLACK_TYPE);
	}
	
	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{  9,  9,  9, 11, 13, 11,  9,  9,  9},
				{ 19, 24, 34, 42, 44, 42, 34, 24, 19},
				{ 19, 24, 32, 37, 37, 37, 32, 24, 19},
				{ 19, 23, 27, 29, 30, 29, 27, 23, 19},
				{ 14, 18, 20, 27, 29, 27, 20, 18, 14},
				{  7,  0, 13,  0, 16,  0, 13,  0,  7},
				{  7,  0,  7,  0, 15,  0,  7,  0,  7},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? RED_TYPE : BLACK_TYPE);
	}

	@Override
	public int valuation(ChessBoard board, int position, Role role) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		if (Conf.SIMPLE_VALUE) {
			return arr[x][y];
		}

		int eatenVal = eatenValue(board, position, role);
//		int eatenVal = 0;
		return Math.max(0, arr[x][y] - eatenVal);
	}

	@Override
	public boolean kingCheck(ChessBoard board, int position) {
		int currX = ChessTools.fetchX(position);
		int currY = ChessTools.fetchY(position);
		if (isRed()) {
			if ((currY >= 2 && currY <= 6 && currX >= 7)
					|| (currY >= 3 && currY <= 5 && currX == 6)) {
				int kingPos = findKingPos(board.getAllPiece(), getPLAYER_ROLE().nextRole());
				int kingX = ChessTools.fetchX(kingPos);
				int kingY = ChessTools.fetchY(kingPos);
				if (kingY == currY && kingX - currX == 1) {
					return true;
				}
				if (kingX == currX && Math.abs(kingY - currY) == 1) {
					return true;
				}
			}
		} else {
			if ((currY >= 2 && currY <= 6 && currX <= 2)
					|| (currY >= 3 && currY <= 5 && currX == 3)) {
				int kingPos = findKingPos(board.getAllPiece(), getPLAYER_ROLE().nextRole());
				int kingX = ChessTools.fetchX(kingPos);
				int kingY = ChessTools.fetchY(kingPos);
				if (kingY == currY && currX - kingX == 1) {
					return true;
				}
				if (kingX == currX && Math.abs(kingY - currY) == 1) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2) {
		int x = ChessTools.fetchX(startPos);
		int y = ChessTools.fetchY(startPos);
		int num = ChessTools.transToNum(cmd2);
		if (cmd1 == '进') {
			x = this.isRed() ? x + num : x - num;
		} else if (cmd1 == '平') {
			y = ChessTools.transLineToY(cmd2, this.getPLAYER_ROLE());
		} else {
			throw new RuntimeException();
		}
		return ChessTools.toPosition(x, y);
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

	private void findReachablePositions(int currX, int currY, byte[][] allPiece, boolean containsProtectedPiece) {
		if (isRed()) {
			if (currX <= 4) {
				tryReach(currX + 1, currY, allPiece, containsProtectedPiece);
			} else {
				tryReach(currX + 1, currY, allPiece, containsProtectedPiece);
				tryReach(currX, currY - 1, allPiece, containsProtectedPiece);
				tryReach(currX, currY + 1, allPiece, containsProtectedPiece);
			}
		} else {
			if (currX >= 5) {
				tryReach(currX - 1, currY, allPiece, containsProtectedPiece);
			} else {
				tryReach(currX - 1, currY, allPiece, containsProtectedPiece);
				tryReach(currX, currY - 1, allPiece, containsProtectedPiece);
				tryReach(currX, currY + 1, allPiece, containsProtectedPiece);
			}
		}
	}

	private void tryReach(int x, int y, byte[][] allPiece, boolean containsProtectedPiece) {
		if (!isValid(x, y)) {
			return;
		}
		byte type = allPiece[x][y];
		if (type == -1 || isEnemy(this, type)) {
			recordReachablePosition(ChessTools.toPosition(x, y));
		}
		if (type != -1 && isFriend(this, type) && containsProtectedPiece) {
			recordReachablePosition(ChessTools.toPosition(x, y));
		}
	}
}

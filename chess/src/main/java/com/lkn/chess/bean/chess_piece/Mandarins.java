package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.Conf;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;


/**
 * 士
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Mandarins extends AbstractChessPiece {
	public static final int RED_TYPE = 4;
	public static final int BLACK_TYPE = 11;

	public Mandarins(Role role) {
		this(null, role);
	}

	public Mandarins(String id, Role role) {
		super(id, role);
		setValues();
		this.setDefaultVal(100);
		if(role == Role.RED){	// 先手
			this.setName("仕");
			this.setShowName("仕");
		}else {
			this.setName("士");
			this.setShowName("士");
		}
		initNum(role, RED_TYPE, BLACK_TYPE);
	}
	
	
	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0, 20,  0, 20,  0,  0,  0},
				{  0,  0,  0,  0, 23,  0,  0,  0,  0},
				{  0,  0,  0, 20,  0, 20,  0,  0,  0}
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
		return false;
	}

	@Override
	public int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2) {
		int x = ChessTools.fetchX(startPos);
		int y = ChessTools.fetchY(startPos);
		int targetY = ChessTools.transLineToY(cmd2, this.isRed() ? Role.RED : Role.BLACK);
		if (cmd1 == '进') {
			if (this.isRed()) {
				x += 1;
			} else {
				x -= 1;
			}
		} else if (cmd1 == '退') {
			if (this.isRed()) {
				x -= 1;
			} else {
				x += 1;
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

	private void findReachablePositions(int currX, int currY, byte[][] allPiece, boolean containsProtectedPiece) {
		if (isRed()) {
			if (currX == 1 && currY == 4) {
				tryReach(currX - 1, currY - 1, allPiece, containsProtectedPiece);
				tryReach(currX - 1, currY + 1, allPiece, containsProtectedPiece);
				tryReach(currX + 1, currY - 1, allPiece, containsProtectedPiece);
				tryReach(currX + 1, currY + 1, allPiece, containsProtectedPiece);
			} else {
				tryReach(1, 4, allPiece, containsProtectedPiece);
			}
		} else {
			if (currX == 8 && currY == 4) {
				tryReach(currX - 1, currY - 1, allPiece, containsProtectedPiece);
				tryReach(currX - 1, currY + 1, allPiece, containsProtectedPiece);
				tryReach(currX + 1, currY - 1, allPiece, containsProtectedPiece);
				tryReach(currX + 1, currY + 1, allPiece, containsProtectedPiece);
			} else {
				tryReach(8, 4, allPiece, containsProtectedPiece);
			}
		}
	}

	private void tryReach(int x, int y, byte[][] allPiece, boolean containsProtectedPiece) {
		byte type = allPiece[x][y];
		if (type == -1 || isEnemy(this, type)) {
			recordReachablePosition(ChessTools.toPosition(x, y));
		}
		if (type != -1 && isFriend(this, type) && containsProtectedPiece) {
			recordReachablePosition(ChessTools.toPosition(x, y));
		}
	}

}

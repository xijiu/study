package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.Conf;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;


/**
 * 车
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Rooks extends AbstractChessPiece {
	public static final int RED_TYPE = 1;
	public static final int BLACK_TYPE = 8;

	public Rooks(Role role) {
		this(null, role);
	}

	public Rooks(String id, Role role) {
		super(id, role);
		setValues();
		this.setDefaultVal(500);
		this.setName("车");
		this.setShowName("車");
		initNum(role, RED_TYPE, BLACK_TYPE);
	}

	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{206,208,207,213,214,213,207,208,206},
				{206,212,209,216,233,216,209,212,206},
				{206,208,207,214,216,214,207,208,206},
				{206,213,213,216,216,216,213,213,206},
				{208,211,211,214,215,214,211,211,208},
				{208,212,212,214,215,214,212,212,208},
				{204,209,204,212,214,212,204,209,204},
				{198,208,204,212,212,212,204,208,198},
				{200,208,206,212,200,212,206,208,200},
				{194,206,204,212,200,212,204,206,194}
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
		byte num = getReachablePositions(position, board, true, 10)[0];
		int eatenVal = eatenValue(board, position, role);
//		int eatenVal = 0;
		return Math.max(0, arr[x][y] + num * 2 - eatenVal);
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
			for (int y = startY + 1; y < endY; y++) {
				if (boardArr[currX][y] != -1) {
					return false;
				}
			}
			return true;
		}
		if (kingY == currY) {
			int startX = Math.min(kingX, currX);
			int endX = Math.max(kingX, currX);
			for (int x = startX + 1; x < endX; x++) {
				if (boardArr[x][currY] != -1) {
					return false;
				}
			}
			return true;
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
		byte type;
		// 向上找
		for (int i = currX + 1; i < 10; i++) {
			int position = ChessTools.toPosition(i, currY);
			if ((type = allPiece[i][currY]) == -1) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, type)) {
					recordReachablePosition(position);
				} else {
					if (containsProtectedPiece) {
						recordReachablePosition(position);
					}
				}
				break;
			}
		}
		// 向下找
		for (int i = currX - 1; i >= 0; i--) {
			int position = ChessTools.toPosition(i, currY);
			if ((type = allPiece[i][currY]) == -1) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, type)) {
					recordReachablePosition(position);
				} else {
					if (containsProtectedPiece) {
						recordReachablePosition(position);
					}
				}
				break;
			}
		}
		// 向右找
		for (int y = currY + 1; y < 9; y++) {
			int position = ChessTools.toPosition(currX, y);
			if ((type = allPiece[currX][y]) == -1) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, type)) {
					recordReachablePosition(position);
				} else {
					if (containsProtectedPiece) {
						recordReachablePosition(position);
					}
				}
				break;
			}
		}
		// 向左找
		for (int y = currY - 1; y >= 0; y--) {
			int position = ChessTools.toPosition(currX, y);
			if ((type = allPiece[currX][y]) == -1) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, type)) {
					recordReachablePosition(position);
				} else {
					if (containsProtectedPiece) {
						recordReachablePosition(position);
					}
				}
				break;
			}
		}
	}

}

package com.lkn.chess.bean.chess_piece;

import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;

/**
 * 将
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class King extends AbstractChessPiece {
	public static final int RED_TYPE = 5;
	public static final int BLACK_TYPE = 12;

	public King(Role role) {
		this(null, role);
	}

	public King(String id, Role role) {
		super(id, role);
		setValues();
		this.setDefaultVal(1000000);
		if(role == Role.RED){	// 先手
			this.setName("帅");
			this.setShowName("帅");
		}else {
			this.setName("将");
			this.setShowName("将");
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
				{  0,  0,  0,  1,  1,  1,  0,  0,  0},
				{  0,  0,  0,  2,  2,  2,  0,  0,  0},
				{  0,  0,  0, 11, 15, 11,  0,  0,  0}
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
		return false;
	}


	@Override
	public int valuation(ChessBoard board, int position, Role role) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		return 1000000 + arr[x][y];
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
		int enemyKingPos = findKingPos(allPiece, getPLAYER_ROLE().nextRole());
		int enemyKingX = ChessTools.fetchX(enemyKingPos);
		int enemyKingY = ChessTools.fetchY(enemyKingPos);
		tryReach(currX - 1, currY, allPiece, containsProtectedPiece, enemyKingX, enemyKingY);
		tryReach(currX + 1, currY, allPiece, containsProtectedPiece, enemyKingX, enemyKingY);
		tryReach(currX, currY - 1, allPiece, containsProtectedPiece, enemyKingX, enemyKingY);
		tryReach(currX, currY + 1, allPiece, containsProtectedPiece, enemyKingX, enemyKingY);

//		tryReachToEnemyKing(currX, currY, allPiece);
	}

	private void tryReachToEnemyKing(int currX, int currY, AbstractChessPiece[][] allPiece) {
		if (this.isRed()) {
			for (int x = currX + 1; x < 10; x++) {
				AbstractChessPiece piece = allPiece[x][currY];
				if (piece != null) {
					if (piece.getName().equals("将")) {
						recordReachablePosition(ChessTools.toPosition(x, currY));
					}
					return;
				}
			}
		} else {
			for (int x = currX - 1; x >= 0; x--) {
				AbstractChessPiece piece = allPiece[x][currY];
				if (piece != null) {
					if (piece.getName().equals("帅")) {
						recordReachablePosition(ChessTools.toPosition(x, currY));
					}
					return;
				}
			}
		}
	}

	private void tryReach(int x, int y, byte[][] allPiece, boolean containsProtectedPiece, int enemyKingX, int enemyKingY) {
		if (isInArea(x, y)) {
			// 两个老头不能见面
			if (enemyKingY == y) {
				boolean empty = true;
				int minX = Math.min(x, enemyKingX);
				int maxX = Math.max(x, enemyKingX);
				for (int tmpX = minX + 1; tmpX < maxX; tmpX++) {
					if (allPiece[tmpX][y] != -1) {
						empty = false;
						break;
					}
				}
				if (empty) {
					return;
				}
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

	private boolean isInArea(int x, int y) {
		if (this.isRed()) {
			return x >= 0 && x <= 2 && y >= 3 && y <= 5;
		} else {
			return x >= 7 && x <= 9 && y >= 3 && y <= 5;
		}
	}

}

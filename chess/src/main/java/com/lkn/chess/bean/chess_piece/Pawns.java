package com.lkn.chess.bean.chess_piece;

import java.util.HashMap;
import java.util.Map;

import com.lkn.chess.ArrPool;
import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.Position;

/**
 * 兵
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Pawns extends AbstractChessPiece {
	public static final int RED_NUM = 7;
	public static final int BLACK_NUM = 8;
	public static final int BASE_VAL = 50;

	public Pawns(Role role) {
		this(null, role);
	}

	public Pawns(String id, Role role) {
		super(id, role);
		setValues();
		this.setFightDefaultVal(BASE_VAL);
		if(role == Role.RED) {	// 先手
			this.setName("兵");
		}else {
			this.setName("卒");
		}
		initNum(role, RED_NUM, BLACK_NUM);
	}
	
	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{  0,  3,  6,  9, 12,  9,  6,  3,  0},
				{ 18, 36, 56, 80,120, 80, 56, 36, 18},
				{ 14, 26, 42, 60, 80, 60, 42, 26, 14},
				{ 10, 20, 30, 34, 40, 34, 30, 20, 10},
				{  6, 12, 18, 18, 20, 18, 18, 12,  6},
				{  6,  0,  8,  0, 10,  0,  8,  0,  6},
				{  6,  0,  8,  0, 10,  0,  8,  0,  6},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? 7 : 14);
	}

	@Override
	public int valuation(ChessBoard board, int position) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		return (int) (BASE_VAL * 2.5) + arr[x][y];
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
	public byte[] getReachablePositions(int currPosition, ChessBoard board) {
		reachableNum = 0;
		int currX = ChessTools.fetchX(currPosition);
		int currY = ChessTools.fetchY(currPosition);

		findReachablePositions(currX, currY, board.getAllPiece());
		reachablePositions[0] = (byte) reachableNum;
		byte[] result = ArrPool.borrow();
		System.arraycopy(reachablePositions, 0, result, 0, reachablePositions.length);
		return result;
	}

	private void findReachablePositions(int currX, int currY, Map<Integer, AbstractChessPiece> allPiece) {
		if (isRed()) {
			if (currX <= 4) {
				tryReach(currX + 1, currY, allPiece);
			} else {
				tryReach(currX + 1, currY, allPiece);
				tryReach(currX, currY - 1, allPiece);
				tryReach(currX, currY + 1, allPiece);
			}
		} else {
			if (currX >= 5) {
				tryReach(currX - 1, currY, allPiece);
			} else {
				tryReach(currX - 1, currY, allPiece);
				tryReach(currX, currY - 1, allPiece);
				tryReach(currX, currY + 1, allPiece);
			}
		}
	}

	private void tryReach(int x, int y, Map<Integer, AbstractChessPiece> allPiece) {
		if (!isValid(x, y)) {
			return;
		}
		AbstractChessPiece piece = allPiece.get(ChessTools.toPosition(x, y));
		if (piece == null || isEnemy(this, piece)) {
			recordReachablePosition(ChessTools.toPosition(x, y));
		}
	}

	/**
	 * 兵的可走路线需要区分是先手方还是后手方
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> reachableMap = new HashMap<String, Position>();
		Map<String, Position> allMap = board.getPositionMap();	// 获取棋盘的所有的位置集合

		Position position = this.getCurrPosition();
		Integer x = position.getX();
		Integer y = position.getY();
		if(this.getPLAYER_ROLE().equals(Role.RED)){	// 先手
			if(y < 5){	// 没有过河的情况，当前的兵只能有一个位置可走
				addValidPosition(x, y + 1, reachableMap, allMap);	// 只有向上
			}else {	// 过河的情况
				addValidPosition(x, y + 1, reachableMap, allMap);	// 1、向上
				addValidPosition(x - 1, y, reachableMap, allMap);	// 2、向左
				addValidPosition(x + 1, y, reachableMap, allMap);	// 3、向右
			}
		} else if(this.getPLAYER_ROLE().equals(Role.BLACK)){	// 后手
			if(y >= 5){	// 没有过河的情况，当前的兵只能有一个位置可走
				addValidPosition(x, y - 1, reachableMap, allMap);	// 只有向下
			}else{	// 过河的情况
				addValidPosition(x, y - 1, reachableMap, allMap);	// 1、向下
				addValidPosition(x - 1, y, reachableMap, allMap);	// 2、向左
				addValidPosition(x + 1, y, reachableMap, allMap);	// 3、向右
			}
		}
		return reachableMap;
	}

	/**
	 * 横坐标为x，纵坐标为y的位置是否可达
	 * 如果可达，那么将其加入reachableMap，否则不做任何操作
	 * @author:likn1	Jan 6, 2016  3:03:04 PM
	 * @param x
	 * @param y
	 * @param reachableMap
	 */
	private void addValidPosition(Integer x, Integer y, Map<String, Position> reachableMap, Map<String, Position> allMap) {
		Position UP_Position = allMap.get(ChessTools.getPositionID(x, y));
		boolean reachable = ChessTools.isPositionReachable(this.getPLAYER_ROLE(), UP_Position);
		if(reachable){	// 如果此位置没有棋子，或者此处的棋子是对方的
			reachableMap.put(UP_Position.getID(), UP_Position);
		}
	}

	@Override
	public String chessRecordes(Position begin, Position end, ChessBoard board) {
		return chessRecordesStraight(begin, end, board);
	}

	@Override
	public Position walkRecorde(ChessBoard board, String third, String forth) {
		return walkRecordeStraight(board, third, forth);
	}

}

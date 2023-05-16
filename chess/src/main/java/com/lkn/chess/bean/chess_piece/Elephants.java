package com.lkn.chess.bean.chess_piece;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.lkn.chess.ArrPool;
import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.Position;

/**
 * 象
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Elephants extends AbstractChessPiece {
	public static final int RED_NUM = 3;
	public static final int BLACK_NUM = 12;

	public Elephants(Role role) {
		this(null, role);
	}

	public Elephants(String id, Role role) {
		super(id, role);
		setValues();
		this.setFightDefaultVal(125);
		if (role == Role.RED) {    // 先手
			this.setName("相");
		} else {
			this.setName("象");
		}
		initNum(role, RED_NUM, BLACK_NUM);
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
				{  2,  0,  0,  0,  4,  0,  0,  0,  2},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? 3 : 10);
	}

	@Override
	public int valuation(ChessBoard board, int position) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		return 100 + arr[x][y];
	}

	@Override
	public int walkAsManual(ChessBoard board, int startPos, char cmd1, char cmd2) {
		int x = ChessTools.fetchX(startPos);
		int y = ChessTools.fetchY(startPos);
		int num = ChessTools.transToNum(cmd2);
		if (cmd1 == '进') {
			if (this.isRed()) {
				x = x + 2;
				y = 9 - num;
			} else {
				x = x - 2;
				y = num - 1;
			}
		} else if (cmd1 == '退') {
			if (this.isRed()) {
				x = x - 2;
				y = 9 - num;
			} else {
				x = x + 2;
				y = num - 1;
			}
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


	/**
	 * 检查4个点，并查看是否堵象眼了
	 */
	private void findReachablePositions(int currX, int currY, Map<Integer, AbstractChessPiece> allPiece) {
		int tmpX = currX - 2;
		int tmpY = currY - 2;
		if (isValid(tmpX, tmpY)) {
			if (!hasPiece(allPiece, currX - 1, tmpY - 1)) {
				AbstractChessPiece piece = allPiece.get(ChessTools.toPosition(tmpX, tmpY));
				if (piece == null || isEnemy(this, piece)) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
			}
		}

		tmpX = currX + 2;
		tmpY = currY + 2;
		if (isValid(tmpX, tmpY)) {
			if (!hasPiece(allPiece, currX + 1, tmpY + 1)) {
				AbstractChessPiece piece = allPiece.get(ChessTools.toPosition(tmpX, tmpY));
				if (piece == null || isEnemy(this, piece)) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
			}
		}

		tmpX = currX - 2;
		tmpY = currY + 2;
		if (isValid(tmpX, tmpY)) {
			if (!hasPiece(allPiece, currX - 1, tmpY + 1)) {
				AbstractChessPiece piece = allPiece.get(ChessTools.toPosition(tmpX, tmpY));
				if (piece == null || isEnemy(this, piece)) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
			}
		}


		tmpX = currX + 2;
		tmpY = currY - 2;
		if (isValid(tmpX, tmpY)) {
			if (!hasPiece(allPiece, currX + 1, tmpY - 1)) {
				AbstractChessPiece piece = allPiece.get(ChessTools.toPosition(tmpX, tmpY));
				if (piece == null || isEnemy(this, piece)) {
					recordReachablePosition(ChessTools.toPosition(tmpX, tmpY));
				}
			}
		}
	}


	/**
	 * 象可走的点为7个
	 * 只有两个点的x、y坐标分别相减的绝对值等于2时，才为可达点
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> reachableMap = new HashMap<String, Position>();
		Map<String, Position> allMap = board.getPositionMap();
		Map<String, Position> cloudWalkMap = new HashMap<String, Position>();
		if(this.getPLAYER_ROLE().equals(Role.RED)){	// 先手情况
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(2, 0)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(6, 0)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(0, 2)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 2)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(8, 2)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(2, 4)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(6, 4)), cloudWalkMap);
		}else if(this.getPLAYER_ROLE().equals(Role.BLACK)){	// 后手情况
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(2, 9)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(6, 9)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(0, 7)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 7)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(8, 7)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(2, 5)), cloudWalkMap);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(6, 5)), cloudWalkMap);
		}
		Integer currX = this.getCurrPosition().getX();
		Integer currY = this.getCurrPosition().getY();
		Collection<Position> collection = cloudWalkMap.values();
		for (Position position : collection) {
			Integer x = position.getX();
			Integer y = position.getY();
			if(Math.abs(currX - x) == 2 && Math.abs(currY - y) == 2){
				int eyeX = currX > x ? (x + 1) : (currX + 1);
				int eyeY = currY > y ? (y + 1) : (currY + 1);
				Position eyePosition = allMap.get(ChessTools.getPositionID(eyeX, eyeY));
				if(!eyePosition.isExistPiece()){	// 如果象眼不存在棋子的话，那么此位置可达
					ChessTools.putPositionToMap(position, reachableMap);
				}
				
			}
		}
		return reachableMap;
	}

	@Override
	public String chessRecordes(Position begin, Position end, ChessBoard board) {
		return chessRecordesCurved(begin, end, board);
	}

	@Override
	public Position walkRecorde(ChessBoard board, String third, String forth) {
		return walkRecordeCurved(board, third, forth);
	}

}

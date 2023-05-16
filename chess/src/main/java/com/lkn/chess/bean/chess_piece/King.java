package com.lkn.chess.bean.chess_piece;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lkn.chess.ArrPool;
import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.Position;

/**
 * 将
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class King extends AbstractChessPiece {
	public static final int RED_NUM = 5;
	public static final int BLACK_NUM = 14;

	public King(Role role) {
		this(null, role);
	}

	public King(String id, Role role) {
		super(id, role);
		setValues();
		this.setFightDefaultVal(1000000);
		if(role == Role.RED){	// 先手
			this.setName("帅");
		}else {
			this.setName("将");
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
				{  0,  0,  0,-10,-10,-10,  0,  0,  0},
				{  0,  0,  0, -8, -8, -8,  0,  0,  0},
				{  0,  0,  0, -2,  0, -2,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? 5 : 12);
	}

	@Override
	public int valuation(ChessBoard board, int position) {
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
		tryReach(currX - 1, currY, allPiece);
		tryReach(currX + 1, currY, allPiece);
		tryReach(currX, currY - 1, allPiece);
		tryReach(currX, currY + 1, allPiece);

		tryReachToEnemyKing(currX, currY, allPiece);
	}

	private void tryReachToEnemyKing(int currX, int currY, Map<Integer, AbstractChessPiece> allPiece) {
		if (this.isRed()) {
			for (int x = currX + 1; x < 10; x++) {
				AbstractChessPiece piece = allPiece.get(ChessTools.toPosition(x, currY));
				if (piece != null) {
					if (piece.getName().equals("将")) {
						recordReachablePosition(ChessTools.toPosition(x, currY));
					}
					return;
				}
			}
		} else {
			for (int x = currX - 1; x >= 0; x--) {
				AbstractChessPiece piece = allPiece.get(ChessTools.toPosition(x, currY));
				if (piece != null) {
					if (piece.getName().equals("帅")) {
						recordReachablePosition(ChessTools.toPosition(x, currY));
					}
					return;
				}
			}
		}
	}

	private void tryReach(int x, int y, Map<Integer, AbstractChessPiece> allPiece) {
		if (isInArea(x, y)) {
			AbstractChessPiece piece = allPiece.get(ChessTools.toPosition(x, y));
			if (piece == null || isEnemy(this, piece)) {
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


	/**
	 * 将的行走
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> reachableMap = new HashMap<String, Position>();	// 最终结果，需要返回的对象
		Map<String, Position> allMap = board.getPositionMap();
		Map<String, Position> couldWalkMapAll = new HashMap<String, Position>();	// 能够走的位置的集合
		if(this.getPLAYER_ROLE().equals(Role.RED)){	// 先手
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 0)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 0)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 0)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 1)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 1)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 1)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 2)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 2)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 2)), couldWalkMapAll);
		}else if(this.getPLAYER_ROLE().equals(Role.BLACK)){	// 后手
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 7)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 7)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 7)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 8)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 8)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 8)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(3, 9)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(4, 9)), couldWalkMapAll);
			ChessTools.putPositionToMap(allMap.get(ChessTools.getPositionID(5, 9)), couldWalkMapAll);
		}
		
		Integer currX = this.getCurrPosition().getX();
		Integer currY = this.getCurrPosition().getY();
		Collection<Position> collection = couldWalkMapAll.values();
		for (Position position : collection) {
			Integer x = position.getX();
			Integer y = position.getY();
			if(currX == x){	// 如果x轴相同
				if((currY == y + 1) || (currY == y - 1)){	// 且y轴只差一步
					if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), position)){	// 如果当前的位置是可达的
						ChessTools.putPositionToMap(position, reachableMap);	// 将当期的位置放入reachableMap中
					}
				}
			}else if(currY == y){	// 如果y轴相同
				if((currX == x + 1) || (currX == x - 1)){	// 且x轴只差一步
					if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), position)){	// 如果当前的位置是可达的
						ChessTools.putPositionToMap(position, reachableMap);	// 将当期的位置放入reachableMap中
					}
				}
			}
		}
		operateTwoKingMeeting(reachableMap, board);	// 处理两个帅见面的情况
		return reachableMap;
	}

	/**
	 * 处理两个帅直接见面的情况
	 * @author:likn1	Feb 13, 2016  10:33:06 AM
	 * @param reachableMap
	 * @param board 
	 */
	private void operateTwoKingMeeting(Map<String, Position> reachableMap, ChessBoard board) {
		Set<String> notReachableSet = new HashSet<String>();
		AbstractChessPiece kingPiece = null;
		if (this.getName().equals("将")) {
			kingPiece = PubTools.getSetIndexEle(ChessTools.getPieceByName(board, "帅", Role.RED), 0);
		} else {
			kingPiece = PubTools.getSetIndexEle(ChessTools.getPieceByName(board, "将", Role.BLACK), 0);
		}
		for (Position position : reachableMap.values()) {
			System.out.println("position == null ? " + (position == null));
			System.out.println("kingPiece == null ? " + (kingPiece == null));
			if (position.getX().equals(kingPiece.getCurrPosition().getX())) {	// 如果在同一列中
				Map<String, Position> map = ChessTools.getAllChess_Y(board.getPositionMap(), position);
				boolean hasOtherPiece = false;
				for (Position p : map.values()) {
					if(p.isExistPiece() && p.getPiece().isAlive()){
						AbstractChessPiece piece = p.getPiece();
						if(piece.getName().equals("帅")){
						} else if(piece.getName().equals("将")){
						} else {
							hasOtherPiece = true;
							break;
						}
					}
				}
				if(!hasOtherPiece){	// 如果两个“将”中间没有其他棋子
					notReachableSet.add(position.getID());
				}
			}
		}
		for (String str : notReachableSet) {	// 将可能会导致两个“将”见面的情况删掉
			reachableMap.remove(str);
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

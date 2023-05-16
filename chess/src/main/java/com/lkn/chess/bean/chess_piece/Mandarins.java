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
 * 士
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Mandarins extends AbstractChessPiece {
	public static final int RED_NUM = 4;
	public static final int BLACK_NUM = 13;

	public Mandarins(Role role) {
		this(null, role);
	}

	public Mandarins(String id, Role role) {
		super(id, role);
		setValues();
		this.setFightDefaultVal(120);
		if(role == Role.RED){	// 先手
			this.setName("仕");
		}else {
			this.setName("士");
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
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  4,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? 4 : 11);
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
			if (currX == 1 && currY == 4) {
				tryReach(currX - 1, currY, allPiece);
				tryReach(currX + 1, currY, allPiece);
				tryReach(currX, currY - 1, allPiece);
				tryReach(currX, currY + 1, allPiece);
			} else {
				tryReach(1, 4, allPiece);
			}
		} else {
			if (currX == 8 && currY == 4) {
				tryReach(currX - 1, currY, allPiece);
				tryReach(currX + 1, currY, allPiece);
				tryReach(currX, currY - 1, allPiece);
				tryReach(currX, currY + 1, allPiece);
			} else {
				tryReach(8, 4, allPiece);
			}
		}
	}

	private void tryReach(int x, int y, Map<Integer, AbstractChessPiece> allPiece) {
		AbstractChessPiece piece = allPiece.get(ChessTools.toPosition(x, y));
		if (piece == null || isEnemy(this, piece)) {
			recordReachablePosition(ChessTools.toPosition(x, y));
		}
	}

	/**
	 * 士只有5个点可以走
	 * 1、如果当前位置是中心位置，那么有4个位置可走
	 * 2、如果当前位置不是中心位置，那么只有一个中心位置可走
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> reachableMap = new HashMap<String, Position>();
		Map<String, Position> allMap = board.getPositionMap();
		Position p1 = null;
		Position p2 = null;
		Position p3 = null;
		Position p4 = null;
		Position center = null;
		if(this.getPLAYER_ROLE().equals(Role.RED)){	// 先手
			p1 = allMap.get(ChessTools.getPositionID(3, 0));
			p2 = allMap.get(ChessTools.getPositionID(5, 0));
			p3 = allMap.get(ChessTools.getPositionID(3, 2));
			p4 = allMap.get(ChessTools.getPositionID(5, 2));
			center = allMap.get(ChessTools.getPositionID(4, 1));
		} else if(this.getPLAYER_ROLE().equals(Role.BLACK)){	// 后手
			p1 = allMap.get(ChessTools.getPositionID(3, 9));
			p2 = allMap.get(ChessTools.getPositionID(5, 9));
			p3 = allMap.get(ChessTools.getPositionID(3, 7));
			p4 = allMap.get(ChessTools.getPositionID(5, 7));
			center = allMap.get(ChessTools.getPositionID(4, 8));
		}
		
		
		if(this.getCurrPosition().getID().equals(center.getID())){	// 如果当前棋子在中心
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), p1)){	// 如果p1可达
				reachableMap.put(p1.getID(), p1);
			}
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), p2)){	// 如果p2可达
				reachableMap.put(p2.getID(), p2);
			}
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), p3)){	// 如果p3可达
				reachableMap.put(p3.getID(), p3);
			}
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), p4)){	// 如果p4可达
				reachableMap.put(p4.getID(), p4);
			}
		} else {	// 如果不在中心
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), center)){	// 如果中心可达
				reachableMap.put(center.getID(), center);
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

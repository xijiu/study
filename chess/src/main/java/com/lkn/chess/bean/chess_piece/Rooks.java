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
 * 车
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Rooks extends AbstractChessPiece {
	public static final int RED_NUM = 1;
	public static final int BLACK_NUM = 10;

	public Rooks(Role role) {
		this(null, role);
	}

	public Rooks(String id, Role role) {
		super(id, role);
		setValues();
		this.setFightDefaultVal(600);
		this.setName("车");
		initNum(role, RED_NUM, BLACK_NUM);
	}
	
	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{ 14, 14, 12, 18, 16, 18, 12, 14, 14},
				{ 16, 20, 18, 24, 26, 24, 18, 20, 16},
				{ 12, 12, 12, 18, 18, 18, 12, 12, 12},
				{ 12, 18, 16, 22, 22, 22, 16, 18, 12},
				{ 12, 14, 12, 18, 18, 18, 12, 14, 12},
				{ 12, 16, 14, 20, 20, 20, 14, 16, 12},
				{  6, 10,  8, 14, 14, 14,  8, 10,  6},
				{  4,  8,  6, 14, 12, 14,  6,  8,  4},
				{  8,  4,  8, 16,  8, 16,  8,  4,  8},
				{ -2, 10,  6, 14, 12, 14,  6, 10, -2}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte type() {
		return (byte) (isRed() ? 1 : 8);
	}

	@Override
	public int valuation(ChessBoard board, int position) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		return 550 + arr[x][y];
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

	/**
	 * 车在棋盘上可达的位置集合
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> allMap = board.getPositionMap();	// 获取棋盘的所有的位置集合
		Position currPosition = this.getCurrPosition();	// 车当前的位置
		return rooksOperate(currPosition, allMap);
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
		AbstractChessPiece piece = null;
		// 向上找
		for (int i = currX + 1; i < 10; i++) {
			int position = ChessTools.toPosition(i, currY);
			if ((piece = allPiece.get(position)) == null) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, piece)) {
					recordReachablePosition(position);
				}
				break;
			}
		}
		// 向下找
		for (int i = currX - 1; i >= 0; i--) {
			int position = ChessTools.toPosition(i, currY);
			if ((piece = allPiece.get(position)) == null) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, piece)) {
					recordReachablePosition(position);
				}
				break;
			}
		}
		// 向右找
		for (int i = currY + 1; i < 9; i++) {
			int position = ChessTools.toPosition(currX, i);
			if ((piece = allPiece.get(position)) == null) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, piece)) {
					recordReachablePosition(position);
				}
				break;
			}
		}
		// 向左找
		for (int i = currY - 1; i >= 0; i--) {
			int position = ChessTools.toPosition(currX, i);
			if ((piece = allPiece.get(position)) == null) {
				recordReachablePosition(position);
			} else {
				if (isEnemy(this, piece)) {
					recordReachablePosition(position);
				}
				break;
			}
		}
	}

	/**
	 * 车的行为
	 * @author:likn1	Jan 11, 2016  3:22:26 PM
	 * @param position
	 * @param allMap
	 * @return
	 */
	public Map<String, Position> rooksOperate(Position position, Map<String, Position> allMap){
		Map<String, Position> reachableMap = new HashMap<String, Position>();
		Map<String, Position> chess_X_map = ChessTools.getAllChess_X(allMap, position);
		Map<String, Position> chess_Y_map = ChessTools.getAllChess_Y(allMap, position);
		reachableMap.putAll(reachableUp(position, chess_Y_map));		// 向上可达的位置
		reachableMap.putAll(reachableDOWN(position, chess_Y_map));	// 向下可达的位置
		reachableMap.putAll(reachableLEFT(position, chess_X_map));	// 向左可达的位置
		reachableMap.putAll(reachableRIGHT(position, chess_X_map));	// 向右可达的位置
		return reachableMap;
	}

	/**
	 * 向右可到达的位置
	 * @author:likn1	Jan 6, 2016  11:59:21 AM
	 * @param currPosition
	 * @param chess_X_map
	 */
	private Map<String, Position> reachableRIGHT(Position currPosition, Map<String, Position> chess_X_map) {
		Map<String, Position> reachableRIGHT = new HashMap<String, Position>();
		Integer x = currPosition.getX();
		Integer y = currPosition.getY();
		for (int i = x + 1; i <= 8; i++) {
			Position position = chess_X_map.get(ChessTools.getPositionID(i, y));
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), position)){
				reachableRIGHT.put(position.getID(), position);
			}
			if(position.isExistPiece()){
				break;
			}
		}
		return reachableRIGHT;
	}

	/**
	 * 向左可到达的位置
	 * @author:likn1	Jan 6, 2016  11:28:21 AM
	 * @param currPosition
	 * @param chess_X_map
	 */
	private Map<String, Position> reachableLEFT(Position currPosition, Map<String, Position> chess_X_map) {
		Map<String, Position> reachableLEFT = new HashMap<String, Position>();
		Integer x = currPosition.getX();
		Integer y = currPosition.getY();
		for (int i = x - 1; i >= 0; i--) {
			Position position = chess_X_map.get(ChessTools.getPositionID(i, y));
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), position)){
				reachableLEFT.put(position.getID(), position);
			}
			if(position.isExistPiece()){
				break;
			}
		}
		return reachableLEFT;
	}

	/**
	 * 向下可达到的位置集合
	 * @author:likn1	Jan 6, 2016  11:08:43 AM
	 * @param currPosition
	 * @param chessY_Map
	 */
	private Map<String, Position> reachableDOWN(Position currPosition, Map<String, Position> chessY_Map) {
		Map<String, Position> reachableDOWN = new HashMap<String, Position>();
		Integer x = currPosition.getX();
		Integer y = currPosition.getY();
		for (int i = y - 1; i >= 0; i--) {
			Position position = chessY_Map.get(ChessTools.getPositionID(x, i));
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), position)){	// 如果位置可到
				reachableDOWN.put(position.getID(), position);
			}
			if(position.isExistPiece()){	// 如果当前位置有棋子的话，无论是否是本方的棋子，那么该结束本循环
				break;
			}
		}
		return reachableDOWN;
	}

	/**
	 * 向上可到的位置
	 * x坐标最大至8
	 * y坐标最大至9
	 * 
	 * @author:likn1	Jan 6, 2016  10:06:19 AM
	 * @param currPosition 
	 * @param chessY_Map
	 */
	private Map<String, Position> reachableUp(Position currPosition, Map<String, Position> chessY_Map) {
		Map<String, Position> reachableUP = new HashMap<String, Position>();
		Integer x = currPosition.getX();
		Integer y = currPosition.getY();
		for (int i = y + 1; i <= 9; i++) {
			Position position = chessY_Map.get(ChessTools.getPositionID(x, i));
			if(ChessTools.isPositionReachable(this.getPLAYER_ROLE(), position)){	// 如果位置可到
				reachableUP.put(position.getID(), position);
			}
			if(position.isExistPiece()){	// 如果当前位置有棋子的话，无论是否是本方的棋子，那么该结束本循环
				break;
			}
		}
		return reachableUP;
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

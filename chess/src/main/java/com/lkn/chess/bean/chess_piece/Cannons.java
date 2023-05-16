package com.lkn.chess.bean.chess_piece;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.lkn.chess.ArrPool;
import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.Position;

/**
 * 炮
 * @author:likn1	Jan 5, 2016  3:53:53 PM
 */
public class Cannons extends AbstractChessPiece {
	public static final int RED_NUM = 6;
	public static final int BLACK_NUM = 9;
	public static final int BASE_VAL = 280;


	public Cannons(Role role) {
		this(null, role);
	}
	
	public Cannons(String id, Role role) {
		super(id, role);
		this.setFightDefaultVal(BASE_VAL);
		setValues();
		this.setName("炮");
		initNum(role, RED_NUM, BLACK_NUM);
	}
	
	/**
	 * 设置子力的位置与加权关系
	 * @author:likn1	Jan 22, 2016  5:53:42 PM
	 */
	private void setValues() {
		int[][] VAL_RED_TEMP = {	// 先手方的位置与权值加成
				{  6,  4,  0,  0,  0,  0,  0,  4,  6},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0, 10,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  6,  6, 10,  6,  6,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0},
				{  0,  0,  0,  0,  0,  0,  0,  0,  0}
			};
		VAL_BLACK = VAL_RED_TEMP;
		VAL_RED = PubTools.arrChessReverse(VAL_BLACK);	// 后手方的位置与权值加成
	}

	@Override
	public byte[] getReachablePositions(int currPosition, ChessBoard board) {
		reachableNum = 0;
		int currX = ChessTools.fetchX(currPosition);
		int currY = ChessTools.fetchY(currPosition);

		addNotEatCase(currX, currY, board.getAllPiece());
		addEatCase(currX, currY, board.getAllPiece());
		reachablePositions[0] = (byte) reachableNum;
		byte[] result = ArrPool.borrow();
		System.arraycopy(reachablePositions, 0, result, 0, reachablePositions.length);
		return result;
	}

	/**
	 * 炮跟将中间如果没有棋子，那么威力将大增
	 */
	@Override
	public int valuation(ChessBoard board, int position) {
		int[][] arr = this.isRed() ? VAL_RED : VAL_BLACK;
		int x = ChessTools.fetchX(position);
		int y = ChessTools.fetchY(position);
		int hollowVal = calcHollow(x, y, board.getAllPiece());
		return BASE_VAL + hollowVal + arr[x][y];
	}

	/**
	 * 计算是否存在空心炮，以及其威力
	 */
	private int calcHollow(int currX, int currY, Map<Integer, AbstractChessPiece> allPiece) {
		int targetPos = isRed() ?
				findPositionByName(allPiece, "将") : findPositionByName(allPiece, "帅") ;
		int targetX = ChessTools.fetchX(targetPos);
		int targetY = ChessTools.fetchY(targetPos);
		if (isRed()) {
			if (targetY == currY) {
				int beginX = Math.min(targetX, currX);
				int endX = Math.max(targetX, currX);
				for (int x = beginX + 1; x < endX; x++) {
					int position = ChessTools.toPosition(x, currY);
					AbstractChessPiece piece = allPiece.get(position);
					if (piece != null) {
						return 0;
					}
				}
				int abs = Math.abs(targetX - currX);
				if (abs >= 2) {
					return 150 + 10 * abs;
				}
				return 0;
			}
			if (targetX == currX) {
				int beginY = Math.min(targetY, currY);
				int endY = Math.max(targetY, currY);
				for (int y = beginY + 1; y < endY; y++) {
					int position = ChessTools.toPosition(currX, y);
					AbstractChessPiece piece = allPiece.get(position);
					if (piece != null) {
						return 0;
					}
				}
				int abs = Math.abs(targetY - currY);
				if (abs >= 2) {
					return 150 + 10 * abs;
				}
				return 0;
			}
		}
		return 0;
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
	public byte type() {
		return (byte) (isRed() ? 6 : 13);
	}

//	@Override
//	public boolean canEat(ChessBoard board, int currPos, int targetPos) {
//		Map<Integer, AbstractChessPiece> allPiece = board.getAllPiece();
//		int currX = ChessTools.fetchX(currPos);
//		int currY = ChessTools.fetchY(currPos);
//		int targetX = ChessTools.fetchX(targetPos);
//		int targetY = ChessTools.fetchY(targetPos);
//		if (currX == targetX) {
//			int minY = Math.min(currY, targetY);
//			int maxY = Math.max(currY, targetY);
//			int num = 0;
//			for (int y = minY + 1; y < maxY; y++) {
//				if (allPiece.containsKey(ChessTools.toPosition(currX, y))) {
//					num++;
//				}
//				if (num > 1) {
//					return false;
//				}
//			}
//			if (num == 1) {
//				return true;
//			}
//		}
//
//		if (currY == targetY) {
//			int minX = Math.min(currX, targetX);
//			int maxX = Math.max(currX, targetX);
//			int num = 0;
//			for (int x = minX + 1; x < maxX; x++) {
//				if (allPiece.containsKey(ChessTools.toPosition(x, currY))) {
//					num++;
//				}
//				if (num > 1) {
//					return false;
//				}
//			}
//			return num == 1;
//		}
//
//		return false;
//	}


	/**
	 * 吃子儿的情况
	 */
	private void addEatCase(int currX, int currY, Map<Integer, AbstractChessPiece> allPiece) {
		boolean hasRack = false;
		AbstractChessPiece piece = null;
		// 向上找
		for (int i = currX + 1; i < 10; i++) {
			int position = ChessTools.toPosition(i, currY);
			if ((piece = allPiece.get(position)) != null) {
				if (hasRack) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					}
					break;
				} else {
					hasRack = true;
				}
			}
		}

		// 向下找
		hasRack = false;
		piece = null;
		for (int i = currX - 1; i >= 0; i--) {
			int position = ChessTools.toPosition(i, currY);
			if ((piece = allPiece.get(position)) != null) {
				if (hasRack) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					}
					break;
				} else {
					hasRack = true;
				}
			}
		}

		// 向右找
		hasRack = false;
		piece = null;
		for (int i = currY + 1; i < 9; i++) {
			int position = ChessTools.toPosition(currX, i);
			if ((piece = allPiece.get(position)) != null) {
				if (hasRack) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					}
					break;
				} else {
					hasRack = true;
				}
			}
		}

		// 向左找
		hasRack = false;
		piece = null;
		for (int i = currY - 1; i >= 0; i--) {
			int position = ChessTools.toPosition(currX, i);
			if ((piece = allPiece.get(position)) != null) {
				if (hasRack) {
					if (isEnemy(this, piece)) {
						recordReachablePosition(position);
					}
					break;
				} else {
					hasRack = true;
				}
			}
		}
	}

	/**
	 * 不吃子儿的情况
	 */
	private void addNotEatCase(int currX, int currY, Map<Integer, AbstractChessPiece> allPiece) {
		// 向上找
		for (int i = currX + 1; i < 10; i++) {
			int position = ChessTools.toPosition(i, currY);
			if (allPiece.get(position) == null) {
				recordReachablePosition(position);
			} else {
				break;
			}
		}
		// 向下找
		for (int i = currX - 1; i >= 0; i--) {
			int position = ChessTools.toPosition(i, currY);
			if (allPiece.get(position) == null) {
				recordReachablePosition(position);
			} else {
				break;
			}
		}
		// 向右找
		for (int i = currY + 1; i < 9; i++) {
			int position = ChessTools.toPosition(currX, i);
			if (allPiece.get(position) == null) {
				recordReachablePosition(position);
			} else {
				break;
			}
		}
		// 向左找
		for (int i = currY - 1; i >= 0; i--) {
			int position = ChessTools.toPosition(currX, i);
			if (allPiece.get(position) == null) {
				recordReachablePosition(position);
			} else {
				break;
			}
		}
	}


	/**
	 * 炮有两种大的行为模式
	 * 1、不吃子
	 * 2、吃子
	 */
	@Override
	public Map<String, Position> getReachablePositions(ChessBoard board) {
		Map<String, Position> reachableMap = new HashMap<String, Position>();	// 可达的位置
		Map<String, Position> allMap = board.getPositionMap();
		Map<String, Position> mapX = ChessTools.getAllChess_X(allMap, this.getCurrPosition());
		Map<String, Position> mapY = ChessTools.getAllChess_Y(allMap, this.getCurrPosition());
		reachableMap.putAll(notEatPiece(mapX, mapY, allMap));	// 不吃子的情况下
		reachableMap.putAll(eatPiece(mapX, mapY, allMap));	// 吃子的情况下
		return reachableMap;
	}

	/**
	 * 在炮不吃子的情况下，能够到达的位置
	 * 此种情况下，炮的行为类似车；
	 * 但是炮不会像车那样吃子，故需要把吃子的行为过滤掉
	 * @author:likn1	Jan 7, 2016  4:12:01 PM
	 * @param mapX
	 * @param mapY
	 * @param allMap
	 * @return
	 */
	private Map<String, Position> notEatPiece(Map<String, Position> mapX, Map<String, Position> mapY, Map<String, Position> allMap) {
		Rooks rooks = new Rooks(null, this.getPLAYER_ROLE());
		Map<String, Position> rooksMap = rooks.rooksOperate(this.getCurrPosition(), allMap);	// 模拟车的行为
		Map<String, Position> reachableMap = new HashMap<String, Position>();	//  没有棋子的map
		Collection<Position> collection = rooksMap.values();
		for (Position position : collection) {
			if(!position.isExistPiece()){
				reachableMap.put(position.getID(), position);
			}
		}
		return reachableMap;
	}
	
	/******************************************************************************************************************************************************************************
	 * 吃子的情况分为4类：上、下、左、右
	 * @author:likn1	Jan 7, 2016  4:14:16 PM
	 * @param mapX
	 * @param mapY
	 * @param allMap
	 * @return
	 */
	private Map<String, Position> eatPiece(Map<String, Position> mapX, Map<String, Position> mapY, Map<String, Position> allMap) {
		Map<String, Position> reachableEatMap = new HashMap<String, Position>();
		ChessTools.putPositionToMap(eatUP(mapY, allMap), reachableEatMap);	// 向上吃子
		ChessTools.putPositionToMap(eatDOWN(mapY, allMap), reachableEatMap);	// 向下吃子
		ChessTools.putPositionToMap(eatLEFT(mapX, allMap), reachableEatMap);	// 向左吃子
		ChessTools.putPositionToMap(eatRIGHT(mapX, allMap), reachableEatMap);	// 向右吃子
		return reachableEatMap;
	}

	/**
	 * 向右吃子
	 * @author:likn1	Jan 7, 2016  5:19:29 PM
	 * @param mapX
	 * @param allMap
	 * @return
	 */
	private Position eatRIGHT(Map<String, Position> mapX, Map<String, Position> allMap) {
		Position returnP = null;
		Set<Integer> set = new TreeSet<Integer>();
		Integer currX = this.getCurrPosition().getX();
		Collection<Position> collection = mapX.values();
		for (Position position : collection) {
			if(position.getX() > currX && position.isExistPiece()){
				set.add(position.getX());
			}
		}
		if(set.size() >= 2){
			Integer x = PubTools.getSetIndexEle(set, 1);	// 获取第二个元素信息
			Position position = allMap.get(ChessTools.getPositionID(x, this.getCurrPosition().getY()));
			if(position.isExistPiece() && !position.getPiece().getPLAYER_ROLE().equals(this.getPLAYER_ROLE())){	// 如果该棋子是对方的棋子
				returnP = position;
			}
		}
		return returnP;
	}

	/**
	 * 向左吃子
	 * @author:likn1	Jan 7, 2016  5:10:06 PM
	 * @param mapX
	 * @param allMap
	 * @return
	 */
	private Position eatLEFT(Map<String, Position> mapX, Map<String, Position> allMap) {
		Position returnP = null;
		Set<Integer> set = new TreeSet<Integer>();
		Integer currX = this.getCurrPosition().getX();
		Collection<Position> collection = mapX.values();
		for (Position position : collection) {
			if(position.getX() < currX && position.isExistPiece()){
				set.add(position.getX());
			}
		}
		if(set.size() >= 2){
			Integer x = PubTools.getSetIndexEle(set, set.size() - 2);	// 获取倒数第二个元素信息
			Position position = allMap.get(ChessTools.getPositionID(x, this.getCurrPosition().getY()));
			if(position.isExistPiece() && !position.getPiece().getPLAYER_ROLE().equals(this.getPLAYER_ROLE())){	// 如果该棋子是对方的棋子
				returnP = position;
			}
		}
		return returnP;
	}

	/**
	 * 向下吃子
	 * @author:likn1	Jan 7, 2016  4:50:14 PM
	 * @param mapY
	 * @param allMap
	 * @return
	 */
	private Position eatDOWN(Map<String, Position> mapY, Map<String, Position> allMap) {
		Position returnP = null;
		Set<Integer> set = new TreeSet<Integer>();
		Integer currY = this.getCurrPosition().getY();
		Collection<Position> collection = mapY.values();
		for (Position position : collection) {
			if(position.getY() < currY && position.isExistPiece()){
				set.add(position.getY());
			}
		}
		if(set.size() >= 2){
			Integer y = PubTools.getSetIndexEle(set, set.size() - 2);	// 获取倒数第二个元素信息
			Position position = allMap.get(ChessTools.getPositionID(this.getCurrPosition().getX(), y));
			if(position.isExistPiece() && !position.getPiece().getPLAYER_ROLE().equals(this.getPLAYER_ROLE())){	// 如果该棋子是对方的棋子
				returnP = position;
			}
		}
		return returnP;
	}

	/**
	 * 向上吃子的情况
	 * @author:likn1	Jan 7, 2016  4:18:20 PM
	 * @param mapY
	 * @param allMap
	 */
	private Position eatUP(Map<String, Position> mapY, Map<String, Position> allMap) {
		Position returnP = null;
		Set<Integer> set = new TreeSet<Integer>();
		Integer currY = this.getCurrPosition().getY();
		Collection<Position> collection = mapY.values();
		for (Position position : collection) {
			if(position.getY() > currY && position.isExistPiece()){
				set.add(position.getY());
			}
		}
		if(set.size() >= 2){
			Integer y = PubTools.getSetIndexEle(set, 1);	// 获取第二个元素信息
			Position position = allMap.get(ChessTools.getPositionID(this.getCurrPosition().getX(), y));
			if(position.isExistPiece() && !position.getPiece().getPLAYER_ROLE().equals(this.getPLAYER_ROLE())){	// 如果该棋子是对方的棋子
				returnP = position;
			}
		}
		return returnP;
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

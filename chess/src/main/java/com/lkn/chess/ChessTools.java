package com.lkn.chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.Position;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;

/**
 * 象棋运子的工具类
 * @author:likn1	Jan 6, 2016  9:49:08 AM
 */
public class ChessTools {

	private final static String[] RED_RECORDES_ARR = {"一","二","三","四","五","六","七","八","九"};
	private final static String[] BLACK_RECORDES_ARR = {"１","２","３","４","５","６","７","８","９"};

	public static String[] getRedRecordesArr() {
		return RED_RECORDES_ARR;
	}

	public static String[] getBlackRecordesArr() {
		return BLACK_RECORDES_ARR;
	}

	public static int transToNum(char str) {
		switch (str) {
			case '一':
			case '1':
			case '１':
				return 1;
			case '二':
			case '2':
			case '２':
				return 2;
			case '三':
			case '3':
			case '３':
				return 3;
			case '四':
			case '4':
			case '４':
				return 4;
			case '五':
			case '5':
			case '５':
				return 5;
			case '六':
			case '6':
			case '６':
				return 6;
			case '七':
			case '7':
			case '７':
				return 7;
			case '八':
			case '8':
			case '８':
				return 8;
			case '九':
			case '9':
			case '９':
				return 9;
		}

		throw new RuntimeException();
	}

	public static void main(String[] args) {
		System.out.println(transLineToY('8', Role.RED));
		System.out.println(transLineToY('8', Role.BLACK));
		System.out.println(transLineToY('八', Role.BLACK));
	}

	public static int transLineToY(char line, Role role) {
		if (role == Role.RED) {
			switch (line) {
				case '一':
				case '1':
				case '１':
					return 8;
				case '二':
				case '2':
				case '２':
					return 7;
				case '三':
				case '3':
				case '３':
					return 6;
				case '四':
				case '4':
				case '４':
					return 5;
				case '五':
				case '5':
				case '５':
					return 4;
				case '六':
				case '6':
				case '６':
					return 3;
				case '七':
				case '7':
				case '７':
					return 2;
				case '八':
				case '8':
				case '８':
					return 1;
				case '九':
				case '9':
				case '９':
					return 0;
			}
		} else {
			switch (line) {
				case '一':
				case '1':
				case '１':
					return 0;
				case '二':
				case '2':
				case '２':
					return 1;
				case '三':
				case '3':
				case '３':
					return 2;
				case '四':
				case '4':
				case '４':
					return 3;
				case '五':
				case '5':
				case '５':
					return 4;
				case '六':
				case '6':
				case '６':
					return 5;
				case '七':
				case '7':
				case '７':
					return 6;
				case '八':
				case '8':
				case '８':
					return 7;
				case '九':
				case '9':
				case '９':
					return 8;
			}
		}

		throw new RuntimeException("invalid line " + line);
	}


	public static int fetchX(int position) {
		if (position < 10) {
			return 0;
		}
		return position / 10;
	}

	public static int fetchY(int position) {
		return position % 10;
	}

	public static int toPosition(int x, int y) {
		return x * 10 + y;
	}

	/**
	 * 获取p所在位置上的所有的X轴上的位置集合
	 * @author:likn1	Jan 6, 2016  9:51:30 AM
	 * @param allMap
	 * @param p
	 * @return
	 */
	public static Map<String, Position> getAllChess_X(Map<String, Position> allMap, Position p){
		Map<String, Position> allX = new HashMap<>();
		Collection<Position> collection = allMap.values();
		for (Position position : collection) {
			if(position.getY().equals(p.getY())){
				allX.put(position.getID(), position);
			}
		}
		return allX;
	}

	/**
	 * 获取p所在位置上的所有的Y轴上的位置集合
	 * @author:likn1	Jan 6, 2016  9:51:30 AM
	 * @param allMap
	 * @param p
	 * @return
	 */
	public static Map<String, Position> getAllChess_Y(Map<String, Position> allMap, Position p){
		Map<String, Position> allY = new HashMap<String, Position>();
		Collection<Position> collection = allMap.values();
		for (Position position : collection) {
			if(position.getX().equals(p.getX())){
				allY.put(position.getID(), position);
			}
		}
		return allY;
	}

	/**
	 * 通过x坐标以及y坐标，拼接此位置的id
	 * @author:likn1	Jan 6, 2016  10:46:18 AM
	 * @param x
	 * @param y
	 * @return
	 */
	public static String getPositionID(Integer x, Integer y){
		StringBuffer id = new StringBuffer().append(x).append(y);
		return id.toString();
	}

	/**
	 * 是否能够达到位置position
	 * 能够到达位置position的情况有两种：
	 * 	1、该position没有棋子
	 * 	2、该position上的棋子为对方的棋子
	 *
	 * 如果是以上两种情况，那么返回true，否则返回false
	 * @author:likn1	Jan 6, 2016  2:45:40 PM
	 * @param role
	 * @param position
	 * @return
	 */
	public static boolean isPositionReachable(Role role, Position position){
		boolean reachable = false;
		if(position != null){
			if(!position.isExistPiece()){	// 该位置没有棋子
				reachable = true;
			} else {
				if(!position.getPiece().getPLAYER_ROLE().equals(role)){	// 该位置的棋子与本身的棋子属于不同的角色
					reachable = true;
				}
			}
		}
		return reachable;
	}

	/**
	 * 将p放入map中
	 * @author:likn1	Jan 6, 2016  5:01:13 PM
	 * @param p
	 * @param map
	 */
	public static void putPositionToMap(Position p, Map<String, Position> map){
		if(p != null && map != null){
			map.put(p.getID(), p);
		}
	}


	/**
	 * 将p放入map中
	 * 当且仅当p的位置为空，或者p位置上的为对方的子时，才将该位置加入map中
	 * @author:likn1	Jan 7, 2016  11:10:04 AM
	 * @param p
	 * @param map
	 */
	public static void putValidPositionToMap(Role role, Position p, Map<String, Position> map){
		if(isPositionReachable(role, p)){	// 如果当前节点能够到达
			map.put(p.getID(), p);
		}
	}

	/**
	 * 根据X坐标以及当前的先后手返回棋谱中应该显示的内容
	 * 例如：
	 * 	ROLE == PlayerRole.ON_THE_OFFENSIVE，X = 1，那么将会返回 “八”
	 * 	ROLE == PlayerRole.DEFENSIVE_POSITION，X = 1，那么将会返回 “２”
	 * @param ROLE
	 * @param X
	 * @return
	 */
	public static String getRecordeShowByX(Role ROLE, Integer X) {
		int currX = ROLE == Role.RED ? 8 - X : X;
		String show = ROLE == Role.RED ? RED_RECORDES_ARR[currX] : BLACK_RECORDES_ARR[currX];
		return show;
	}

	/**
	 * 根据X坐标以及当前的先后手返回棋谱中应该显示的内容
	 * 例如：
	 * 	ROLE == PlayerRole.ON_THE_OFFENSIVE，X = 1，那么将会返回 “八”
	 * 	ROLE == PlayerRole.DEFENSIVE_POSITION，X = 1，那么将会返回 “２”
	 * @param ROLE
	 * @param X
	 * @return
	 */
	public static int getXByRecordeShow(Role ROLE, String recorde) {
		int X = -1;
		if(ROLE == Role.RED){
			for (int i = 0; i < RED_RECORDES_ARR.length; i++) {
				if(RED_RECORDES_ARR[i].equals(recorde)){
					X = 8 - i;
					break;
				}
			}
		} else if(ROLE == Role.BLACK){
			for (int i = 0; i < BLACK_RECORDES_ARR.length; i++) {
				if(BLACK_RECORDES_ARR[i].equals(recorde)){
					X = i;
					break;
				}
			}
		}
		return X;
	}

	/**
	 * 根据棋谱判断当前走棋方的先后手
	 * @param law
	 * @return
	 */
	public static Role judgeLawRoler(String law){
		Role role = null;
		for (int i = 0; i < RED_RECORDES_ARR.length; i++) {
			if(law.contains(RED_RECORDES_ARR[i])){
				role = Role.RED;
				break;
			}
		}
		role = role == null ? Role.BLACK : role;
		return role;
	}

	/**
	 * 根据棋子的名称获取该棋子
	 * @param board
	 * @param name
	 * @param ROLE
	 * @return
	 */
	public static Set<AbstractChessPiece> getPieceByName(ChessBoard board, String name, Role ROLE){
		Set<AbstractChessPiece> resultSet =  new HashSet<>();
		Set<AbstractChessPiece> set = board.getPiecesByPlayRole(ROLE);
		for (AbstractChessPiece piece : set) {
			if(piece.getName().equals(name)){
				resultSet.add(piece);
			}
		}
		return resultSet;
	}

	/**
	 * 根据棋子的开始位置以及结束位置来判断该棋子是“进”还是“退”还是“平”
	 * @author:likn1	Feb 9, 2016  9:41:34 AM
	 * @param beginY
	 * @param endY
	 * @param role
	 */
	public static String judgePieceDirection(Integer beginY, Integer endY, Role role){
		String result = null;
		if(beginY == endY){
			result = "平";
		} else {
			if(role == Role.RED){	// 先手
				result = endY > beginY ? "进" : "退";
			} else {	// 后手
				result = endY < beginY ? "进" : "退";
			}
		}
		return result;
	}
}

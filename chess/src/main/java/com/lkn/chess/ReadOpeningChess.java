package com.lkn.chess;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.ChessWalkBean;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.Position;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;

/**
 * 读取开局棋谱的类
 * 如果在开局阶段能找到类似的开局，那么首先沿用棋谱中的着法
 * @author pc
 *
 */
public class ReadOpeningChess {
	/**
	 * 获取下一步的着法
	 * @param currChessLaw
	 * @return
	 */
	public static String getNextChessLaw(String currChessLaw){
		String chessLaw = null;
		Set<String> resultSet = new HashSet<String>();
		Set<String> set = null;
		if(Conf.getComputerRole() == Role.RED){
			set = Conf.getRedWinSet();
		} else {
			set = Conf.getBlackWinSet();
		}
		set.addAll(Conf.getPeaceSet());
		for (String law : set) {
			if(law.startsWith(currChessLaw)){
				chessLaw = law.substring(currChessLaw.length(), law.length());
				if(chessLaw.length() >= 4){
					chessLaw = chessLaw.substring(0, 4);
					resultSet.add(chessLaw);
				}
			}
		}
		chessLaw = PubTools.getRandomEleFromSet(resultSet);
		return chessLaw;
	}
	
	public static ChessWalkBean getNextChessLaw(String currChessLaw, ChessBoard board){
		ChessWalkBean walkBean = null;
		String nextLaw = getNextChessLaw(currChessLaw);
		if(nextLaw != null && !nextLaw.equals("")){
			walkBean = transfLawToWalkBean(nextLaw, board);
		}
		return walkBean;
	}
	
	/**
	 * 将棋谱中的着法转变为程序中的执行步骤
	 * @param nextLaw
	 * @param board
	 * @return
	 */
	public static ChessWalkBean transfLawToWalkBean(String nextLaw, ChessBoard board) {
		ChessWalkBean walkBean = null;
		Role ROLE = ChessTools.judgeLawRoler(nextLaw);
		if(nextLaw != null && nextLaw.length() == 4){
			char[] arr = nextLaw.toCharArray();
			Set<AbstractChessPiece> set = getWalkPiece(arr, board, ROLE);
			for (AbstractChessPiece walkPiece : set) {
				Position endPosition = walkPiece.walkRecorde(board, String.valueOf(arr[2]), String.valueOf(arr[3]));
				if(endPosition == null){
					continue;
				}
				walkBean = new ChessWalkBean(walkPiece.getCurrPosition(), endPosition);
			}
//			if(walkBean == null){
//				AbstractChessPiece piece = PubTools.getSetIndexEle(set, 0);
//				Map<String, Position> reachablePositions = piece.getReachablePositions(board);
//				System.out.println(piece.getName());
//				System.out.println(reachablePositions.size());
//			}
		}
		return walkBean;
	}
	
	/**
	 * 将棋谱中的着法转变为程序中的执行步骤
	 * @param nextLaw
	 * @param board
	 * @return
	 */
	public static ChessWalkBean transfLawToWalkBean(ChessBoard board) {
		ChessWalkBean walkBean = null;
		Map<String, Set<String>> map = null;
		if(Conf.getComputerRole() == Role.RED){	// 电脑先手
			map = Conf.getRedNextStepMap();
		} else {	// 电脑后手
			map = Conf.getBlackNextStepMap();
		}
		System.out.println(board.currPiecesStr());
		Set<String> set = map.get(board.currPiecesStr());
		if(set != null && set.size() > 0){
			String nextLaw = PubTools.getRandomEleFromSet(set);
			if(nextLaw != null && nextLaw.length() == 4){
				char[] arr = nextLaw.toCharArray();
				Set<AbstractChessPiece> resultSet = getWalkPiece(arr, board, Conf.getComputerRole());
				for (AbstractChessPiece piece : resultSet) {
					Position endPosition = piece.walkRecorde(board, String.valueOf(arr[2]), String.valueOf(arr[3]));
					if(endPosition == null){
						continue;
					}
					walkBean = new ChessWalkBean(piece.getCurrPosition(), endPosition);
				}
			}
		}
		return walkBean;
	}
	

	/**
	 * 根据棋谱的前两位便可确认走棋的棋子
	 * @param arr
	 * @param board
	 * @param ROLE 
	 * @return
	 */
	private static Set<AbstractChessPiece> getWalkPiece(char[] arr, ChessBoard board, Role ROLE) {
		Set<AbstractChessPiece> returnSet = new HashSet<AbstractChessPiece>();
		AbstractChessPiece returnPiece = null;
		if(arr[0] == '前' || arr[0] == '后'){
			Set<AbstractChessPiece> set = ChessTools.getPieceByName(board, String.valueOf(arr[1]), ROLE);
			set = operatePawns(set, String.valueOf(arr[1]));	// 处理“兵”、“卒”的情况
			AbstractChessPiece piece1 = PubTools.getSetIndexEle(set, 0);
			AbstractChessPiece piece2 = PubTools.getSetIndexEle(set, 1);
			if(arr[0] == '前'){
				returnPiece = ROLE == Role.RED ? (piece2.getCurrPosition().getY() > piece1.getCurrPosition().getY() ? piece2 : piece1) : (piece2.getCurrPosition().getY() > piece1.getCurrPosition().getY() ? piece1 : piece2);
			}else {
				returnPiece = ROLE == Role.RED ? (piece2.getCurrPosition().getY() > piece1.getCurrPosition().getY() ? piece1 : piece2) : (piece2.getCurrPosition().getY() > piece1.getCurrPosition().getY() ? piece2 : piece1);
			}
			returnSet.add(returnPiece);
		}else {
			Set<AbstractChessPiece> set = ChessTools.getPieceByName(board, String.valueOf(arr[0]), ROLE);
			int X = ChessTools.getXByRecordeShow(ROLE, String.valueOf(arr[1]));
			for (AbstractChessPiece piece : set) {
				if(piece.getCurrPosition().getX().equals(X)){
					returnSet.add(piece);
				}
			}
		}
		return returnSet;
	}

	private static Set<AbstractChessPiece> operatePawns(Set<AbstractChessPiece> set, String pieceName) {
		if(pieceName.equals("兵") || pieceName.equals("卒")){
			Map<Integer, Set<AbstractChessPiece>> map = new HashMap<Integer, Set<AbstractChessPiece>>();
			for (AbstractChessPiece abstractChessPiece : set) {
				Set<AbstractChessPiece> setTemp = map.get(abstractChessPiece.getCurrPosition().getX());
				if(setTemp == null){
					setTemp = new HashSet<AbstractChessPiece>();
					map.put(abstractChessPiece.getCurrPosition().getX(), setTemp);
				}
				setTemp.add(abstractChessPiece);
			}
			for (Set<AbstractChessPiece> eleSet : map.values()) {
				if(eleSet.size() == 2){
					set.clear();
					set.addAll(eleSet);
					break;
				}
			}
		}
		return set;
	}

}

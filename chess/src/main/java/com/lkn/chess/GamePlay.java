package com.lkn.chess;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.ChessWalkBean;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.Position;
import com.lkn.chess.bean.RoundTurn;
import com.lkn.chess.bean.WalkTrackBean;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;

/**
 * @author LiKangNing
 */
public class GamePlay {
	private Map<ChessWalkBean, Integer> bestResultMap = new HashMap<ChessWalkBean, Integer>();
	private Map<Integer, WalkTrackBean> bestTrackMap = new HashMap<Integer, WalkTrackBean>();	// 最佳行走轨迹
	private Map<Integer, Map<String, ChessWalkBean>> transfMap = new HashMap<Integer, Map<String,ChessWalkBean>>();	// 置换表算法---
	private ChessWalkBean walkBean = new ChessWalkBean();
	private int number = 0;	// 计数器，用来计算电脑思考的情况数目
	
	/**
	 * 电脑考虑后走棋
	 * 1、电脑考虑
	 * 2、电脑走棋
	 * @param chessBoard
	 */
	public void computerWalk(ChessBoard chessBoard){
		long begin = System.currentTimeMillis();
		computerThink(chessBoard);	// 电脑首先考虑
		walkBean.walkActual(bestTrackMap.get(1).getBegin(), bestTrackMap.get(1).getEnd(), chessBoard);	// 电脑走棋
		long end = System.currentTimeMillis();
		System.out.println("电脑思考耗时：   " + ((end - begin)/1000) + "   秒。 考虑情况：   " + number + "   种");
		System.out.println("当前棋谱：" + chessBoard.getChessRecordesBuffer().toString());
		number = 0;
		bestTrackMap.clear();
	}

	/**
	 * 电脑考虑
	 * 首先在棋谱中查找是否有可走的棋谱
	 * 如果找不到可走棋谱，那么再进行独立思考
	 * @param chessBoard
	 */
	public void computerThink(ChessBoard chessBoard){
		bestResultMap.clear();
		ChessWalkBean lawWalkBean = ReadOpeningChess.getNextChessLaw(chessBoard.getChessRecordesBuffer().toString(), chessBoard);
		lawWalkBean = lawWalkBean == null ? ReadOpeningChess.transfLawToWalkBean(chessBoard) : lawWalkBean;
		if(lawWalkBean != null){	// 棋谱中有对应记录
			bestTrackMap.put(1, new WalkTrackBean(lawWalkBean.getBeginPosition(), lawWalkBean.getEndPosition()));
		} else {	// 进行独立思考
			iterationThink(chessBoard, Conf.getComputerRole());
		}
	}
	
	/**
	 * 初始化最大最小值
	 * @param currWalkRole
	 * @return
	 */
	private int initVal(Role currWalkRole) {
		int returnVal = 0;
		if(currWalkRole == Conf.getComputerRole()){
			returnVal = Conf.getGamePlayMinVal();
		} else {
			returnVal = Conf.getGamePlayMaxVal();
		}
		return returnVal;
	}
	
	
	private int tempThinkDepth = -1;	// 临时变量，存放每次迭代思考的深度
	
	/**
	 * 迭代深入算法，考虑深度从1层-max层逐一深入
	 * @param board
	 * @param currWalkRole
	 */
	private void iterationThink(ChessBoard board, Role currWalkRole) {
		for (int i = 1; i <= Conf.getThinkDepth(); i++) {
			tempThinkDepth = i;
			transfMap.clear();	// 每次进行迭代深化的时候都清空一下置换表中的信息
			iterationThink(board, currWalkRole, 1, Conf.getGamePlayMaxVal());
		}
		int a = 0;
		Set<Integer> keySet = transfMap.keySet();
		for (Integer integer : keySet) {
			Map<String, ChessWalkBean> map = transfMap.get(integer);
			a = a + map.size();
		}
		System.out.println("置换表长度： " + a);
	}
	
	/**
	 * 迭代深入算法
	 * @param board
	 * @param currWalkRole
	 * @param depth
	 * @param lastStepVal
	 * @return
	 */
	private Integer iterationThink(ChessBoard board, Role currWalkRole, int depth, int lastStepVal) {
		int maxOrMinVal = initVal(currWalkRole);    // 初始化最大最小值
		ChessWalkBean transfWalkBean = findTransfMap(board, depth);    // 查找置换表，如果发现可走的路线，那么直接进行行走
//		transfWalkBean = null;
		if (transfWalkBean != null) {    // 如果在置换表中已经找到可直接行走的方式，那么不需要再向下递归
			AbstractChessPiece[] walkArr = walkBean.walk(transfWalkBean.getBeginPosition(), transfWalkBean.getEndPosition());    // 走一步
			maxOrMinVal = board.getHigherFightValByRole(Conf.getComputerRole());
			walkBean.walkBack(transfWalkBean.getBeginPosition(), transfWalkBean.getEndPosition(), walkArr);    // 回走
		} else {
			Set<AbstractChessPiece> set = board.getPiecesByPlayRole(currWalkRole);    // 获取当前角色可走的棋子集合
			WalkTrackBean trackBean = bestTrackMap.get(depth);    // 获取当前深度下的最优着法
			Set<AbstractChessPiece> sortedSet = getSortedSet(set, trackBean);    // 获取已经排序好的set
			Position currBestBeginPosition = null;
			Position currBestEndPosition = null;
			for (AbstractChessPiece piece : sortedSet) {    // 遍历每个棋子
				Position beginPosition = piece.getCurrPosition();    // 开始的位置
				Map<String, Position> sortedMap = getSortedMap(beginPosition, piece.getReachablePositions(board), trackBean);    // 对可走的位置进行排序
				for (Position position : sortedMap.values()) {
					if (isRepeatChess(beginPosition, position, depth, board)) {    // 检查是否存在常将的情况
						continue;
					}
					int val = 0;
					AbstractChessPiece[] walkArr = walkBean.walk(beginPosition, position);    // 走一步
					if (depth == tempThinkDepth || board.isKingEaten()) {    // 如果已经到达最大深度，或者对方的“将”已被吃掉
						val = board.getHigherFightValByRole(Conf.getComputerRole());
						++number;
					} else {
						val = iterationThink(board, Role.nextRole(currWalkRole), depth + 1, maxOrMinVal);
					}
					walkBean.walkBack(beginPosition, position, walkArr);    // 回走

					if (currWalkRole.equals(Conf.getComputerRole())) {
						if (val > maxOrMinVal) {
							maxOrMinVal = val;
							currBestBeginPosition = beginPosition;
							currBestEndPosition = position;
							bestTrackMap.put(depth, new WalkTrackBean(beginPosition, position));
						}
						if (val > lastStepVal) {    // 发生剪枝行为----β剪枝
							return Conf.getGamePlayMaxVal();
						}
					} else {
						if (val < maxOrMinVal) {
							maxOrMinVal = val;
							currBestBeginPosition = beginPosition;
							currBestEndPosition = position;
							bestTrackMap.put(depth, new WalkTrackBean(beginPosition, position));
						}
						if (val < lastStepVal) {    // 发生剪枝行为----α剪枝
							return Conf.getGamePlayMinVal();
						}
					}
				}
			}
			saveToTransfMap(board, currBestBeginPosition, currBestEndPosition, depth);    // 向置换表中存储
		}
		return maxOrMinVal;
	}

	/**
	 * 检查是否存在常将的情况
	 * @author:likn1	Feb 22, 2016  3:40:03 PM
	 * @param beginPosition
	 * @param endPosition
	 * @param depth
	 * @param board 
	 * @return
	 */
	private boolean isRepeatChess(Position beginPosition, Position endPosition, int depth, ChessBoard board) {
		boolean exist = false;
		if(depth == 1){
			List<RoundTurn> list = board.getChessRecordesList();
			if(list.size() >= 3){
				if(Conf.getComputerRole() == Role.BLACK){
					if(list.get(list.size() - 3).isSame(list.get(list.size() - 2))){
						if(list.get(list.size() - 1).isSameRed(list.get(list.size() - 2))){
							if(list.get(list.size() - 2).isSameBlack(beginPosition, endPosition)){
								exist = true;
							}
						}
					}
				} else {
					if(list.get(list.size() - 2).isSame(list.get(list.size() - 1))){
						if(list.get(list.size() - 1).isSameRed(beginPosition, endPosition)){
							exist = true;
						}
					}
				}
			}
		}
		return exist;
	}

	private ChessWalkBean findTransfMap(ChessBoard board, int depth) {
		ChessWalkBean chessWalkBean = null;
		String currPiecesStr = board.currPiecesStr();
		for (int i = 1; i <= depth; i++) {
			Map<String, ChessWalkBean> map = transfMap.get(depth);
			if(map != null){
				ChessWalkBean tempChessWalkBean = map.get(currPiecesStr);
				if(tempChessWalkBean != null){
					chessWalkBean = tempChessWalkBean;
					break;
				}
			}
		}
		return chessWalkBean;
	}

	/**
	 * 向置换表中存入相应的内容
	 * @author:likn1	Feb 14, 2016  10:40:06 AM
	 * @param board
	 * @param beginPosition
	 * @param endPosition
	 * @param depth
	 */
	private void saveToTransfMap(ChessBoard board, Position beginPosition, Position endPosition, int depth) {
		Integer thinkingMaxDepth = Conf.getThinkDepth();
		if(thinkingMaxDepth - depth <= 3 || beginPosition == null || endPosition == null){	// 思考深度小于3的置换表不予保留
			return ;
		}
		Map<String, ChessWalkBean> map = transfMap.get(depth);
		if(map == null){
			map = new HashMap<String, ChessWalkBean>();
			transfMap.put(depth, map);
		}
		map.put(board.currPiecesStr(), new ChessWalkBean(beginPosition, endPosition));
	}

	/**
	 * 仅对reachableMap进行重新排序，其余一概不做
	 * 将棋子可走的路线进行重新排序，将最佳走法放在第一位
	 * @param beginPosition
	 * @param reachableMap
	 * @param trackBean
	 * @return
	 */
	private Map<String, Position> getSortedMap(Position beginPosition, Map<String, Position> reachableMap, WalkTrackBean trackBean) {
		if(trackBean == null){	// 如果当前不存在最优解，那么直接返回当前reachableMap
			return reachableMap;
		}
		Map<String, Position> returnMap = null;
		if(trackBean.getBegin().isSameXandY(beginPosition)){
			returnMap = new LinkedHashMap<String, Position>();
			for (Position position : reachableMap.values()) {
				if(position.isSameXandY(trackBean.getEnd())){
					returnMap.put(position.getID(), position);
					break;
				}
			}
			for (Position position : reachableMap.values()) {
				if(!position.isSameXandY(trackBean.getEnd())){
					returnMap.put(position.getID(), position);
				}
			}
		}else {
			returnMap = reachableMap;
		}
		return returnMap;
	}

	/**
	 * 仅对set进行重新排序
	 * 对当前可走的棋子进行重新排序，将最优解放在第一位
	 * @param set
	 * @param trackBean
	 * @return
	 */
	private Set<AbstractChessPiece> getSortedSet(Set<AbstractChessPiece> set, WalkTrackBean trackBean) {
		if(trackBean == null){	// 如果当前不存在最优解，那么直接返回当前set
			return set;
		}
		Set<AbstractChessPiece> sortedSet = new LinkedHashSet<AbstractChessPiece>(set.size());
		Position begin = trackBean.getBegin();
		for (AbstractChessPiece piece : set) {	// 此循环将最好结果首先加入set的首位
			if(piece.getCurrPosition().isSameXandY(begin)){
				sortedSet.add(piece);
				break;
			}
		}
		for (AbstractChessPiece piece : set) {	// 此循环将其他结果加入set中
			if(!piece.getCurrPosition().isSameXandY(begin)){
				sortedSet.add(piece);
			}
		}
		return sortedSet;
	}
}

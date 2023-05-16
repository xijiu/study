package com.lkn.chess;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.ChessWalkBean;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.Position;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;

/**
 * 最简单的极小极大值算法，采用最简单的αβ剪纸，效率低下
 * 主要为测试类，跟优化算法进行对比
 * @author pc
 *
 */
public class GamePlayLow {
	private Map<ChessWalkBean, Integer> bestResultMap = new HashMap<ChessWalkBean, Integer>();
	
	public void gamePlay(){
		ChessBoard board = new ChessBoard();
		board.init();
	}
	
	/**
	 * 电脑考虑后走棋
	 * 1、电脑考虑
	 * 2、电脑走棋
	 * @param board
	 */
	public void computerWalk(ChessBoard chessBoard){
		long begin = System.currentTimeMillis();
		computerThink(chessBoard);	// 电脑首先考虑
		computerWalkAfterThink(chessBoard);	// 电脑走棋
		long end = System.currentTimeMillis();
		System.out.println("电脑思考耗时：   " + ((end - begin)/1000) + "   秒");
		System.out.println("考虑情况：   " + number + "   种");
		number = 0;
	}

	/**
	 * 1、电脑考虑
	 * @param board
	 */
	public void computerThink(ChessBoard chessBoard){
		bestResultMap.clear();
		walkThink(chessBoard, Conf.getComputerRole(), 1, Conf.getGamePlayMaxVal());
	}
	
	/**
	 * 2、电脑走棋
	 * 随机从最优解中选出一个走法
	 */
	private void computerWalkAfterThink(ChessBoard chessBoard) {
		walkBean.walk(bestBeginPosition, bestEndPosition);
	}
	
	private Position bestBeginPosition = null;
	private Position bestEndPosition = null;
	private ChessWalkBean walkBean = new ChessWalkBean();
	private int number = 0;
	private Integer walkThink(ChessBoard board, Role currWalkRole, int depth, int lastStepVal){
		Set<AbstractChessPiece> set = board.getPiecesByPlayRole(currWalkRole);	// 获取当前角色可走的棋子集合
		int maxOrMinVal = initVal(currWalkRole);
		for (AbstractChessPiece piece : set) {	// 遍历每个棋子
			Position beginPosition = piece.getCurrPosition();	// 开始的位置
			Map<String, Position> reachableMap = piece.getReachablePositions(board);	// 获取该棋子可行走的位置集合
			for (Position position : reachableMap.values()) {
				int val = 0;
				AbstractChessPiece[] walkArr = walkBean.walk(beginPosition, position);	// 走一步
				if(depth == Conf.getThinkDepth()){	// 如果已经到达最大深度
					val = board.getHigherFightValByRole(Conf.getComputerRole());
					++number;
				} else {
					val = walkThink(board, Role.nextRole(currWalkRole), depth + 1, maxOrMinVal);
				}
				walkBean.walkBack(beginPosition, position, walkArr);	// 回走
				
				if(currWalkRole.equals(Conf.getComputerRole())){
					if(depth == 1 && val > maxOrMinVal){
						System.out.println(val);
						bestBeginPosition = beginPosition;
						bestEndPosition = position;
					}
					if(val > maxOrMinVal){
						maxOrMinVal = val;
					}
					if(val > lastStepVal){	// β剪枝
						return Conf.getGamePlayMaxVal();
					}
				} else {
					maxOrMinVal = val < maxOrMinVal ? val : maxOrMinVal;
					if(val < lastStepVal){	// α剪枝
						return Conf.getGamePlayMinVal();
					}
				}
			}
		}
		return maxOrMinVal;
	}

	private int initVal(Role currWalkRole) {
		int returnVal = 0;
		if(currWalkRole == Conf.getComputerRole()){
			returnVal = Conf.getGamePlayMinVal();
		} else {
			returnVal = Conf.getGamePlayMaxVal();
		}
		return returnVal;
	}
}

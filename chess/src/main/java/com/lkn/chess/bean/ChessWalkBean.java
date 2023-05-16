package com.lkn.chess.bean;

import com.lkn.chess.Conf;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;

/**
 * 下棋步骤的实体bean
 * 
 * @author:likn1 Jan 8, 2016 2:09:57 PM
 */
public class ChessWalkBean {
	private String id;
	private Position beginPosition; // 走子的起始位置
	private Position endPosition; // 走子的结束位置
	private AbstractChessPiece piece; // 走子的棋子
	private boolean isEat; // 是否吃子
	private AbstractChessPiece eatenPiece; // 被吃的棋子
	private Integer fightVal; // 吃掉的战斗力
	
	public ChessWalkBean(){
	}
	
	public ChessWalkBean(Position beginPosition, Position endPosition){
		this.beginPosition = beginPosition;
		this.endPosition = endPosition;
		this.piece = beginPosition.getPiece();
		this.isEat = endPosition.isExistPiece() ? true : false;	// 结束位置存在棋子吗？ 如果存在，那么就有吃子行为，否则没有
		if(isEat){	// 如果存在吃子行为
			this.eatenPiece = endPosition.getPiece();
			this.fightVal = eatenPiece.getFightVal();
		}
	}
	
	/**
	 * 电脑的行走价值
	 * 1、如果是吃掉的对方的子，那么将返回该子的权值
	 * 2、如果吃掉的是自己的子，那么将返回该子的权值的负数
	 * 3、如果没有吃子行为，那么将返回0
	 * @author:likn1	Jan 19, 2016  6:24:11 PM
	 * @return
	 */
	public Integer walkValue(){
		int val = 0;
		if(isEat){
			if(eatenPiece.getPLAYER_ROLE().equals(Conf.getComputerRole())){	// 如果被吃的棋子为电脑本身
				val = -fightVal;
			}else {
				val = fightVal;
			}
		}
		return val;
	}
	
	/**
	 * 棋盘真实的发生变化
	 * @author:likn1	Feb 22, 2016  2:49:56 PM
	 * @param beginPosition
	 * @param endPosition
	 */
	public void walkActual(Position beginPosition, Position endPosition, ChessBoard board){
		walk(beginPosition, endPosition);	// 电脑走棋
		board.chessRecordes(beginPosition, endPosition);	// 记录棋谱
	}
	
	/**
	 * 行走，一般用于电脑思考时模拟走棋的过程
	 * @author:likn1	Feb 22, 2016  2:50:13 PM
	 * @param beginPosition
	 * @param endPosition
	 * @return
	 */
	public AbstractChessPiece[] walk(Position beginPosition, Position endPosition){
		AbstractChessPiece eatenPiece = null;	//  被吃掉的棋子，如果没有发生吃子行为，那么将返回null
		if(endPosition.isExistPiece()){	// 如果存在吃子行为
			eatenPiece = endPosition.getPiece();
			eatenPiece.setAlive(false);	// 被吃掉的棋子设置为不在战斗
		}
		AbstractChessPiece walkPiece = beginPosition.getPiece();
		beginPosition.setPiece(null);	// 将之前的位置设置为null
		walkPiece.setCurrPosition(endPosition, true);	// 棋子变更位置，同时改变该棋子的战斗力
		AbstractChessPiece[] arr = {walkPiece, eatenPiece};
		return arr;
	}
	
	/**
	 * 回走
	 * @author:likn1	Jan 22, 2016  3:20:37 PM
	 * @param beginPosition
	 * @param endPosition
	 * @param arr
	 */
	public void walkBack(Position beginPosition, Position endPosition, AbstractChessPiece[] arr) {
		AbstractChessPiece beginPiece = arr[0];
		AbstractChessPiece endPiece = arr[1];
		if(endPiece != null){	// 如果存在吃子行为
			endPiece.setAlive(true);	// 被吃掉的棋子设置为在战斗
			endPiece.setCurrPosition(endPosition, true);
		} else {
			endPosition.setPiece(null);
		}
		beginPiece.setCurrPosition(beginPosition, true);
	}
	
	/**
	 * 当前的对象跟bean在开始与结束位置是否相同
	 * @author:likn1	Jan 11, 2016  3:55:32 PM
	 * @param bean
	 * @return
	 */
	public boolean isSameBeginAndEndPosition(ChessWalkBean bean){
		boolean isSame = false;
		Integer currBeginX = this.beginPosition.getX();
		Integer currBeginY = this.beginPosition.getY();
		Integer currEndX = this.endPosition.getX();
		Integer currEndY = this.endPosition.getY();
		if(bean != null){
			Integer beginX = bean.getBeginPosition().getX();
			Integer beginY = bean.getBeginPosition().getY();
			Integer endX = bean.getEndPosition().getX();
			Integer endY = bean.getEndPosition().getY();
			if(currBeginX.equals(beginX) && currBeginY.equals(beginY) && currEndX.equals(endX) && currEndY.equals(endY)){
				isSame = true;
			}
		}
		return isSame;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Position getBeginPosition() {
		return beginPosition;
	}

	public void setBeginPosition(Position beginPosition) {
		this.beginPosition = beginPosition;
	}

	public Position getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(Position endPosition) {
		this.endPosition = endPosition;
	}

	public AbstractChessPiece getPiece() {
		return piece;
	}

	public void setPiece(AbstractChessPiece piece) {
		this.piece = piece;
	}

	public boolean isEat() {
		return isEat;
	}

	public void setEat(boolean isEat) {
		this.isEat = isEat;
	}

	public AbstractChessPiece getEatenPiece() {
		return eatenPiece;
	}

	public void setEatenPiece(AbstractChessPiece eatenPiece) {
		this.eatenPiece = eatenPiece;
	}

	public Integer getFightVal() {
		return fightVal;
	}

	public void setFightVal(Integer fightVal) {
		this.fightVal = fightVal;
	}

}

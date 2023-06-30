package com.lkn.chess.bean;

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AbstractChessPiece getPiece() {
		return piece;
	}

	public void setPiece(AbstractChessPiece piece) {
		this.piece = piece;
	}
}

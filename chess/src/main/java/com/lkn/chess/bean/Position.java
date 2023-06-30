package com.lkn.chess.bean;

import com.lkn.chess.bean.chess_piece.AbstractChessPiece;

public class Position implements Cloneable {
	private final String ID; // 位置的唯一编号
	private final Integer x; // 横坐标的值
	private final Integer y; // 纵坐标的值
	private boolean existPiece; // 是否存在棋子
	private AbstractChessPiece piece; // 存在棋子的对象，如果没有则为null

	public Position(Integer x, Integer y, Boolean existPiece, AbstractChessPiece piece) {
		this.x = x;
		this.y = y;
		this.existPiece = existPiece;
		this.piece = piece;
		this.ID = String.valueOf(x) + y;
	}

	public String getID() {
		return ID;
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	public boolean isExistPiece() {
		return existPiece;
	}

	public void setExistPiece(boolean existPiece) {
		this.existPiece = existPiece;
	}

	public AbstractChessPiece getPiece() {
		return piece;
	}

	public boolean isSameXandY(Position position){
		if(position != null && position.getX().equals(this.x) && position.getY().equals(this.y)){
			return true;
		}
		return false;
	}

}

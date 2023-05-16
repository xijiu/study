package com.lkn.chess.bean;

import com.lkn.chess.bean.chess_piece.AbstractChessPiece;

public class Position implements Cloneable{
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
	
	/**
	 * 为该位置放置棋子
	 * 如果放置的棋子为null，那么将existPiece变量设置为false，否则设置为true
	 * @author:likn1	Jan 6, 2016  2:51:04 PM
	 * @param piece
	 */
	public void setPiece(AbstractChessPiece piece) {
		this.piece = piece;
		if(piece == null){
			this.existPiece = false;
		}else {
			this.existPiece = true;
			if(!this.isSameXandY(piece.getCurrPosition())){
				piece.setCurrPosition(this);	// 将该棋子对应的位置信息设为本类
			}
		}
	}
	
	public Position clone(){
		Position clone = null;
		try {
			clone = (Position)super.clone();
			AbstractChessPiece clonePiece = clone.getPiece() == null ? null : clone.getPiece().cloneImpl(clone);
			clone.setPiece(clonePiece);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}

}

package com.lkn.chess.bean;

/**
 * 一个来回 先手走棋后，后手走棋
 * 
 * @author:likn1 Feb 22, 2016 3:10:16 PM
 */
public class RoundTurn {
	private Position redBeginPosition;
	private Position redEndPosition;
	private Position blackBeginPosition;
	private Position blackEndPosition;
	
	public RoundTurn(Position redBeginPosition, Position redEndPosition, Position blackBeginPosition, Position blackEndPosition){
		this.redBeginPosition = redBeginPosition;
		this.redEndPosition = redEndPosition;
		this.blackBeginPosition = blackBeginPosition;
		this.blackEndPosition = blackEndPosition;
	}
	
	public void setBlackPosition(Position blackBeginPosition, Position blackEndPosition){
		this.blackBeginPosition = blackBeginPosition;
		this.blackEndPosition = blackEndPosition;
	}

	public Position getRedBeginPosition() {
		return redBeginPosition;
	}

	public void setRedBeginPosition(Position redBeginPosition) {
		this.redBeginPosition = redBeginPosition;
	}

	public Position getRedEndPosition() {
		return redEndPosition;
	}

	public void setRedEndPosition(Position redEndPosition) {
		this.redEndPosition = redEndPosition;
	}

	public Position getBlackBeginPosition() {
		return blackBeginPosition;
	}

	public void setBlackBeginPosition(Position blackBeginPosition) {
		this.blackBeginPosition = blackBeginPosition;
	}

	public Position getBlackEndPosition() {
		return blackEndPosition;
	}

	public void setBlackEndPosition(Position blackEndPosition) {
		this.blackEndPosition = blackEndPosition;
	}
	
	public boolean isSame(RoundTurn turn){
		boolean same = false;
		if(redBeginPosition.isSameXandY(turn.getRedBeginPosition()) && redEndPosition.isSameXandY(turn.getRedEndPosition()) && blackBeginPosition.isSameXandY(turn.getBlackBeginPosition()) && blackEndPosition.isSameXandY(turn.getBlackEndPosition())){
			same = true;
		}
		return same;
	}
	
	public boolean isSameRed(RoundTurn turn){
		boolean same = false;
		if(redBeginPosition.isSameXandY(turn.getRedBeginPosition()) && redEndPosition.isSameXandY(turn.getRedEndPosition())){
			same = true;
		}
		return same;
	}
	
	public boolean isSameBlack(RoundTurn turn){
		boolean same = false;
		if(blackBeginPosition.isSameXandY(turn.getBlackBeginPosition()) && blackEndPosition.isSameXandY(turn.getBlackEndPosition())){
			same = true;
		}
		return same;
	}
	
	public boolean isSameRed(Position redBeginPosition, Position redEndPosition){
		boolean same = false;
		if(redBeginPosition.isSameXandY(this.redBeginPosition) && redEndPosition.isSameXandY(this.redEndPosition)){
			same = true;
		}
		return same;
	}
	
	public boolean isSameBlack(Position blackBeginPosition, Position blackEndPosition){
		boolean same = false;
		if(blackBeginPosition.isSameXandY(this.blackBeginPosition) && blackEndPosition.isSameXandY(this.blackEndPosition)){
			same = true;
		}
		return same;
	}

}

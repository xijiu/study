package com.lkn.chess.bean;

/**
 * 行走轨迹的实体bean
 * 
 * @author:likn1 Jan 28, 2016 2:09:10 PM
 */
public class WalkTrackBean {
	private Position begin;
	private Position end;
	private int depth;

	public WalkTrackBean(Position begin, Position end) {
		setBegin(begin);
		setEnd(end);
	}
	
	public void toStr(){
		System.out.println("begin:" + begin.getID() + "     end:" + end.getID());
	}

	public Position getBegin() {
		return begin;
	}

	public void setBegin(Position begin) {
		this.begin = begin;
	}

	public Position getEnd() {
		return end;
	}

	public void setEnd(Position end) {
		this.end = end;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

}

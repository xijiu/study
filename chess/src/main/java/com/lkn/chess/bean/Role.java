package com.lkn.chess.bean;
/**
 * 象棋角色，分为先手跟后手
 * @author:likn1	Jan 5, 2016  11:29:54 AM
 */
public enum Role {
	RED,	// 先手
	BLACK;	// 后手
	
	/**
	 * 下一步该走棋的角色
	 * @param currRole
	 * @return
	 */
	public static Role nextRole(Role currRole){
		if(currRole.equals(Role.RED)){
			return Role.BLACK;
		}else {
			return Role.RED;
		}
	}

	public Role nextRole() {
		if(this == RED){
			return Role.BLACK;
		}else {
			return Role.RED;
		}
	}
}

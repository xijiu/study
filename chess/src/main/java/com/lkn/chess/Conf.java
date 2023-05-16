package com.lkn.chess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.ChessWalkBean;
import com.lkn.chess.bean.Role;

/**
 * 配置类
 * @author LiKangNing
 *
 */
public class Conf {
	private static File chessFile = new File(System.getProperty("user.dir") + "/ChessSourceFile.txt");	// 棋谱文件
	private static final boolean loadChessFile = false;	// 是否加载棋谱
	public static final Integer THINK_DEPTH = 5;	// 考虑深度
	private static Role computerRole = Role.BLACK;	// 电脑的默认角色为后手
	public final static int GAME_PLAY_MAX_VAL = 100000;
	public final static int GAME_PLAY_MIN_VAL = -GAME_PLAY_MAX_VAL;
	private static Set<String> redWinSet = new HashSet<String>();
	private static Set<String> blackWinSet = new HashSet<String>();
	private static Set<String> peaceSet = new HashSet<String>();
	private static Map<String, Character> pieceNumberAndFlagMap = new HashMap<String, Character>();	// 棋子编号与标记位的map
	private static Map<String, Set<String>> redNextStepMap = new HashMap<String, Set<String>>();	// 存放着红旗下一步应该走的着法；key为长度为90的精简的棋盘状态，value为着法
	private static Map<String, Set<String>> blackNextStepMap = new HashMap<String, Set<String>>();	// 存放着黑旗下一步应该走的着法；key为长度为90的精简的棋盘状态，value为着法
	
	static{
		pieceNumberAndFlagMap.put("00", 'a');
		pieceNumberAndFlagMap.put("10", 'b');
		pieceNumberAndFlagMap.put("20", 'c');
		pieceNumberAndFlagMap.put("30", 'd');
		pieceNumberAndFlagMap.put("40", 'e');
		pieceNumberAndFlagMap.put("50", 'f');
		pieceNumberAndFlagMap.put("60", 'g');
		pieceNumberAndFlagMap.put("70", 'h');
		pieceNumberAndFlagMap.put("80", 'i');
		pieceNumberAndFlagMap.put("12", 'j');
		pieceNumberAndFlagMap.put("72", 'k');
		pieceNumberAndFlagMap.put("03", 'l');
		pieceNumberAndFlagMap.put("23", 'm');
		pieceNumberAndFlagMap.put("43", 'n');
		pieceNumberAndFlagMap.put("63", 'o');
		pieceNumberAndFlagMap.put("83", 'p');
		pieceNumberAndFlagMap.put("89", 'A');
		pieceNumberAndFlagMap.put("79", 'B');
		pieceNumberAndFlagMap.put("69", 'C');
		pieceNumberAndFlagMap.put("59", 'D');
		pieceNumberAndFlagMap.put("49", 'E');
		pieceNumberAndFlagMap.put("39", 'F');
		pieceNumberAndFlagMap.put("29", 'G');
		pieceNumberAndFlagMap.put("19", 'H');
		pieceNumberAndFlagMap.put("09", 'I');
		pieceNumberAndFlagMap.put("77", 'J');
		pieceNumberAndFlagMap.put("17", 'K');
		pieceNumberAndFlagMap.put("86", 'L');
		pieceNumberAndFlagMap.put("66", 'M');
		pieceNumberAndFlagMap.put("46", 'N');
		pieceNumberAndFlagMap.put("26", 'O');
		pieceNumberAndFlagMap.put("06", 'P');
		
		if(loadChessFile){
			ChessBoard board = new ChessBoard();
			BufferedReader br = null;
			try {
				int lineNum = 200;
				br = new BufferedReader(new FileReader(chessFile));
				String line = null;
				int num = 0;
				while((line = br.readLine()) != null){
					if(num >= lineNum){
						break;
					}
					System.out.println(num++);
					if(line.endsWith("1")){	// 红胜
						operateChessLaw(line, "1", board);	// 解析棋谱
						redWinSet.add(line);
					}else if(line.endsWith("2")){
						operateChessLaw(line, "2", board);	// 解析棋谱
						blackWinSet.add(line);
					}else if(line.endsWith("3")){
						operateChessLaw(line, "3", board);	// 解析棋谱
						peaceSet.add(line);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
		{  I,  H,  G,  F,  E,  D,  C,  B,  A},
		{  0,  0,  0,  0,  0,  0,  0,  0,  0},
		{  0,  K,  0,  0,  0,  0,  0,  J,  0},
		{  P,  0,  O,  0,  N,  0,  M,  0,  L},
		{  0,  0,  0,  0,  0,  0,  0,  0,  0},
		{  0,  0,  0,  0,  0,  0,  0,  0,  0},
		{  l,  0,  m,  0,  n,  0,  o,  0,  p},
		{  0,  j,  0,  0,  0,  0,  0,  k,  0},
		{  0,  0,  0,  0,  0,  0,  0,  0,  0},
		{  a,  b,  c,  d,  e,  f,  g,  h,  i}
	 * 
	 * */
	
	
	
	public static Map<String, Character> getPieceNumberAndFlagMap() {
		return pieceNumberAndFlagMap;
	}

	/**
	 * 处理一个棋谱
	 * @author:likn1	Feb 2, 2016  2:19:04 PM
	 * @param line
	 * @param flag	1：红胜	2：黑胜	3：平局
	 */
	private static void operateChessLaw(String line, String flag, ChessBoard board) {
		board.init();
		String currPiecesStr = board.currPiecesStr();
		String[] lawArr = line.split(" ");
		for (int i = 0; i < lawArr.length; i++) {
			if(lawArr[i] == null || lawArr[i].length() != 4){
				break;
			}
//			System.out.println("CCCCCCCCCCCCCC: " + flag + " : " + lawArr[i]);
			putSingleElementToMap(currPiecesStr, lawArr[i], flag, i);
			ChessWalkBean walkBean = ReadOpeningChess.transfLawToWalkBean(lawArr[i], board);
			if(walkBean == null){	// 如果当前棋谱记载不正确，那么将忽视该棋谱
				board.show();
				break;
			}
			walkBean.walk(walkBean.getBeginPosition(), walkBean.getEndPosition());
			currPiecesStr = board.currPiecesStr();
		}
	}
	
	private static void putSingleElementToMap(String currPiecesStr, String value, String flag, int i) {
		if(flag.equals("1") && (i % 2 == 0)){
			putSingleElementToMap(currPiecesStr, value, flag);
		}
		if(flag.equals("2") && (i % 2 == 1)){
			putSingleElementToMap(currPiecesStr, value, flag);
		}
		if(flag.equals("3")){
			putSingleElementToMap(currPiecesStr, value, "1");
			putSingleElementToMap(currPiecesStr, value, "2");
		}
	}

	private static void putSingleElementToMap(String currPiecesStr, String value, String flag) {
		Map<String, Set<String>> map = null;
		if(flag.equals("1")){
			map = redNextStepMap;
		}else if(flag.equals("2")){
			map = blackNextStepMap;
		}
		putSingleElementToMap(currPiecesStr, value, map);
	}

	private static void putSingleElementToMap(String key, String value, Map<String, Set<String>> map){
		if(map == null){
			return ;
		}
		Set<String> set = map.get(key);
		if(set == null){
			set = new HashSet<String>();
			map.put(key, set);
		}
		set.add(value);
	}

	public static Set<String> getRedWinSet() {
		return redWinSet;
	}

	public static Set<String> getBlackWinSet() {
		return blackWinSet;
	}

	public static Set<String> getPeaceSet() {
		return peaceSet;
	}

	public static File getChessFile() {
		return chessFile;
	}

	public static Integer getThinkDepth() {
		return THINK_DEPTH;
	}

	public static Role getComputerRole() {
		return computerRole;
	}

	public static void setComputerRole(Role computerRole) {
		Conf.computerRole = computerRole;
	}

	public static int getGamePlayMaxVal() {
		return GAME_PLAY_MAX_VAL;
	}

	public static int getGamePlayMinVal() {
		return GAME_PLAY_MIN_VAL;
	}

	public static Map<String, Set<String>> getRedNextStepMap() {
		return redNextStepMap;
	}

	public static void setRedNextStepMap(Map<String, Set<String>> redNextStepMap) {
		Conf.redNextStepMap = redNextStepMap;
	}

	public static Map<String, Set<String>> getBlackNextStepMap() {
		return blackNextStepMap;
	}

	public static void setBlackNextStepMap(Map<String, Set<String>> blackNextStepMap) {
		Conf.blackNextStepMap = blackNextStepMap;
	}
	
	
}

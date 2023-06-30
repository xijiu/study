package com.lkn.chess;

import java.io.File;

/**
 * 配置类
 * @author LiKangNing
 *
 */
public class Conf {
	private static File chessFile = new File(System.getProperty("user.dir") + "/ChessSourceFile.txt");	// 棋谱文件
	public static DifficultyLevel DIFFICULTY_LEVEL = DifficultyLevel.HARD;
	/** 分为3个难度：小孩儿、大叔、老头 */
	public static int difficulty = DIFFICULTY_LEVEL.difficulty;
	/** 考虑深度 */
	public static int THINK_DEPTH = DIFFICULTY_LEVEL.thinkDepth;
	/** 是否简单计算棋子的价值 */
	public static final boolean SIMPLE_VALUE = false;
	public final static int GAME_PLAY_MAX_VAL = 100000;
	public final static int GAME_PLAY_MIN_VAL = -GAME_PLAY_MAX_VAL;

	public static void changeDifficulty(DifficultyLevel level) {
		DIFFICULTY_LEVEL = level;
		difficulty = DIFFICULTY_LEVEL.difficulty;
		THINK_DEPTH = DIFFICULTY_LEVEL.thinkDepth;
	}
	

	public static File getChessFile() {
		return chessFile;
	}
}

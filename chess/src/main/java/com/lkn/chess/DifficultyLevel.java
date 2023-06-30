package com.lkn.chess;

/**
 * @author xijiu
 * @since 2023/5/26 上午9:44
 */
public enum DifficultyLevel {

    EASY(1, 3),

    MID(2, 4),

    HARD(3, 7);

    public final int difficulty;

    public final int thinkDepth;

    DifficultyLevel(int difficulty, int thinkDepth) {
        this.difficulty = difficulty;
        this.thinkDepth = thinkDepth;
    }

}

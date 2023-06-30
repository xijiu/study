package com.lkn.chess.bean;

/**
 * @author xijiu
 * @since 2023/5/29 下午5:11
 */
public class ExchangeTable {
    private byte level;
    private byte fromPos;
    private byte toPos;
    /** 局面评分 */
    private int value;

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getFromPos() {
        return fromPos;
    }

    public void setFromPos(byte fromPos) {
        this.fromPos = fromPos;
    }

    public byte getToPos() {
        return toPos;
    }

    public void setToPos(byte toPos) {
        this.toPos = toPos;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

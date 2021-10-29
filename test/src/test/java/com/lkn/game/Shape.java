package com.lkn.game;

public abstract class Shape {

    protected static byte[][] board = Game.board;

    protected int line = 0;
    protected int pos = 0;
    protected int currForm = 1;

    protected abstract boolean tryPut();

    protected boolean put(byte code, int posLen, int lineLen, byte[][] shape,
                        int lOfs1, int rOft1, int lOfs2, int rOft2,
                        int lOfs3, int rOft3, int lOfs4, int rOft4) {
        while (true) {
            if (pos >= posLen) {
                pos = 0;
                line++;
                continue;
            }
            if (line >= lineLen) {
                currForm++;
                pos = 0;
                line = 0;
                return false;
            }

            byte left = (byte) line;
            byte right = shape[line][pos++];
            if (board[left][right] == 0
                    && board[left + lOfs1][right + rOft1] == 0
                    && board[left + lOfs2][right + rOft2] == 0
                    && board[left + lOfs3][right + rOft3] == 0
                    && board[left + lOfs4][right + rOft4] == 0) {
                board[left][right] = code;
                board[left + lOfs1][right + rOft1] = code;
                board[left + lOfs2][right + rOft2] = code;
                board[left + lOfs3][right + rOft3] = code;
                board[left + lOfs4][right + rOft4] = code;
                return true;
            }
        }
    }

    public void reset() {
        line = 0;
        pos = 0;
        currForm = 1;
    }
}

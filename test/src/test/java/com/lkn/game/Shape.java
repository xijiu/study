package com.lkn.game;

public abstract class Shape {

    protected byte[][] board;

    protected int line = 0;
    protected int pos = 0;
    protected int currForm = 1;
    protected Shape next = null;


    public Shape(byte[][] board, Shape next) {
        this.board = board;
        this.next = next;
    }

    protected abstract boolean tryPut();

    protected boolean put(byte code, int posLen, int lineLen, int[][] shape, int[] posArr) {
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

            int left = line;
            int right = shape[line][pos++];
            if (board[left][right] == 0
                    && board[left + posArr[0]][right + posArr[1]] == 0
                    && board[left + posArr[2]][right + posArr[3]] == 0
                    && board[left + posArr[4]][right + posArr[5]] == 0
                    && board[left + posArr[6]][right + posArr[7]] == 0) {
                board[left][right] = code;
                board[left + posArr[0]][right + posArr[1]] = code;
                board[left + posArr[2]][right + posArr[3]] = code;
                board[left + posArr[4]][right + posArr[5]] = code;
                board[left + posArr[6]][right + posArr[7]] = code;
                return true;
            }
        }
    }

    protected boolean putPos(byte code, int posLen, int lineLen, int[][] shape, int[] posArr) {
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

            int left = line;
            int right = shape[line][pos++];
            if (board[left][right] == 0
                    && board[left + posArr[0]][right + posArr[1]] == 0
                    && board[left + posArr[2]][right + posArr[3]] == 0
                    && board[left + posArr[4]][right + posArr[5]] == 0
                    && board[left + posArr[6]][right + posArr[7]] == 0) {
                board[left][right] = code;
                board[left + posArr[0]][right + posArr[1]] = code;
                board[left + posArr[2]][right + posArr[3]] = code;
                board[left + posArr[4]][right + posArr[5]] = code;
                board[left + posArr[6]][right + posArr[7]] = code;
                return true;
            }
        }
    }

    protected abstract int getCode();

    public void reset() {
        line = 0;
        pos = 0;
        currForm = 1;
    }

    protected int[][] genericArr(int[][] originArr) {
        int line = 5 - originArr.length + 1;
        int col = Game.steps - originArr[0].length + 1;
        int index = 0;
        for (int i = 0; i < originArr[0].length; i++) {
            if (originArr[0][i] == 0) {
                index++;
            } else if (originArr[0][i] == 1) {
                break;
            }
        }
        int[][] target = new int[line][col];
        for (int i = 0; i < line; i++) {
            for (int j = 0; j < col; j++) {
                target[i][j] = (index + j);
            }
        }
        return target;
    }

    protected int[] genericPosArr(int[][] originArr) {
        int[] resultArr = new int[8];
        int index = 0;
        int firstI = -1;
        int firstJ = -1;
        for (int i = 0; i < originArr.length; i++) {
            for (int j = 0; j < originArr[0].length; j++) {
                if (originArr[i][j] == 1) {
                    if (firstI == -1) {
                        firstI = i;
                        firstJ = j;
                    } else {
                        resultArr[index++] = i - firstI;
                        resultArr[index++] = j - firstJ;
                    }
                }
            }
        }
        return resultArr;
    }

}

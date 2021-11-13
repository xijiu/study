package com.lkn.new_game;

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

    protected abstract boolean tryPut(int i, int j);

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
            if (board[left][right] == -1
                    && board[left + posArr[0]][right + posArr[1]] == -1
                    && board[left + posArr[2]][right + posArr[3]] == -1
                    && board[left + posArr[4]][right + posArr[5]] == -1
                    && board[left + posArr[6]][right + posArr[7]] == -1) {
                board[left][right] = code;
                board[left + posArr[0]][right + posArr[1]] = code;
                board[left + posArr[2]][right + posArr[3]] = code;
                board[left + posArr[4]][right + posArr[5]] = code;
                board[left + posArr[6]][right + posArr[7]] = code;
                return true;
            }
        }
    }

    protected boolean putToPos(byte code, int[] posArr, int i, int j) {
        currForm++;
        int pos_1_i = i + posArr[0];
        int pos_1_j = j + posArr[1];
        int pos_2_i = i + posArr[2];
        int pos_2_j = j + posArr[3];
        int pos_3_i = i + posArr[4];
        int pos_3_j = j + posArr[5];
        int pos_4_i = i + posArr[6];
        int pos_4_j = j + posArr[7];

        if (!(pos_1_i >= 0 && pos_1_i < 5
                && pos_2_i >= 0 && pos_2_i < 5
                && pos_3_i >= 0 && pos_3_i < 5
                && pos_4_i >= 0 && pos_4_i < 5
                && pos_1_j >= 0 && pos_1_j < 12
                && pos_2_j >= 0 && pos_2_j < 12
                && pos_3_j >= 0 && pos_3_j < 12
                && pos_4_j >= 0 && pos_4_j < 12)) {
            return false;
        }

        if (board[i][j] == -1
                && board[pos_1_i][pos_1_j] == -1
                && board[pos_2_i][pos_2_j] == -1
                && board[pos_3_i][pos_3_j] == -1
                && board[pos_4_i][pos_4_j] == -1) {
            board[i][j] = code;
            board[i + posArr[0]][j + posArr[1]] = code;
            board[i + posArr[2]][j + posArr[3]] = code;
            board[i + posArr[4]][j + posArr[5]] = code;
            board[i + posArr[6]][j + posArr[7]] = code;
            return true;
        }
        return false;
    }

    protected abstract int getCode();

    public void reset() {
        line = 0;
        pos = 0;
        currForm = 1;
    }

    protected int[][] genericArr(int[][] originArr) {
        int line = 5 - originArr.length + 1;
        int col = NewGame.steps - originArr[0].length + 1;
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

        for (int j = 0; j < originArr[0].length; j++) {
            for (int i = 0; i < originArr.length; i++) {
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

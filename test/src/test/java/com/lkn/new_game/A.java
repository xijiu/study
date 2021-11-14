package com.lkn.new_game;

public class A extends Shape {

    private static final byte code = 0;

    private final int[][] origin1 = new int[][] {
            {1, 0},
            {1, 1},
            {1, 1},
    };
    private final int[][] origin2 = new int[][] {
            {1, 1, 1},
            {1, 1, 0},
    };
    private final int[][] origin3 = new int[][] {
            {1, 1},
            {1, 1},
            {0, 1},
    };
    private final int[][] origin4 = new int[][] {
            {0, 1, 1},
            {1, 1, 1},
    };
    private final int[][] origin5 = new int[][] {
            {0, 1},
            {1, 1},
            {1, 1},
    };
    private final int[][] origin6 = new int[][] {
            {1, 1, 1},
            {0, 1, 1},
    };
    private final int[][] origin7 = new int[][] {
            {1, 1},
            {1, 1},
            {1, 0},
    };
    private final int[][] origin8 = new int[][] {
            {1, 1, 0},
            {1, 1, 1},
    };
    private final int[] posArr1 = genericPosArr(origin1);
    private final int[] posArr2 = genericPosArr(origin2);
    private final int[] posArr3 = genericPosArr(origin3);
    private final int[] posArr4 = genericPosArr(origin4);
    private final int[] posArr5 = genericPosArr(origin5);
    private final int[] posArr6 = genericPosArr(origin6);
    private final int[] posArr7 = genericPosArr(origin7);
    private final int[] posArr8 = genericPosArr(origin8);

    public A(byte[][] board) {
        super(board);
    }

    @Override
    protected boolean tryPut(int i, int j) {
        boolean result;
        while (true) {
            switch (currForm) {
                case 1:
                    result = putToPos(code, posArr1, i, j);
                    break;
                case 2:
                    result = putToPos(code, posArr2, i, j);
                    break;
                case 3:
                    result = putToPos(code, posArr3, i, j);
                    break;
                case 4:
                    result = putToPos(code, posArr4, i, j);
                    break;
                case 5:
                    result = putToPos(code, posArr5, i, j);
                    break;
                case 6:
                    result = putToPos(code, posArr6, i, j);
                    break;
                case 7:
                    result = putToPos(code, posArr7, i, j);
                    break;
                case 8:
                    result = putToPos(code, posArr8, i, j);
                    break;
                default:
                    currForm = 1;
                    return false;
            }
            if (result) {
                break;
            }
        }
        return true;
    }

    @Override
    protected int getCode() {
        return code;
    }


}

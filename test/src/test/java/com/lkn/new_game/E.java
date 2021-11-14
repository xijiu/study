package com.lkn.new_game;

/**
 * 镜面正反一样
 */
public class E extends Shape {

    private static final byte code = 4;


    private final int[][] origin1 = new int[][] {
            {1, 0, 0},
            {1, 1, 0},
            {0, 1, 1},
    };
    private final int[][] origin2 = new int[][] {
            {0, 0, 1},
            {0, 1, 1},
            {1, 1, 0},
    };
    private final int[][] origin3 = new int[][] {
            {1, 1, 0},
            {0, 1, 1},
            {0, 0, 1},
    };
    private final int[][] origin4 = new int[][] {
            {0, 1, 1},
            {1, 1, 0},
            {1, 0, 0},
    };

    private final int[] posArr1 = genericPosArr(origin1);
    private final int[] posArr2 = genericPosArr(origin2);
    private final int[] posArr3 = genericPosArr(origin3);
    private final int[] posArr4 = genericPosArr(origin4);

    public E(byte[][] board) {
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

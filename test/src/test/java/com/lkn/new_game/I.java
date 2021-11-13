package com.lkn.new_game;

public class I extends Shape {

    private static byte code = 8;


    private final int[][] origin1 = new int[][] {
            {0, 1, 0},
            {1, 1, 1},
            {0, 1, 0},
    };

    private final int[][] shape1 = genericArr(origin1);
    private final int[] posArr1 = genericPosArr(origin1);

    public I(byte[][] board, Shape next) {
        super(board, next);
    }

    @Override
    protected boolean tryPut(int i, int j) {
        boolean result;
        while (true) {
            switch (currForm) {
                case 1:
                    result = putToPos(code, posArr1, i, j);
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

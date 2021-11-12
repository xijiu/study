package com.lkn.game;

public class I extends Shape {

    private static byte code = 9;


    private final byte[][] origin1 = new byte[][] {
            {0, 1, 0},
            {1, 1, 1},
            {0, 1, 0},
    };

    private final byte[][] shape1 = genericArr(origin1);
    private final int[] posArr1 = genericPosArr(origin1);

    public I(int[][] board) {
        super(board);
    }

    @Override
    protected boolean tryPut() {
        boolean result;
        while (true) {
            switch (currForm) {
                case 1:
                    result = put(code, shape1[0].length, shape1.length, shape1, posArr1);
                    break;
                default:
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

package com.lkn.game;

/**
 * 镜面都是一样的
 */
public class C extends Shape {

    private static byte code = 3;

    private final byte[][] origin1 = new byte[][] {
            {1, 1, 1},
            {1, 0, 1},
    };
    private final byte[][] origin2 = new byte[][] {
            {1, 1},
            {1, 0},
            {1, 1},
    };
    private final byte[][] origin3 = new byte[][] {
            {1, 0, 1},
            {1, 1, 1},
    };
    private final byte[][] origin4 = new byte[][] {
            {1, 1},
            {0, 1},
            {1, 1},
    };


    private final byte[][] shape1 = genericArr(origin1);
    private final int[] posArr1 = genericPosArr(origin1);
    private final byte[][] shape2 = genericArr(origin2);
    private final int[] posArr2 = genericPosArr(origin2);
    private final byte[][] shape3 = genericArr(origin3);
    private final int[] posArr3 = genericPosArr(origin3);
    private final byte[][] shape4 = genericArr(origin4);
    private final int[] posArr4 = genericPosArr(origin4);

    public C(int[][] board) {
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
                case 2:
                    result = put(code, shape2[0].length, shape2.length, shape2, posArr2);
                    break;
                case 3:
                    result = put(code, shape3[0].length, shape3.length, shape3, posArr3);
                    break;
                case 4:
                    result = put(code, shape4[0].length, shape4.length, shape4, posArr4);
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

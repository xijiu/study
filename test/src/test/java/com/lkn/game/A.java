package com.lkn.game;

public class A extends Shape {

    private static byte code = 1;

    private final byte[][] origin1 = new byte[][] {
            {1, 0},
            {1, 1},
            {1, 1},
    };
    private final byte[][] origin2 = new byte[][] {
            {1, 1, 1},
            {1, 1, 0},
    };
    private final byte[][] origin3 = new byte[][] {
            {1, 1},
            {1, 1},
            {0, 1},
    };
    private final byte[][] origin4 = new byte[][] {
            {0, 1, 1},
            {1, 1, 1},
    };
    private final byte[][] origin5 = new byte[][] {
            {0, 1},
            {1, 1},
            {1, 1},
    };
    private final byte[][] origin6 = new byte[][] {
            {1, 1, 1},
            {0, 1, 1},
    };
    private final byte[][] origin7 = new byte[][] {
            {1, 1},
            {1, 1},
            {1, 0},
    };
    private final byte[][] origin8 = new byte[][] {
            {1, 1, 0},
            {1, 1, 1},
    };
    private final byte[][] shape1 = genericArr(origin1);
    private final int[] posArr1 = genericPosArr(origin1);
    private final byte[][] shape2 = genericArr(origin2);
    private final int[] posArr2 = genericPosArr(origin2);
    private final byte[][] shape3 = genericArr(origin3);
    private final int[] posArr3 = genericPosArr(origin3);
    private final byte[][] shape4 = genericArr(origin4);
    private final int[] posArr4 = genericPosArr(origin4);
    private final byte[][] shape5 = genericArr(origin5);
    private final int[] posArr5 = genericPosArr(origin5);
    private final byte[][] shape6 = genericArr(origin6);
    private final int[] posArr6 = genericPosArr(origin6);
    private final byte[][] shape7 = genericArr(origin7);
    private final int[] posArr7 = genericPosArr(origin7);
    private final byte[][] shape8 = genericArr(origin8);
    private final int[] posArr8 = genericPosArr(origin8);

    public A(byte[][] board) {
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
                case 5:
                    result = put(code, shape5[0].length, shape5.length, shape5, posArr5);
                    break;
                case 6:
                    result = put(code, shape6[0].length, shape6.length, shape6, posArr6);
                    break;
                case 7:
                    result = put(code, shape7[0].length, shape7.length, shape7, posArr7);
                    break;
                case 8:
                    result = put(code, shape8[0].length, shape8.length, shape8, posArr8);
                    break;
                default:
                    return false;
            }
            if (result) {
                break;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    protected int getCode() {
        return code;
    }


}

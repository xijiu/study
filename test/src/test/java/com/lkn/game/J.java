package com.lkn.game;

public class J extends Shape {

    private static byte code = 10;

    private static int forms = 4;

    // a
    // aa
    //  a
    //  a
    private final byte[][] shape1 = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
    };

    //  aaa
    // aa
    private final byte[][] shape2 = {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
    };

    // a
    // a
    // aa
    //  a
    private final byte[][] shape3 = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
    };

    //   aa
    // aaa
    private final byte[][] shape4 = {
            {2, 3, 4, 5, 6, 7, 8, 9, 10},
            {2, 3, 4, 5, 6, 7, 8, 9, 10},
            {2, 3, 4, 5, 6, 7, 8, 9, 10},
            {2, 3, 4, 5, 6, 7, 8, 9, 10},
    };

    @Override
    protected boolean tryPut() {
        boolean result;
        while (true) {
            switch (currForm) {
                case 1:
                    // a
                    // aa
                    //  a
                    //  a
                    result = put(code, shape1[0].length, shape1.length, shape1, 1, 0, 1, 1, 2, 1, 3, 1);
                    break;
                case 2:
                    //  aaa
                    // aa
                    result = put(code, shape2[0].length, shape2.length, shape2, 0, 1, 0, 2, 1, 0, 1, -1);
                    break;
                case 3:
                    // a
                    // a
                    // aa
                    //  a
                    result = put(code, shape3[0].length, shape3.length, shape3, 1, 0, 2, 0, 2, 1, 3, 1);
                    break;
                case 4:
                    //   aa
                    // aaa
                    result = put(code, shape4[0].length, shape4.length, shape4, 0, 1, 1, 0, 1, -1, 1, -2);
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

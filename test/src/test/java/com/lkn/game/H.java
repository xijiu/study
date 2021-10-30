package com.lkn.game;

public class H extends Shape {

    private static byte code = 8;

    private static int forms = 4;

    // a
    // a
    // a
    // aa
    private final byte[][] shape1 = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
    };

    //    a
    // aaaa
    private final byte[][] shape2 = {
            {3, 4, 5, 6, 7, 8, 9, 10, 11},
            {3, 4, 5, 6, 7, 8, 9, 10, 11},
            {3, 4, 5, 6, 7, 8, 9, 10, 11},
            {3, 4, 5, 6, 7, 8, 9, 10, 11},
    };

    // aa
    //  a
    //  a
    //  a
    private final byte[][] shape3 = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
    };

    // aaaa
    // a
    private final byte[][] shape4 = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8},
            {0, 1, 2, 3, 4, 5, 6, 7, 8},
            {0, 1, 2, 3, 4, 5, 6, 7, 8},
            {0, 1, 2, 3, 4, 5, 6, 7, 8},
    };

    @Override
    protected boolean tryPut() {
        boolean result;
        while (true) {
            switch (currForm) {
                case 1:
                    // a
                    // a
                    // a
                    // aa
                    result = put(code, shape1[0].length, shape1.length, shape1, 1, 0, 2, 0, 3, 0, 3, 1);
                    break;
                case 2:
                    //    a
                    // aaaa
                    result = put(code, shape2[0].length, shape2.length, shape2, 1, 0, 1, -1, 1, -2, 1, -3);
                    break;
                case 3:
                    // aa
                    //  a
                    //  a
                    //  a
                    result = put(code, shape3[0].length, shape3.length, shape3, 0, 1, 1, 1, 2, 1, 3, 1);
                    break;
                case 4:
                    // aaaa
                    // a
                    result = put(code, shape4[0].length, shape4.length, shape4, 0, 1, 0, 2, 0, 3, 1, 0);
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

package com.lkn.game;

public class C extends Shape {

    private static byte code = 3;

    private static int forms = 4;

    // aa
    // a
    // aa
    private final byte[][] shape1 = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
    };

    // a a
    // aaa
    private final byte[][] shape2 = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
    };

    // aa
    //  a
    // aa
    private final byte[][] shape3 = shape1;

    // aaa
    // a a
    private final byte[][] shape4 = shape2;

    @Override
    protected boolean tryPut() {
        boolean result;
        while (true) {
            switch (currForm) {
                case 1:
                    // aa
                    // a
                    // aa
                    result = put(code, shape1[0].length, shape1.length, shape1, 1, 0, 2, 0, 0, 1, 2, 1);
                    break;
                case 2:
                    // a a
                    // aaa
                    result = put(code, shape2[0].length, shape2.length, shape2, 1, 0, 1, 1, 1, 2, 0, 2);
                    break;
                case 3:
                    // aa
                    //  a
                    // aa
                    result = put(code, shape3[0].length, shape3.length, shape3, 0, 1, 1, 1, 2, 1, 2, 0);
                    break;
                case 4:
                    // aaa
                    // a a
                    result = put(code, shape4[0].length, shape4.length, shape4, 0, 1, 0, 2, 1, 0, 1, 2);
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



}

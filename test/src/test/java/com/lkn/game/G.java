package com.lkn.game;

public class G extends Shape {

    private static byte code = 7;

    private static int forms = 2;

    // a
    // a
    // a
    // a
    // a
    private final byte[][] shape1 = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
    };

    // aaaaa
    private final byte[][] shape2 = {
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 7},
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
                    // a
                    // a
                    result = put(code, shape1[0].length, shape1.length, shape1, 1, 0, 2, 0, 3, 0, 4, 0);
                    break;
                case 2:
                    // aaaaa
                    result = put(code, shape2[0].length, shape2.length, shape2, 0, 1, 0, 2, 0, 3, 0, 4);
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

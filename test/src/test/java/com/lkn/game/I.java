package com.lkn.game;

public class I extends Shape {

    private static byte code = 9;

    private static int forms = 1;

    //  a
    // aaa
    //  a
    private final byte[][] shape1 = {
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
    };

    @Override
    protected boolean tryPut() {
        boolean result;
        while (true) {
            switch (currForm) {
                case 1:
                    //  a
                    // aaa
                    //  a
                    result = put(code, shape1[0].length, shape1.length, shape1, 1, 0, 1, -1, 1, 1, 2, 0);
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

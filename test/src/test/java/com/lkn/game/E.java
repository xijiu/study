package com.lkn.game;

public class E extends Shape {

    private static byte code = 5;

    private static int forms = 4;

    // a
    // aa
    //  aa
    private final byte[][] shape1 = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
    };

    //   a
    //  aa
    // aa
    private final byte[][] shape2 = {
            {2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
            {2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
            {2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
    };

    // aa
    //  aa
    //   a
    private final byte[][] shape3 = shape1;

    //  aa
    // aa
    // a
    private final byte[][] shape4 = {
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
    };;

    @Override
    protected boolean tryPut() {
        boolean result;
        while (true) {
            switch (currForm) {
                case 1:
                    // a
                    // aa
                    //  aa
                    result = put(code, shape1[0].length, shape1.length, shape1, 1, 0, 1, 1, 2, 1, 2, 2);
                    break;
                case 2:
                    //   a
                    //  aa
                    // aa
                    result = put(code, shape2[0].length, shape2.length, shape2, 1, 0, 1, -1, 2, -1, 2, -2);
                    break;
                case 3:
                    // aa
                    //  aa
                    //   a
                    result = put(code, shape3[0].length, shape3.length, shape3, 0, 1, 1, 1, 1, 2, 2, 2);
                    break;
                case 4:
                    //  aa
                    // aa
                    // a
                    result = put(code, shape4[0].length, shape4.length, shape4, 1, 0, 1, -1, 2, -1, 0, 1);
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

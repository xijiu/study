package com.lkn.game;

public class B extends Shape {

    private static byte code = 2;

    private static int forms = 4;

    // a
    // a
    // aa
    // a
    private final byte[][] shape1 = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
    };

    //   a
    // aaaa
    private final byte[][] shape2 = {
            {2, 3, 4, 5, 6, 7, 8, 9, 10},
            {2, 3, 4, 5, 6, 7, 8, 9, 10},
            {2, 3, 4, 5, 6, 7, 8, 9, 10},
            {2, 3, 4, 5, 6, 7, 8, 9, 10},
    };

     // aaaa
     //  a
    private final byte[][] shape3 = {
             {0, 1, 2, 3, 4, 5, 6, 7, 8},
             {0, 1, 2, 3, 4, 5, 6, 7, 8},
             {0, 1, 2, 3, 4, 5, 6, 7, 8},
             {0, 1, 2, 3, 4, 5, 6, 7, 8},
    };

    //  a
    // aa
    //  a
    //  a
    private final byte[][] shape4 = {
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
    };

    @Override
    protected boolean tryPut() {
        boolean result;
        while (true) {
            switch (currForm) {
                case 1:
                    // a
                    // a
                    // aa
                    // a
                    result = put(code, shape1[0].length, shape1.length, shape1, 1, 0, 2, 0, 2, 1, 3, 0);
                    break;
                case 2:
                    //   a
                    // aaaa
                    result = put(code, shape2[0].length, shape2.length, shape2, 1, -2, 1, -1, 1, 0, 1, 1);
                    break;
                case 3:
                    // aaaa
                    //  a
                    result = put(code, shape3[0].length, shape3.length, shape3, 0, 1, 0, 2, 0, 3, 1, 1);
                    break;
                case 4:
                    //  a
                    // aa
                    //  a
                    //  a
                    result = put(code, shape4[0].length, shape4.length, shape4, 1, 0, 2, 0, 3, 0, 1, -1);
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

package com.lkn.game;


public class Game {
    public static final int steps = 12;

    public static byte[][] board = new byte[5][steps];

    private static Shape[] shapes = {
            new A(), new B(), new C(), new D(),
            new E(), new F(), new G(), new H(),
            new I(), new J(), new K(), new L()};

    private static int succeedNum = 0;

    public static void main(String[] args) {
//        printBoard();
//        A a = new A();
//        for (int i = 0; i < 100; i++) {
//            a.tryPut();
//            printBoard();
//            updateBoard(1, 0);
//        }

        long begin = System.currentTimeMillis();
        new Game().start(0);
        long cost = System.currentTimeMillis() - begin;
        System.out.println("find num is " + succeedNum);
        System.out.println("time cost " + cost);
    }

    private void start(int index) {
        while (true) {
            updateBoard(shapes[index].getCode(), 0);
            if (shapes[index].tryPut()) {
                if (index >= 8) {
                    System.out.println(index);
                    printBoard();
                }
                if (index == steps - 1) {
                    if (1 == 1) {
                        printBoard();
                        System.exit(1);
                    }
                    succeedNum++;
                    return;
                } else {
                    index++;
                }
            } else {
//                System.out.println("fail " + index);
                if (index == 0) {
                    return;
                }
                shapes[index].reset();
                index--;
            }
        }
    }


    private static void updateBoard(int origin, int update) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                if (board[i][j] == origin) {
                    board[i][j] = (byte) update;
                }
            }
        }
    }

    private static void printBoard() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < steps; j++) {
                System.out.print(codeToChar(board[i][j]) + ", ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    private static char codeToChar(byte b) {
        switch (b) {
            case 1:
                return 'a';
            case 2:
                return 'b';
            case 3:
                return 'c';
            case 4:
                return 'd';
            case 5:
                return 'e';
            case 6:
                return 'f';
            case 7:
                return 'g';
            case 8:
                return 'h';
            case 9:
                return 'i';
            case 10:
                return 'j';
            case 11:
                return 'k';
            case 12:
                return 'l';
            case 0:
                return '0';
            case -1:
                return '&';
        }
        throw new RuntimeException("error");
    }
}

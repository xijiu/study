package com.lkn.game;


public class Game {
    public static final int steps = 12;

    public static byte[][] board = new byte[5][steps];

    private static Shape[] shapes = {new A(), new B(), new C(), new D()};

    private static int succeedNum = 0;

    public static void main(String[] args) {
        printBoard();
        A a = new A();
        for (int i = 0; i < 100; i++) {
            a.tryPut();
            printBoard();
            updateBoard(1, 0);
        }


//        long begin = System.currentTimeMillis();
//        new Game().start(0);
//        long cost = System.currentTimeMillis() - begin;
//        System.out.println("find num is " + succeedNum);
//        System.out.println("time cost " + cost);
    }

    private void start(int index) {
        while (true) {
            if (shapes[index].tryPut()) {
                if (index == steps - 1) {
                    succeedNum++;
                    return;
                } else {
                    start(index + 1);
                }
            } else {
                if (index == 0) {
                    return;
                }
                shapes[index].reset();
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
                System.out.print(board[i][j] + ", ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }
}

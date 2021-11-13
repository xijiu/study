package com.lkn.new_game;


import java.util.Arrays;

public class NewGame {
    public static final int steps = 12;

    public static final int boardWidth = 5;

    public byte[][] board = new byte[boardWidth][steps];

    private final L l_ = new L(board, null);
    private final K k_ = new K(board, l_);
    private final J j_ = new J(board, k_);
    private final I i_ = new I(board, j_);
    private final H h_ = new H(board, i_);
    private final G g_ = new G(board, h_);
    private final F f_ = new F(board, g_);
    private final E e_ = new E(board, f_);
    private final D d_ = new D(board, e_);
    private final C c_ = new C(board, d_);
    private final B b_ = new B(board, c_);
    private final A a_ = new A(board, b_);

    private final Shape[] shapes = {a_, b_, c_, d_, e_, f_, g_, h_, i_, j_, k_, l_};

    private static int findTimes = 0;

    public static void main(String[] args) throws Exception {
        long beginTime = System.currentTimeMillis();
        new NewGame().begin();
        long cost = System.currentTimeMillis() - beginTime;
        System.out.println("find times is " + findTimes);
        System.out.println("total cost time is " + cost);
    }

    private void begin() {
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < steps; j++) {
                board[i][j] = -1;
            }
        }
        boolean[] putFlagArr = new boolean[steps];
        Arrays.fill(putFlagArr, false);

        int[] stack = new int[steps];
        Arrays.fill(stack, -1);
        stack[0] = 0;
        int stackIndex = 0;

        while (true) {
            int shapeIndex = stack[stackIndex];
            Shape shape = shapes[shapeIndex];
            findEmptyPos();
            boolean result = shape.tryPut(emptyPos.i, emptyPos.j);
            if (result) {
                if (stackIndex == 11) {
                    // 找到一个答案，把答案print出来
                    findTimes++;
                    printBoard();

                    updateBoard(stack[stackIndex--], -1);
                    shape.currForm = 1;
                    updateBoard(stack[stackIndex], -1);
                } else {
                    int index = findNextShape(-1, stack, stackIndex);
                    if (index != 100) {
                        stack[++stackIndex] = index;
                    }
                }
            } else {
                int index = findNextShape(shape.getCode(), stack, stackIndex);
                if (index != 100) {
                    stack[stackIndex] = index;
                } else {
                    stackIndex--;
                    if (stackIndex < 0) {
                        // 结束了
                        System.out.println("over3");
                        return;
                    }
                    updateBoard(stack[stackIndex], -1);
                }

            }

        }
    }

    private int findNextShape(int beginIndex, int[] stack, int stackIndex) {
        for (int i = beginIndex + 1; i < 12; i++) {
            boolean contains = false;
            for (int j = 0; j <= stackIndex; j++) {
                if (stack[j] == i) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                return i;
            }
        }
        return 100;
    }

    private Pos emptyPos = new Pos();

    private void findEmptyPos() {
        for (int j = 0; j < steps; j++) {
            for (int i = 0; i < boardWidth; i++) {
                if (board[i][j] == -1) {
                    emptyPos.i = i;
                    emptyPos.j = j;
                    return;
                }
            }
        }
    }


    private void updateBoard(int origin, int update) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                if (board[i][j] == origin) {
                    board[i][j] = (byte) update;
                }
            }
        }
    }

    private void printBoard() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < steps; j++) {
                System.out.print(codeToChar(board[i][j]) + ", ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    private static char codeToChar(int b) {
        switch (b) {
            case 0:
                return 'a';
            case 1:
                return 'b';
            case 2:
                return 'c';
            case 3:
                return 'd';
            case 4:
                return 'e';
            case 5:
                return 'f';
            case 6:
                return 'g';
            case 7:
                return 'h';
            case 8:
                return 'i';
            case 9:
                return 'j';
            case 10:
                return 'k';
            case 11:
                return 'l';
            case -1:
                return '-';
        }
        throw new RuntimeException("error");
    }
}

package com.lkn.game;


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NewGame {
    public static final int steps = 12;

    public static final int boardWidth = 5;

    public static final long beginTime = System.currentTimeMillis();

    private static AtomicLong tryPutTotalTimes = new AtomicLong();

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

    private static AtomicInteger succeedNum = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        new NewGame().begin();
    }

    private void begin() {
        boolean[] putFlagArr = new boolean[steps];
        Arrays.fill(putFlagArr, false);
        int putIndex = 0;

        int[] stack = new int[steps];
        Arrays.fill(stack, -1);
        int stackIndex = 0;
        stack[0] = 0;

        while (true) {
            int shapeIndex = stack[stackIndex];
            Shape shape = shapes[shapeIndex];
            findEmptyPos();

        }
    }

    private Pos emptyPos = new Pos();

    private void findEmptyPos() {
        for (int j = 0; j < steps; j++) {
            for (int i = 0; i < boardWidth; i++) {
                if (board[i][j] == 0) {
                    emptyPos.i = i;
                    emptyPos.j = j;
                    break;
                }
            }
        }
    }


    int tryPutTimes = 0;

    private void start(int index) {
        while (true) {
            updateBoard(shapes[index].getCode(), 0);
            tryPutTimes++;
            if (shapes[index].tryPut()) {
                if (!checkBoardValid()) {
                    continue;
                }
//                System.out.println("succeed " + index);
                if (index >= 11) {
                    System.out.println(index);
                    printBoard();
                }
                if (index == steps - 1) {
                    int num = succeedNum.incrementAndGet();
                    System.out.println("find num is " + num + ", time cost " + (System.currentTimeMillis() - beginTime));
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

    /**
     * 检查棋盘是否有效，用于剪枝操作
     */
    private boolean checkBoardValid() {
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < steps; j++) {
                if (board[i][j] == 0) {
                    int activeNum = queryActiveNum(i, j);
                    if (activeNum % 5 != 0 || activeNum == 0) {
                        updateBoard(-2, 0);
                        return false;
                    }
                }
            }
        }
        updateBoard(-2, 0);
        return true;
    }

    private int[] queue = new int[60];
    private int headIndex = 0;
    private int tailIndex = 0;
//    private Set<Integer> set = new HashSet<>();
    private boolean[] set = new boolean[60];

    private int queryActiveNum(int i, int j) {
        headIndex = 0;
        tailIndex = 0;
        Arrays.fill(set, false);
        int num = 0;
        queue[tailIndex++] = i * 12 + j;
        while (tailIndex - headIndex > 0) {
            int val = queue[headIndex++];
            i = val / 12;
            j = val % 12;
            if (j + 1 < steps && board[i][j + 1] == 0) {
                int tmp = i * 12 + j + 1;
                if (!set[tmp]) {
                    set[tmp] = true;
                    queue[tailIndex++] = tmp;
                    num++;
                }
            }
            if (j - 1 >= 0 && board[i][j - 1] == 0) {
                int tmp = i * 12 + j - 1;
                if (!set[tmp]) {
                    set[tmp] = true;
                    queue[tailIndex++] = tmp;
                    num++;
                }
            }
            if (i + 1 < boardWidth && board[i + 1][j] == 0) {
                int tmp = (i + 1) * 12 + j;
                if (!set[tmp]) {
                    set[tmp] = true;
                    queue[tailIndex++] = tmp;
                    num++;
                }
            }
            if (i - 1 >= 0 && board[i - 1][j] == 0) {
                int tmp = (i - 1) * 12 + j;
                if (!set[tmp]) {
                    set[tmp] = true;
                    queue[tailIndex++] = tmp;
                    num++;
                }
            }
        }
        for (int k = 0; k < tailIndex; k++) {
            int val = queue[k];
            i = val / 12;
            j = val % 12;
            board[i][j] = -2;
        }
        return num;
    }

//    private int queryActiveNum(int i, int j) {
//        int num = 0;
//        int col1 = j;
//        int col2 = j;
//        for (int k = j; k < 12; k++) {
//            if (board[i][k] == 0) {
//                board[i][k] = -2;
//                col2 = k;
//                num++;
//            } else {
//                break;
//            }
//        }
//        if (++i >= boardWidth) {
//            return num;
//        }
//        boolean goOn = false;
//        for (int k = col1; k <= col2; k++) {
//            if (board[i][k] == 0) {
//                board[i][k] = -2;
//                num++;
//                goOn = true;
//            }
//        }
//        for (int k = col1; k >= 0; k--) {
//            if (board[i][k] == 0) {
//                board[i][k] = -2;
//                col1 = k;
//                num++;
//            } else {
//                break;
//            }
//        }
//
//
//
//        headIndex = 0;
//        tailIndex = 0;
//        Arrays.fill(set, false);
//        int num = 0;
//        queue[tailIndex++] = i * 12 + j;
//        while (tailIndex - headIndex > 0) {
//            int val = queue[headIndex++];
//            i = val / 12;
//            j = val % 12;
//            if (j + 1 < steps && board[i][j + 1] == 0) {
//                int tmp = i * 12 + j + 1;
//                if (!set[tmp]) {
//                    set[tmp] = true;
//                    queue[tailIndex++] = tmp;
//                    num++;
//                }
//            }
//            if (j - 1 >= 0 && board[i][j - 1] == 0) {
//                int tmp = i * 12 + j - 1;
//                if (!set[tmp]) {
//                    set[tmp] = true;
//                    queue[tailIndex++] = tmp;
//                    num++;
//                }
//            }
//            if (i + 1 < boardWidth && board[i + 1][j] == 0) {
//                int tmp = (i + 1) * 12 + j;
//                if (!set[tmp]) {
//                    set[tmp] = true;
//                    queue[tailIndex++] = tmp;
//                    num++;
//                }
//            }
//            if (i - 1 >= 0 && board[i - 1][j] == 0) {
//                int tmp = (i - 1) * 12 + j;
//                if (!set[tmp]) {
//                    set[tmp] = true;
//                    queue[tailIndex++] = tmp;
//                    num++;
//                }
//            }
//        }
//        for (int k = 0; k < tailIndex; k++) {
//            int val = queue[k];
//            i = val / 12;
//            j = val % 12;
//            board[i][j] = -2;
//        }
//        return num;
//    }


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

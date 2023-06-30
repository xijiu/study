package com.lkn.chess;

import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;
import com.lkn.chess.bean.chess_piece.Cannons;
import com.lkn.chess.bean.chess_piece.Elephants;
import com.lkn.chess.bean.chess_piece.Horse;
import com.lkn.chess.bean.chess_piece.King;
import com.lkn.chess.bean.chess_piece.Mandarins;
import com.lkn.chess.bean.chess_piece.Pawns;
import com.lkn.chess.bean.chess_piece.Rooks;
import com.lkn.chess.manual.BytesKey;
import com.lkn.chess.manual.Manual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 默认电脑是黑方
 *
 * @author xijiu
 * @since 2023/5/4 上午8:52
 */
public class GamePlayHigh {

    private static int COUNT = 0;
    private static LinkedHashMap<Integer, Integer> posMap = new LinkedHashMap<>();
    /** 最佳路径 key 为 level */
    private static Map<Integer, Integer> bestRouteMap = new HashMap<>();
    private static Map<Integer, Integer> exchangeTableMap = new HashMap<>();
    private static int TMP_THINK_DEPTH = 0;
    private static int EXTEND_THINK_DEPTH = 0;

    public int[] computerWalk(ChessBoard chessBoard) {
        long begin = System.currentTimeMillis();
        adjustThinkDepthIfNecessary(chessBoard);
        COUNT = 0;
        bestRouteMap.clear();

        Integer from;
        Integer to;

        Integer multiPos = Manual.readManual(chessBoard.snapshot());
        if (multiPos != -1) {
            System.out.println("hit manual");
            from = PubTools.uncompressBegin(multiPos);
            to = PubTools.uncompressEnd(multiPos);
        } else {
            maxLevel = -1;
            for (int i = 2; i <= Conf.THINK_DEPTH; i++) {
                TMP_THINK_DEPTH = i;
                posMap.clear();
                exchangeTableMap.clear();
                think(chessBoard, Role.BLACK, 1, Integer.MAX_VALUE);
            }
            System.out.println("maxLevel maxLevel maxLevel is " + maxLevel);

//            System.out.println("----------begin-------");
//            System.out.println(sb);
//            System.out.println("----------end-------");
//
//            System.out.println("----------begin1-------");
//            System.out.println(sb1);
//            System.out.println("----------end1-------");
//
//            System.out.println("----------begin2-------");
//            System.out.println(sb2);
//            System.out.println("----------end2-------");


            List<Integer> list = new ArrayList<>(posMap.keySet());
            Collections.shuffle(list);
            from = list.get(0);
            to = posMap.get(from);
        }

        System.out.println("SOURCE_POS is " + from);
        System.out.println("TARGET_POS is " + to);
        chessBoard.walk(from, to);
        System.out.println("考虑情况 " + COUNT + ", time cost " + (System.currentTimeMillis() - begin));

        chessBoard.boardPrintToConsole();
        return new int[]{from, to};
    }

    /**
     * 调整思考深度
     * 如果当前棋盘的子减少了，那么需要适当提高搜索树的深度
     */
    private void adjustThinkDepthIfNecessary(ChessBoard chessBoard) {
        AbstractChessPiece[][] allPiece = chessBoard.getAllPiece();
        int pieceNum = 0;
        for (AbstractChessPiece[] arr : allPiece) {
            for (AbstractChessPiece piece : arr) {
                if (piece != null) {
                    pieceNum++;
                }
            }
        }
        if (pieceNum >= 20) {
            return;
        }
        if (pieceNum >= 8) {
            Conf.THINK_DEPTH = Conf.DIFFICULTY_LEVEL.thinkDepth + 1;
        } else {
            Conf.THINK_DEPTH = Conf.DIFFICULTY_LEVEL.thinkDepth + 2;
        }
    }

    private void printBestPath() {
        if (bestRouteMap.size() == 0) {
            return;
        }
        System.out.println("最佳路径： ");
        for (int i = 1; i <= Conf.THINK_DEPTH; i++) {
            Integer pos = bestRouteMap.get(i);
            int begin = PubTools.uncompressBegin(pos);
            int end = PubTools.uncompressEnd(pos);
            System.out.println("level " + i + ", begin " + begin + ", end " + end);
        }
    }

    private static boolean valid = false;
    private static boolean valid1 = false;
    private static boolean valid2 = false;
    private static StringBuilder sb = new StringBuilder();
    private static StringBuilder sb1 = new StringBuilder();
    private static StringBuilder sb2 = new StringBuilder();
    private static int maxLevel = -1;
    private static BytesKey bytesKey = new BytesKey(null);

    private int think(ChessBoard chessBoard, Role role, int level, int parentVal) {
        int finalVal = role == Role.RED ? Integer.MAX_VALUE : Integer.MIN_VALUE;
//        int finalVal = role == Role.RED ? parentVal : parentVal;
        AbstractChessPiece[][] pieceArr = chessBoard.getAllPiece();
        byte[] singlePieceArr = role == Role.RED ? chessBoard.getRedPieceArr() : chessBoard.getBlackPieceArr();
        int firstPos = sortPieceMap(pieceArr, level, role);

        for (int k = -1; k < singlePieceArr.length; k++) {
            int sourcePos = k == -1 ? firstPos : singlePieceArr[k];
            if (sourcePos == -1) {
                continue;
            }

            int x = ChessTools.fetchX(sourcePos);
            int y = ChessTools.fetchY(sourcePos);
            AbstractChessPiece piece = pieceArr[x][y];

            if (piece == null) {
                continue;
            }
            if (firstPos == sourcePos && k != -1) {
                continue;
            }

            byte[] reachablePositions = piece.getReachablePositions(sourcePos, chessBoard, false, level);
            // 只保留吃的场景
//            retainEatCaseIfNecessary(chessBoard, reachablePositions, level);
            topEatCase(reachablePositions, pieceArr);
            exchangeBestPositionToFirst(reachablePositions, level);
            byte size = reachablePositions[0];
            if (size == 0) {
                COUNT++;
                continue;
            } else {
                for (int i = 1; i <= size; i++) {
                    COUNT++;
                    byte targetPos = reachablePositions[i];
//                    if (level == 1 && sourcePos == 78 && targetPos == 86) {
//                        valid = true;
//                    }
//                    if (valid && level == 2 && sourcePos == 65 && targetPos == 86) {
//                        valid1 = true;
//                    }
//                    if (valid && valid1 && level == 3 && sourcePos == 98 && targetPos == 97) {
//                        valid2 = true;
//                    }
                    AbstractChessPiece eatenPiece = chessBoard.walk(sourcePos, targetPos);
                    boolean isKingEaten = isKingEaten(eatenPiece);
                    Role nextRole = role.nextRole();
                    int newLevel = level + 1;
                    maxLevel = Math.max(maxLevel, newLevel);
                    int value;
                    if (!isKingEaten) {
                        if (TMP_THINK_DEPTH < Conf.THINK_DEPTH && level == TMP_THINK_DEPTH) {
                            value = computeChessValue(chessBoard, role.nextRole());
                        } else {
                            boolean isAdjustDepth = false;
                            if (level == TMP_THINK_DEPTH - 1) {
                                if (piece.kingCheck(chessBoard, targetPos) && EXTEND_THINK_DEPTH == 0) {
                                    isAdjustDepth = true;
                                    EXTEND_THINK_DEPTH = adjustExtendThinkPath(level);
                                }
                                value = think(chessBoard, nextRole, newLevel, finalVal);
                            } else {
                                if (level == TMP_THINK_DEPTH && piece.kingCheck(chessBoard, targetPos) && EXTEND_THINK_DEPTH == 0) {
                                    isAdjustDepth = true;
                                    EXTEND_THINK_DEPTH = adjustExtendThinkPath(level);
                                }
                                if (level - TMP_THINK_DEPTH < EXTEND_THINK_DEPTH) {
                                    value = think(chessBoard, nextRole, newLevel, finalVal);
                                } else {
                                    value = computeChessValue(chessBoard, role.nextRole());
                                }

                                // value = quietSearch(chessBoard, nextRole, finalVal, newLevel);
                            }
                            if (isAdjustDepth) {
                                EXTEND_THINK_DEPTH = 0;
                            }
                        }
                    } else {
                        value = role == Role.RED ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                    }

                    if (role == Role.RED) {
                        if (value < finalVal) {
                            bestRouteMap.put(level, PubTools.compress(sourcePos, targetPos));
                        }
                        finalVal = Math.min(value, finalVal);
                    } else {
                        if (level == 1 && TMP_THINK_DEPTH == Conf.THINK_DEPTH) {
                            if (value > finalVal) {
                                posMap.clear();
                                posMap.put(sourcePos, (int) targetPos);
                                System.out.println("最新最好行棋： " + piece.getName() + " -- " + value + ", " + sourcePos + ":" + targetPos + ",  before val " + finalVal + ", after val " + value);
                            } else if (value == finalVal) {
                                posMap.put(sourcePos, (int) targetPos);
                            }
                        }
                        if (value > finalVal) {
                            bestRouteMap.put(level, PubTools.compress(sourcePos, targetPos));
                        }
                        finalVal = Math.max(value, finalVal);
                    }

//                    if (valid && level == 2) {
//                        sb.append("LEVEL is " + level + ",   " + piece.getName() + " ::: " + value + ", " + sourcePos + "  :::  " + targetPos).append("\n");
//                    }
//
//                    if (valid1 && level == 3) {
//                        sb1.append("LEVEL is " + level + ",   " + piece.getName() + " ::: " + value + ", " + sourcePos + "  :::  " + targetPos).append("\n");
//                    }
//
//                    if (valid2 && level == 4) {
//                        sb2.append("LEVEL is " + level + ",   " + piece.getName() + " ::: " + value + ", " + sourcePos + "  :::  " + targetPos).append("\n");
//                    }

                    chessBoard.unWalk(sourcePos, targetPos, eatenPiece);
                    boolean pruning = needPruning(role, parentVal, value);
//                    if (level == 1 && sourcePos == 78 && targetPos == 86) {
//                        valid = false;
//                    }
//                    if (valid && level == 2 && sourcePos == 65 && targetPos == 86) {
//                        valid1 = false;
//                    }
//                    if (valid1 && level == 3 && sourcePos == 98 && targetPos == 97) {
//                        valid2 = false;
//                    }
                    if (pruning) {
                        return role == Role.RED ? Conf.GAME_PLAY_MIN_VAL : Conf.GAME_PLAY_MAX_VAL;
                    }
                }
            }
        }
        return finalVal;
    }

    private int adjustExtendThinkPath(int level) {
//        if (level == TMP_THINK_DEPTH - 1) {
//            switch (Conf.DIFFICULTY_LEVEL) {
//                case EASY:
//                    return 2;
//                case MID:
//                    return 2;
//                case HARD:
//                    return 1;
//                default:
//                    return 0;
//            }
//        }
        if (level == TMP_THINK_DEPTH) {
            switch (Conf.DIFFICULTY_LEVEL) {
                case EASY:
                    return 3;
                case MID:
                    return 2;
                case HARD:
                    return 2;
                default:
                    return 0;
            }
        }

        return 0;
    }


    private int quietSearch(ChessBoard chessBoard, Role role, int parentVal, int level) {
        if (TMP_THINK_DEPTH < Conf.THINK_DEPTH) {
            return computeChessValue(chessBoard, role.nextRole());
        }
        int finalVal = role == Role.RED ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        AbstractChessPiece[][] pieceArr = chessBoard.getAllPiece();
        byte[] singlePieceArr = role == Role.RED ? chessBoard.getRedPieceArr() : chessBoard.getBlackPieceArr();

        for (int k = 0; k < singlePieceArr.length; k++) {
            int sourcePos = singlePieceArr[k];
            if (sourcePos == -1) {
                continue;
            }
            int x = ChessTools.fetchX(sourcePos);
            int y = ChessTools.fetchY(sourcePos);
            AbstractChessPiece piece = pieceArr[x][y];
            if (piece == null) {
                continue;
            }

            byte[] reachablePositions = piece.getReachablePositions(sourcePos, chessBoard, false, level);
            // 只保留吃的场景
            retainEatCaseIfNecessary(chessBoard, reachablePositions, level);
            byte size = reachablePositions[0];
            if (size == 0) {
                COUNT++;
                finalVal = computeChessValue(chessBoard, role.nextRole());
            } else {
                for (int i = 1; i <= size; i++) {
                    COUNT++;
                    byte targetPos = reachablePositions[i];
                    AbstractChessPiece eatenPiece = chessBoard.walk(sourcePos, targetPos);
                    boolean isKingEaten = isKingEaten(eatenPiece);
                    int value;
                    if (!isKingEaten) {
                        Role nextRole = role.nextRole();
                        int newLevel = level + 1;
                        maxLevel = Math.max(maxLevel, newLevel);
                        value = computeChessValue(chessBoard, role.nextRole());
                        boolean pruning = needPruning(role, parentVal, value);
                        if (pruning) {
                            chessBoard.unWalk(sourcePos, targetPos, eatenPiece);
                            return role == Role.RED ? Conf.GAME_PLAY_MIN_VAL : Conf.GAME_PLAY_MAX_VAL;
                        }
                        value = quietSearch(chessBoard, nextRole, finalVal, newLevel);
                    } else {
                        value = role == Role.RED ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                    }

                    if (role == Role.RED) {
                        finalVal = Math.min(value, finalVal);
                    } else {
                        finalVal = Math.max(value, finalVal);
                    }

                    chessBoard.unWalk(sourcePos, targetPos, eatenPiece);
                    boolean pruning = needPruning(role, parentVal, value);
                    if (pruning) {
                        return role == Role.RED ? Conf.GAME_PLAY_MIN_VAL : Conf.GAME_PLAY_MAX_VAL;
                    }
                }
            }
        }
        return finalVal;
    }

    private void topEatCase(byte[] reachablePositions, AbstractChessPiece[][] pieceArr) {
        byte size = reachablePositions[0];
        int eatIndex = 1;
        for (int i = 1; i <= size; i++) {
            byte reachablePosition = reachablePositions[i];
            AbstractChessPiece piece = ChessTools.getPiece(pieceArr, reachablePosition);
            if (piece != null) {
                byte tmp = reachablePositions[eatIndex];
                reachablePositions[eatIndex] = reachablePosition;
                reachablePositions[i] = tmp;
                eatIndex++;
            }
        }
    }

    private void retainEatCaseIfNecessary(ChessBoard chessBoard, byte[] reachablePositions, int level) {
        if (level <= TMP_THINK_DEPTH) {
            return;
        }
        int index = 1;
        AbstractChessPiece[][] allPiece = chessBoard.getAllPiece();
        byte len = reachablePositions[0];
        for (int i = 1; i <= len; i++) {
            AbstractChessPiece piece = ChessTools.getPiece(allPiece, reachablePositions[i]);
            if (piece != null) {
                byte type = piece.type();
                if (type == 6 || type == 13 || type == 2 || type == 9 || type == 1 || type == 8) {
                    reachablePositions[index++] = reachablePositions[i];
                }
            }
        }
        reachablePositions[0] = (byte) (index - 1);
    }

    private boolean isKingEaten(AbstractChessPiece eatenPiece) {
        if (eatenPiece == null) {
            return false;
        }
        byte type = eatenPiece.type();
        return type == 5 || type == 12;
    }

    private void exchangeBestPositionToFirst(byte[] reachablePositions, int level) {
//        if (1 == 1) {
//            return;
//        }
//        if (level > TMP_THINK_DEPTH) {
//            return;
//        }
        Integer position = bestRouteMap.get(level);
        if (position == null) {
            return;
        }
        int endPos = PubTools.uncompressEnd(position);
        byte reachablePosition = reachablePositions[0];
        for (int i = 1; i <= reachablePosition; i++) {
            if (reachablePositions[i] == endPos) {
                byte tmp = reachablePositions[1];
                reachablePositions[1] = reachablePositions[i];
                reachablePositions[i] = tmp;
                break;
            }
        }
    }

    private int sortPieceMap(AbstractChessPiece[][] pieceArr, int level, Role role) {
//        if (1 == 1) {
//            return new HashMap<>(pieceMap);
//        }
        Integer position = bestRouteMap.get(level);
        if (position == null) {
            return -1;
        }
        int beginPos = PubTools.uncompressBegin(position);
        AbstractChessPiece piece = ChessTools.getPiece(pieceArr, beginPos);
        if (piece != null) {
            if (piece.getPLAYER_ROLE() == role) {
                return beginPos;
            }
        }
        return -1;
    }

    /**
     * 判断是否需要剪枝
     */
    private boolean needPruning(Role role, int parentVal, int currVal) {
//        if (1 == 1) {
//            return false;
//        }
        if (role == Role.RED) {
            if (currVal < parentVal) {
                return true;
            }
        } else {
            if (currVal > parentVal) {
                return true;
            }
        }
        return false;
    }

    /**
     * 评估棋盘的价值
     *
     * @param chessBoard 棋盘
     * @param role
     * @return  价值
     */
    public int computeChessValue(ChessBoard chessBoard, Role role) {
        int hashCode = chessBoard.toHashCode();
        Integer val = exchangeTableMap.get(hashCode);
        if (val != null) {
            return val;
        }
        chessBoard.genericNextStepPositionMap();
        int totalValue = 0;
        AbstractChessPiece[][] allPiece = chessBoard.getAllPiece();
        for (int x = 0; x < allPiece.length; x++) {
            for (int y = 0; y < allPiece[x].length; y++) {
                int position = ChessTools.toPosition(x, y);
                AbstractChessPiece piece = allPiece[x][y];
                if (piece == null) {
                    continue;
                }
                int value = piece.valuation(chessBoard, position, role);
                if (piece.isRed()) {
                    totalValue -= value;
                } else {
                    totalValue += value;
                }
            }
        }
        exchangeTableMap.put(hashCode, totalValue);
        return totalValue;
    }


    public static void main(String[] args) {
        byte[] reachablePositions = {8, 3, 4, 7, 1, 5};
        sortEatPiece(reachablePositions, 6);
        System.out.println(Arrays.toString(reachablePositions));
    }


    private static byte[] RED_SORT_ARR = {King.RED_TYPE, Rooks.RED_TYPE, Cannons.RED_TYPE, Horse.RED_TYPE, Mandarins.RED_TYPE, Elephants.RED_TYPE, Pawns.RED_TYPE};
    private static byte[] BLACK_SORT_ARR = {King.BLACK_TYPE, Rooks.BLACK_TYPE, Cannons.BLACK_TYPE, Horse.BLACK_TYPE, Mandarins.BLACK_TYPE, Elephants.BLACK_TYPE, Pawns.BLACK_TYPE};

    private static void sortEatPiece(byte[] reachablePositions, int eatIndex) {
        if (eatIndex <= 1) {
            return;
        }

        byte[] arr;
        if (reachablePositions[1] <= 7) {
            arr = RED_SORT_ARR;
        } else {
            arr = BLACK_SORT_ARR;
        }

        int index = 1;
        for (byte redType : arr) {
            for (int i = index; i < eatIndex; i++) {
                if (reachablePositions[i] == redType) {
                    byte tmp = reachablePositions[index];
                    reachablePositions[index] = reachablePositions[i];
                    reachablePositions[i] = tmp;
                    index++;
                }
            }
        }
    }
}

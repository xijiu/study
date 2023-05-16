package com.lkn.chess;

import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;
import com.lkn.chess.manual.Manual;

import java.util.ArrayList;
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

    private static int SOURCE_POS = 0;
    private static int TARGET_POS = 0;
    private static int COUNT = 0;
    private static LinkedHashMap<Integer, Integer> posMap = new LinkedHashMap<>();
    /** 最佳路径 key 为 level */
    private static Map<Integer, Integer> bestRouteMap = new HashMap<>();
    private static int TMP_THINK_DEPTH = 0;

    public void computerWalk(ChessBoard chessBoard) {
        long begin = System.currentTimeMillis();
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
            for (int i = 1; i <= Conf.THINK_DEPTH; i++) {
                TMP_THINK_DEPTH = i;
                posMap.clear();
                think(chessBoard, Role.BLACK, 1, Integer.MAX_VALUE);
            }

            List<Integer> list = new ArrayList<>(posMap.keySet());
            Collections.shuffle(list);
            from = list.get(0);
            to = posMap.get(from);
        }


        System.out.println("SOURCE_POS is " + from);
        System.out.println("TARGET_POS is " + to);
        chessBoard.walk(from, to);
        System.out.println("考虑情况 " + COUNT + ", time cost " + (System.currentTimeMillis() - begin));
    }

    private int think(ChessBoard chessBoard, Role role, int level, int parentVal) {
        int finalVal = role == Role.RED ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        Map<Integer, AbstractChessPiece> pieceMap = role == Role.RED ? chessBoard.getRedPiece() : chessBoard.getBlackPiece();
        Map<Integer, AbstractChessPiece> sortMap = sortPieceMap(pieceMap, level);
        for (Map.Entry<Integer, AbstractChessPiece> entry : sortMap.entrySet()) {
            Integer sourcePos = entry.getKey();
            AbstractChessPiece piece = entry.getValue();
            byte[] reachablePositions = piece.getReachablePositions(sourcePos, chessBoard);
            exchangeBestPositionToFirst(reachablePositions, level);
            byte size = reachablePositions[0];
            for (int i = 1; i <= size; i++) {
                COUNT++;
//                if (COUNT % 1000000 == 0) {
//                    System.out.println(COUNT);
//                }
                byte targetPos = reachablePositions[i];
                AbstractChessPiece eatenPiece = chessBoard.walk(sourcePos, targetPos);
                boolean isKingEaten = isKingEaten(eatenPiece);
                Role nextRole = role.nextRole();
                int newLevel = level + 1;
                int value;
                if (newLevel <= TMP_THINK_DEPTH && !isKingEaten) {
                    value = think(chessBoard, nextRole, newLevel, finalVal);
                } else {
                    if (isKingEaten) {
                        value = role == Role.RED ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                    } else {
                        value = computeChessValue(chessBoard);
                    }
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
                        } else if (value == finalVal) {
                            posMap.put(sourcePos, (int) targetPos);
                        }
                        System.out.println(piece.getName() + " ::: " + value);
                    }
                    if (value > finalVal) {
                        bestRouteMap.put(level, PubTools.compress(sourcePos, targetPos));
                    }
                    finalVal = Math.max(value, finalVal);
                }
                chessBoard.unWalk(sourcePos, targetPos, eatenPiece);
                boolean pruning = needPruning(role, parentVal, value);
                if (pruning) {
                    return role == Role.RED ? Conf.GAME_PLAY_MIN_VAL : Conf.GAME_PLAY_MAX_VAL;
                }
            }
            ArrPool.giveBack(reachablePositions);
        }
        return finalVal;
    }

    private boolean isKingEaten(AbstractChessPiece eatenPiece) {
        if (eatenPiece == null) {
            return false;
        }
        String name = eatenPiece.getName();
        return name.equals("帅") || name.equals("将");
    }

    private void exchangeBestPositionToFirst(byte[] reachablePositions, int level) {
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

    private Map<Integer, AbstractChessPiece> sortPieceMap(Map<Integer, AbstractChessPiece> pieceMap, int level) {
        Integer position = bestRouteMap.get(level);
        if (position == null) {
            return new HashMap<>(pieceMap);
        }
        Map<Integer, AbstractChessPiece> sortMap = new LinkedHashMap<>();
        int beginPos = PubTools.uncompressBegin(position);
        AbstractChessPiece piece = pieceMap.get(beginPos);
        if (piece != null) {
            sortMap.put(beginPos, piece);
            for (Map.Entry<Integer, AbstractChessPiece> entry : pieceMap.entrySet()) {
                if (entry.getKey() != beginPos) {
                    sortMap.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            sortMap.putAll(pieceMap);
        }
        return sortMap;
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
        }
        if (role == Role.BLACK) {
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
     * @return  价值
     */
    private int computeChessValue(ChessBoard chessBoard) {
        int totalValue = 0;
        Map<Integer, AbstractChessPiece> totalPieceMap = chessBoard.getAllPiece();
        for (Map.Entry<Integer, AbstractChessPiece> entry : totalPieceMap.entrySet()) {
            Integer position = entry.getKey();
            AbstractChessPiece piece = entry.getValue();
            int value = piece.valuation(chessBoard, position);
            if (piece.isRed()) {
                totalValue -= value;
            } else {
                totalValue += value;
            }
        }
        return totalValue;
    }


}

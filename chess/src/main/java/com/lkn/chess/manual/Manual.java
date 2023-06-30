package com.lkn.chess.manual;

import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;
import com.lkn.chess.bean.chess_piece.AbstractChessPiece;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * 棋谱结构
 * 棋子类型总共有14种，红方7种，黑方7种
 *
 * 红方
 * 车    1
 * 马    2
 * 象    3
 * 士    4
 * 帅    5
 * 炮    6
 * 兵    7
 *
 * 黑方
 * 车    8
 * 马    9
 * 象    10
 * 士    11
 * 将    12
 * 炮    13
 * 兵    14
 *
 * 一张棋盘上总共有90个点，因此一个棋盘快照的bit协议如下：
 *
 *
 * 总共有14种棋子，用4个bit来标记一个棋子类型，棋盘最多有32个棋子，因此占用空间32*4=128 bit
 *
 * [1-90]   :  用来标记每个点是否有棋子
 * [91-218] :  每个点的具体类型
 *
 * [1-----------------------218]       [8bit]      [begin,end] [begin,end] ...
 *            棋盘快照                 谱招num,8bit           8bit,8bit
 *
 * @author xijiu
 * @since 2023/5/6 上午8:55
 */
public class Manual {

    /** 是否启用棋谱 */
    private static final boolean TAKE_EFFECT = false;

    private static File file = new File("/Users/likangning/study/study/chess/src/main/resources/manual.chess");

    private static File sourceFile = new File("/Users/likangning/study/study/chess/src/main/resources/char.source");

    private static Map<BytesKey, Set<Integer>> map = new HashMap<>();

    private static boolean LOAD_FLAG = false;

    public static ByteBuffer byteBuffer = null;

    public static Integer readManual(byte[] snapshot) {
        if (!TAKE_EFFECT) {
            return -1;
        }
        try {
            loadManualToMemory();
            Set<Integer> set = map.get(new BytesKey(snapshot));
            if (set != null && set.size() > 0) {
                int random = new Random().nextInt(set.size());
                int i = 0;
                for (Integer num : set) {
                    if (i == random) {
                        return num;
                    } else {
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    private static void loadManualToMemory() throws Exception {
        if (LOAD_FLAG) {
            return;
        }
//        ByteBuffer byteBuffer = ByteBuffer.allocate(10 * 1024 * 1024);
//        FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.READ);
//        fileChannel.read(byteBuffer);
//        fileChannel.close();
//        byteBuffer.flip();

        byteBuffer.flip();

        int snapshotNum = 0;
        while (byteBuffer.hasRemaining()) {
            byte[] bytes = new byte[28];
            byteBuffer.get(bytes);
            byte num = byteBuffer.get();
            Set<Integer> targetSet = new HashSet<>();
            for (int i = 0; i < num; i++) {
                targetSet.add(byteBuffer.getInt());
            }
            BytesKey bytesKey = new BytesKey(bytes);
            map.put(bytesKey, targetSet);
            snapshotNum++;
        }
        System.out.println("收录快照： " + snapshotNum);
        LOAD_FLAG = true;
    }

    private static void readCharManual(String str) {
        if (str.length() <= 0) {
            return;
        }
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.init();
        Role role = Role.RED;
        // 将多个空格替换为1个空格
        str = str.replaceAll(" +", " ");
        str = str.replaceAll("m", "");
        String[] split = str.split("\n");
        for (String line : split) {
            line = line.trim();
            String[] s = line.split(" ");
            for (String single : s) {
                if (single.length() == 0) {
                    continue;
                }
                int multiPos = transToBeginEndPos(chessBoard, single, role);
                recordManualToMemory(chessBoard, multiPos, role);
                chessBoard.walk(PubTools.uncompressBegin(multiPos), PubTools.uncompressEnd(multiPos));
                chessBoard.print();
                System.out.println("\n\n\n");
                role = role.nextRole();
            }
        }
    }

    public static void recordManualToMemory(ChessBoard chessBoard, int multiPos, Role role) {
        if (role == Role.BLACK) {
            BytesKey bytesKey = new BytesKey(chessBoard.snapshot());
            Set<Integer> set = map.get(bytesKey);
            if (set == null) {
                set = new HashSet<>();
                map.put(bytesKey, set);
            }

            if (set.size() < 128) {
                set.add(multiPos);
            }


            // 棋盘对称反转
            BytesKey bytesKeyConvert = new BytesKey(chessBoard.snapshotConvert());
            Set<Integer> setConvert = map.get(bytesKeyConvert);
            if (setConvert == null) {
                setConvert = new HashSet<>();
                map.put(bytesKey, setConvert);
            }

            if (setConvert.size() < 128) {
                int beginPos = PubTools.uncompressBegin(multiPos);
                int beginX = ChessTools.fetchX(beginPos);
                int beginY = 8 - ChessTools.fetchY(beginPos);
                int finalBeginPos = ChessTools.toPosition(beginX, beginY);

                int endPos = PubTools.uncompressEnd(multiPos);
                int endX = ChessTools.fetchX(endPos);
                int endY = 8 - ChessTools.fetchY(endPos);
                int finalEndPos = ChessTools.toPosition(endX, endY);
                setConvert.add(PubTools.compress(finalBeginPos, finalEndPos));
            }
        }
    }

    private static int transToBeginEndPos(ChessBoard chessBoard, String cmd, Role role) {
        cmd = deleteUselessChar(cmd);
        char[] chars = cmd.toCharArray();
        Map.Entry<Integer, AbstractChessPiece> beginEntry = transToBeginPos(chessBoard, role, chars[0], chars[1]);
        Integer fromPos = beginEntry.getKey();
        AbstractChessPiece piece = beginEntry.getValue();
        int toPos = piece.walkAsManual(chessBoard, fromPos, chars[2], chars[3]);
        return PubTools.compress(fromPos, toPos);
    }

    private static Map.Entry<Integer, AbstractChessPiece> transToBeginPos(ChessBoard chessBoard, Role role, char pieceName, char line) {
        if (pieceName == '前' || pieceName == '后') {
            return transToBeginPos(chessBoard, role, line, pieceName == '前');
        }
        int targetY = ChessTools.transLineToY(line, role);
//        Map<Integer, AbstractChessPiece> map = role == Role.RED ? chessBoard.getRedPiece() : chessBoard.getBlackPiece();
//        for (Map.Entry<Integer, AbstractChessPiece> entry : map.entrySet()) {
//            Integer pos = entry.getKey();
//            int y = ChessTools.fetchY(pos);
//            AbstractChessPiece piece = entry.getValue();
//            if (piece.getName().equals(String.valueOf(pieceName)) && targetY == y) {
//                return entry;
//            }
//        }
        throw new RuntimeException("transToBeginPos error");
    }

    private static Map.Entry<Integer, AbstractChessPiece> transToBeginPos(ChessBoard chessBoard, Role role, char pieceName, boolean isAdvance) {
        if (pieceName == '兵' || pieceName == '卒') {
            throw new RuntimeException();
        }
        Map.Entry<Integer, AbstractChessPiece> entry1 = null;
        Map.Entry<Integer, AbstractChessPiece> entry2 = null;
//        Map<Integer, AbstractChessPiece> map = role == Role.RED ? chessBoard.getRedPiece() : chessBoard.getBlackPiece();
//        for (Map.Entry<Integer, AbstractChessPiece> entry : map.entrySet()) {
//            AbstractChessPiece piece = entry.getValue();
//            if (piece.getName().equals(String.valueOf(pieceName))) {
//                if (entry1 == null) {
//                    entry1 = entry;
//                } else {
//                    entry2 = entry;
//                }
//            }
//        }

        Integer pos1 = entry1.getKey();
        int x1 = ChessTools.fetchX(pos1);
        Integer pos2 = entry2.getKey();
        int x2 = ChessTools.fetchX(pos2);
        if (role == Role.RED) {
            if (isAdvance) {
                return x1 > x2 ? entry1 : entry2;
            } else {
                return x1 > x2 ? entry2 : entry1;
            }
        } else {
            if (isAdvance) {
                return x1 > x2 ? entry2 : entry1;
            } else {
                return x1 > x2 ? entry1 : entry2;
            }
        }
    }

    /**
     * 删除棋谱中无用字符
     *
     * @param cmd   棋谱，例如： 8.兵三进一*
     * @return  整理后的棋谱，例如  兵三进一
     */
    private static String deleteUselessChar(String cmd) {
        System.out.println("cmd begin is " + cmd);
        cmd = cmd.trim();
        // 删除棋谱中所有的*符号
        cmd = cmd.replaceAll("\\*", "");
        cmd = cmd.replaceAll("m", "");
        // 删除.以及之前的字符
        int index = cmd.indexOf(".");
        if (index >= 0) {
            cmd = cmd.substring(index + 1);
        }
        System.out.println("cmd is " + cmd);
        if (cmd.length() != 4) {
            throw new RuntimeException(cmd);
        }
        return cmd;
    }


}

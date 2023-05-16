package com.lkn.chess.manual;

import com.lkn.chess.ChessTools;
import com.lkn.chess.PubTools;
import com.lkn.chess.bean.ChessBoard;
import com.lkn.chess.bean.Role;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author xijiu
 * @since 2023/5/11 上午9:49
 */
public class QuickManual {
    public static File quickCharFile = new File("/Users/likangning/study/study/chess/src/main/resources/quickChar.source");

    public static void quickRead() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(quickCharFile));
        String line = null;
        int num = 1;
        int errNum = 1;
        while ((line = br.readLine()) != null) {
            String[] arr = line.split(" {2}");
            if (arr.length != 2) {
                System.out.println(line);
                System.exit(1);
            }
            if (!isBlackWin(arr[1])) {
                continue;
            }
            line = arr[0];

            try {
                quickReadOneManual(line);
                System.out.println("吸收对局数量 " + num++);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("aaaaaaaaa:::: " + line);
            }
        }
    }

//    public static void main(String[] args) {
//        quickReadOneManual("7747724247435041");
//    }

    private static void quickReadOneManual(String line) {
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.init();
        Role role = Role.RED;
        char[] chars = line.toCharArray();
        for (int i = 0; i < chars.length; i += 4) {
            int fromPos = -1;
            int toPos = -1;
            if (role == Role.RED) {
                char fromY = chars[i];
                char fromX = chars[i + 1];
                int finalFromX = 9 - (fromX - 48);
                int finalFromY = fromY - 48;
                fromPos = ChessTools.toPosition(finalFromX, finalFromY);

                char toY = chars[i + 2];
                char toX = chars[i + 3];
                int finalToX = 9 - (toX - 48);
                int finalToY = toY - 48;
                toPos = ChessTools.toPosition(finalToX, finalToY);
            } else {
                char fromY = chars[i];
                char fromX = chars[i + 1];
                int finalFromX = 9 - (fromX - 48);
                int finalFromY = fromY - 48;
                fromPos = ChessTools.toPosition(finalFromX, finalFromY);

                char toY = chars[i + 2];
                char toX = chars[i + 3];
                int finalToX = 9 - (toX - 48);
                int finalToY = toY - 48;
                toPos = ChessTools.toPosition(finalToX, finalToY);
            }

            int multiPos = PubTools.compress(fromPos, toPos);
            Manual.recordManualToMemory(chessBoard, multiPos, role);
            chessBoard.walk(PubTools.uncompressBegin(multiPos), PubTools.uncompressEnd(multiPos));
//            chessBoard.print();
//            System.out.println("\n\n\n");
//            System.out.println("======================");
            role = role.nextRole();
        }

    }

    private static boolean isBlackWin(String str) {
        return str.contains("黑胜")
                || str.contains("黑方反先")
                || str.contains("黑反先")
                || str.contains("双方均势")
                || str.contains("均势")
                || str.contains("黑方易走")
                || str.contains("黑方足可满意")
                || str.contains("黑方满意")
                || str.contains("自愿作和")
                || str.contains("作和")
                || str.contains("黑方多子占优")
                || str.contains("黑方占优")
                || str.contains("黑方多子胜势")
                || str.contains("黑方多子")
                || str.contains("黑方阵形工整")
                || str.contains("黑方富有弹性")
                || str.contains("各有顾忌")
                || str.contains("双方互缠")
                || str.contains("黑方主动")
                || str.contains("黑主动")
                || str.contains("互缠")
                || str.contains("互有顾忌")
                || str.contains("趋于平稳")
                || str.contains("局势平稳")
                || str.contains("对峙之势")
                || str.contains("黑势颇具弹性")
                || str.contains("红方失控")
                || str.contains("互缠之势")
                || str.contains("和棋")
                || str.contains("黑方略优")
                || str.contains("黑方优")
                || str.contains("各有千秋")
                || str.contains("黑方出子领先")
                || str.contains("黑方已获得满意之势")
                || str.contains("黑势颇有弹性")
                || str.contains("黑方子尽其用")
                || str.contains("黑方得子")
                || str.contains("黑可对抗")
                || str.contains("黑可抗衡")
                || str.contains("黑方先手")
                || str.contains("黑方好走")
                || str.contains("黑子灵活")
                || str.contains("黑方明显占优")
                || str.contains("黑方攻势勇不可挡")
                || str.contains("黑方足可相争")
                || str.contains("黑方子力灵活占先")
                || str.contains("黑子活跃好走")
                || str.contains("黑方多子好走")
                || str.contains("黑多卒占优")
                || str.contains("黑胜势")
                || str.contains("黑方较先")
                || str.contains("黑方子力位置稍好")
                || str.contains("黑方稍优")
                || str.contains("黑稍优")
                || str.contains("黑子稍优")
                || str.contains("黑方优势")
                || str.contains("黑势不差")
                || str.contains("和势")
                || str.contains("黑方兵种齐全较好")
                || str.contains("大致和棋")
                || str.contains("黑方子力灵活")
                || str.contains("黑好走")
                || str.contains("黑方子力灵活占优")
                || str.contains("黑方多卒略优")
                || str.contains("黑方多卒占优")
                || str.contains("黑优")
                || str.contains("黑子好走");
    }
}

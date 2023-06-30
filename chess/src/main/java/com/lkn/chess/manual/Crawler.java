package com.lkn.chess.manual;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * 爬虫，用来拉取网页棋谱
 *
 * @author xijiu
 * @since 2023/5/11 上午9:03
 */
public class Crawler {

    private static PrintWriter pw;

    private static int pageNum = 0;

    static {
        try {
            pw = new PrintWriter(new FileWriter(QuickManual.quickCharFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
//        pawn();
//        cannons2();
        cannons3();
    }


    /**
     * 士角炮
     */
    private static void cannons3() throws Exception {
        for (int i = 1; i <= 5; i++) {
            String parentPage = "https://www.siyuetian.net/kai/ShowClass.asp?ClassID=32&page=" + i;
            readParentPageUrl(parentPage);
        }
    }

    /**
     * 过宫炮
     */
    private static void cannons2() throws Exception {
        for (int i = 1; i <= 12; i++) {
            String parentPage = "https://www.siyuetian.net/kai/ShowClass.asp?ClassID=33&page=" + i;
            readParentPageUrl(parentPage);
        }
    }

    /**
     * 挺兵
     */
    private static void pawn() throws Exception {
        for (int i = 1; i <= 14; i++) {
            String parentPage = "https://www.siyuetian.net/kai/ShowClass.asp?ClassID=30&page=" + i;
            readParentPageUrl(parentPage);
        }
    }

    /**
     * 飞相局
     */
    private static void horse() throws Exception {
        for (int i = 1; i <= 7; i++) {
            String parentPage = "https://www.siyuetian.net/kai/ShowClass.asp?ClassID=31&page=" + i;
            readParentPageUrl(parentPage);
        }
    }

    /**
     * 飞相局
     */
    private static void elephants() throws Exception {
        for (int i = 1; i <= 9; i++) {
            String parentPage = "https://www.siyuetian.net/kai/ShowClass.asp?ClassID=29&page=" + i;
            readParentPageUrl(parentPage);
        }
    }

    /**
     * 中炮的开局棋谱
     */
    private static void midCannons() throws Exception {
        for (int i = 1; i <= 138; i++) {
            String parentPage = "https://www.siyuetian.net/kai/ShowClass.asp?ClassID=28&page=" + i;
            readParentPageUrl(parentPage);
        }
    }

    private static void readParentPageUrl(String parentUrl) throws Exception {
        URL url = new URL(parentUrl);
        //通过url建立与网页的连接
        URLConnection conn = url.openConnection();
        //通过链接取得网页返回的数据
        InputStream is = conn.getInputStream();

        //一般按行读取网页数据，并进行内容分析
        //因此用BufferedReader和InputStreamReader把字节流转化为字符流的缓冲流
        //进行转换时，需要处理编码格式问题
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        boolean begin = false;
        //按行读取并打印
        String line = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.equals("<tbody>")) {
                begin = true;
                continue;
            }
            if (line.equals("</tbody>")) {
                begin = false;
                break;
            }
            if (begin) {
                String target = "/kai/ShowArticle.asp?ArticleID=";
                if (line.contains(target)) {
                    int beginIndex = line.indexOf(target);
                    int endIndex = line.indexOf("\"", beginIndex);
                    String sonUrl = line.substring(beginIndex, endIndex);
                    sonUrl = "https://www.siyuetian.net" + sonUrl;
                    readWebUrl(sonUrl);
                }
            }
        }
        br.close();
    }

    private static void readWebUrl(String webUrl) {
        //建立url爬取核心对象
        try {
            URL url = new URL(webUrl);
            //通过url建立与网页的连接
            URLConnection conn = url.openConnection();
            //通过链接取得网页返回的数据
            InputStream is = conn.getInputStream();

            //一般按行读取网页数据，并进行内容分析
            //因此用BufferedReader和InputStreamReader把字节流转化为字符流的缓冲流
            //进行转换时，需要处理编码格式问题
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuilder sb = new StringBuilder();
            int step = -1;
            //按行读取并打印
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.contains("DhtmlXQ_movelist")) {
                    int start = line.indexOf("]") + 1;
                    int end = line.indexOf("[", 5);
                    String substring = line.substring(start, end);
                    sb.append(substring).append("  ");
                    step = substring.length() / 4;
                }
                if (line.contains("DhtmlXQ_comment" + step)) {
                    String head = "[DhtmlXQ_comment" + step + "]";
                    String tail = "[/DhtmlXQ_comment" + step + "]";

                    int beginIndex = line.indexOf(head) + head.length();
                    int endIndex = line.lastIndexOf(tail);
                    String substring = line.substring(beginIndex, endIndex);
                    sb.append(substring);
                }
            }
            br.close();

            pw.println(sb.toString());
            System.out.println("已完成 " + ++pageNum);
            if (pageNum % 100 == 0) {
                pw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static void printAllWebContent(String webUrl) {
        //建立url爬取核心对象
        try {
            URL url = new URL(webUrl);
            //通过url建立与网页的连接
            URLConnection conn = url.openConnection();
            //通过链接取得网页返回的数据
            InputStream is = conn.getInputStream();

            //一般按行读取网页数据，并进行内容分析
            //因此用BufferedReader和InputStreamReader把字节流转化为字符流的缓冲流
            //进行转换时，需要处理编码格式问题
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuilder sb = new StringBuilder();
            int step = -1;
            //按行读取并打印
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

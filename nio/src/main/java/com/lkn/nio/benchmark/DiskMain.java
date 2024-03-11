package com.lkn.nio.benchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DiskMain {

    private static final AtomicLong TOTAL_LEN = new AtomicLong();

    public static void main(String[] args) throws Exception {
//        args = new String[]{
//                "-p", "/Users/likangning/test/disk",
//                "-t", "4",
//                "-s", "8",
//                "-k", "30",
//        };
        Param param = parseParam(args);
        stateDiskSpeed(param);
        pressDisk(param);
    }

    private static void stateDiskSpeed(Param param) {
        long begin = System.currentTimeMillis();
        Thread thread = new Thread(() -> {
            long lastTime = System.currentTimeMillis();
            DecimalFormat df = new DecimalFormat("#0.00");
            System.out.println();
            while (true) {
                if ((System.currentTimeMillis() - begin) > param.keepTime) {
                    break;
                }
                int sleepSecond = 2;
                sleepSeconds(sleepSecond);

                long size = TOTAL_LEN.getAndSet(0);
                long now = System.currentTimeMillis();
                long cost = now - lastTime;
                double v = size / 1024D / 1024D / (cost / 1000D);
                System.out.println("disk write speed is " + df.format(v) + " MB/sec");
                lastTime = now;
            }
        });
        thread.start();
    }

    private static void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void pressDisk(Param param) throws Exception {

        long begin = System.currentTimeMillis();
        int threadNum = param.threadNum;
        int msgK = param.msgK;
        int keepTime = param.keepTime;
        int size = msgK * 1024;
        List<Thread> threadList = new ArrayList<>();
        for (int j = 0; j < threadNum; j++) {
            Thread thread = new Thread(() -> {
                try {
                    File file = new File(param.basePath + UUID.randomUUID() + ".log");
                    System.out.println("file.getPath(): " + file.getPath());
                    file.createNewFile();
                    FileChannel channel = FileChannel.open(Paths.get(file.getPath()), StandardOpenOption.WRITE);
                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(size);
                    for (int i = 0; ; i++) {
                        byteBuffer.clear();
                        byteBuffer.position(size);
                        byteBuffer.flip();
                        channel.write(byteBuffer);
                        TOTAL_LEN.addAndGet(size);

                        if (i % 100 == 0) {
                            long cost = System.currentTimeMillis() - begin;
                            if (cost > keepTime) {
                                break;
                            }
                        }
                    }
                    channel.force(false);
                    file.delete();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
            threadList.add(thread);
        }
        for (Thread thread : threadList) {
            thread.join();
        }
    }

    private static Param parseParam(String[] args) {
        // 创建一个解析器
        CommandLineParser parser = new DefaultParser();

        // 创建一个 Options，用来包装 option
        Options options = new Options();


        options.addOption(Option.builder("p") //短key
                .longOpt("path") //长key
                .hasArg(true) //是否含有参数
                .argName("path") //参数值的名称
                .required(true) //命令行中必须包含此 option
                .desc("base path") //描述
                .optionalArg(false) //参数的值是否可选
                .numberOfArgs(1) //指明参数有多少个参数值
                //.hasArgs() //无限制参数值个数
                .valueSeparator(',')// 参数值的分隔符
                .type(Integer.class) //参数值的类型
                .build());

        options.addOption(Option.builder("t") //短key
                .longOpt("threadNum") //长key
                .hasArg(true) //是否含有参数
                .argName("threadNum") //参数值的名称
                .required(true) //命令行中必须包含此 option
                .desc("thread number") //描述
                .optionalArg(false) //参数的值是否可选
                .numberOfArgs(1) //指明参数有多少个参数值
                //.hasArgs() //无限制参数值个数
                .valueSeparator(',')// 参数值的分隔符
                .type(Integer.class) //参数值的类型
                .build());

        options.addOption(Option.builder("s") //短key
                .longOpt("size") //长key
                .hasArg(true) //是否含有参数
                .argName("size") //参数值的名称
                .required(true) //命令行中必须包含此 option
                .desc("single write size (K)") //描述
                .optionalArg(false) //参数的值是否可选
                .numberOfArgs(1) //指明参数有多少个参数值
                //.hasArgs() //无限制参数值个数
                .valueSeparator(',')// 参数值的分隔符
                .type(Integer.class) //参数值的类型
                .build());

        options.addOption(Option.builder("k") //短key
                .longOpt("keep") //长key
                .hasArg(true) //是否含有参数
                .argName("keep") //参数值的名称
                .required(true) //命令行中必须包含此 option
                .desc("keep time (Second)") //描述
                .optionalArg(false) //参数的值是否可选
                .numberOfArgs(1) //指明参数有多少个参数值
                //.hasArgs() //无限制参数值个数
                .valueSeparator(',')// 参数值的分隔符
                .type(Integer.class) //参数值的类型
                .build());

        try {
            // 解析命令行参数
            CommandLine line = parser.parse(options, args);

            return new Param(
                    line.getOptionValue("p"),
                    Integer.parseInt(line.getOptionValue("t")),
                    Integer.parseInt(line.getOptionValue("s")),
                    Integer.parseInt(line.getOptionValue("k"))
            );
        } catch (Exception exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
            throw new RuntimeException(exp);
        }
    }

    private static class Param {
        public final String basePath;
        public final int threadNum;
        public final int msgK;
        public final int keepTime;
        private Param(String basePath, int threadNum, int msgK, int keepTime) {
            if (!basePath.endsWith("/")) {
                basePath += "/";
            }
            this.basePath = basePath;
            this.threadNum = threadNum;
            this.msgK = msgK;
            this.keepTime = keepTime * 1000;
        }
    }
}


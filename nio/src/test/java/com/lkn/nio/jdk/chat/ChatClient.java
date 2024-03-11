package com.lkn.nio.jdk.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 发送消息需要写对方名称+“|”+消息体来发送，例如给zhangsan发送消息：
 * zhangsan|你好啊张三
 */
public class ChatClient {

    private final int port = Common.PORT;
    private final String seperator = "|";
    private final Charset charset = StandardCharsets.UTF_8;    //字符集
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private SocketChannel socketChannel;
    private Selector selector;
    private String name = "";
    private boolean flag = true;    //服务端断开，客户端的读事件不会一直发生（与服务端不一样）

    /** 设置消息的最大值为2M */
    private ByteBuffer totalSendBuffer = ByteBuffer.allocate(Common.MAX_MSG_SIZE);

    private Scanner scanner = new Scanner(System.in);

    public void startClient() throws IOException {
        //客户端初始化固定流程：4步
        selector = Selector.open();                                //1.打开Selector
        socketChannel = SocketChannel.open(new InetSocketAddress(port));//2.连接服务端，这里默认本机的IP
        socketChannel.configureBlocking(false);                            //3.配置此channel非阻塞
        SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);//4.将channel的读事件注册到选择器
        selectionKey.attach(null);

        /*
         * 因为等待用户输入会导致主线程阻塞
         * 所以用主线程处理输入，新开一个线程处理读数据
         */
        new Thread(new ClientReadThread()).start();    //开一个异步线程处理读
        String input = "";
        while (flag) {
            input = scanner.nextLine();
            String[] strArray;
            if ("".equals(input)) {
                System.out.println("不允许输入空串！");
                continue;
                // 如果姓名没有初始化，且长度为1.说明当前在设置姓名
            } else if ("".equals(name) && input.split("[|]").length == 1) {
                //啥也不干
                // 如果姓名已经初始化过了，且长度为2.说明这是正常的发送格式
            } else if (!"".equals(name) && input.split("[|]").length == 2) {
                input = input + seperator + name;
            } else {
                System.out.println("输入不合法，请重新输入：");
                continue;
            }
            try {
                byte[] msgBody = input.getBytes(StandardCharsets.UTF_8);
                totalSendBuffer.clear();
                totalSendBuffer.putInt(msgBody.length);
                totalSendBuffer.put(msgBody);
                totalSendBuffer.flip();

                socketChannel.write(totalSendBuffer);
            } catch (Exception e) {
                System.out.println(e.getMessage() + "客户端主线程退出连接！！");
            }
        }
    }

    private class ClientReadThread implements Runnable {
        @Override
        public void run() {
            Iterator<SelectionKey> ikeys;
            SelectionKey key;
            SocketChannel client;
            try {
                while (flag) {
                    selector.select();    //调用此方法一直阻塞，直到有channel可用
                    ikeys = selector.selectedKeys().iterator();
                    while (ikeys.hasNext()) {
                        key = ikeys.next();
                        if (key.isReadable()) {    //处理读事件
                            client = (SocketChannel) key.channel();

                            String msg = Common.readOneMsg(client);
                            System.out.println("msg size is " + msg.length());

                            String[] StrArray = msg.split("[|]");
                            for (String message : StrArray) {
                                if (message.isEmpty()) {
                                    continue;
                                }
                                if (message.contains("您的昵称通过验证")) {
                                    if (message.contains("您的昵称通过验证")) {
                                        String[] nameValid = message.split(" ");
                                        name = nameValid[1];
                                        key.attach(name);
                                    }
                                }
                                System.out.println(message);
                            }
                        }
                        ikeys.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopMainThread() {
        flag = false;
    }


}

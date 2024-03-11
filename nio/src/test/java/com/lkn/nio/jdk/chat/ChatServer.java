package com.lkn.nio.jdk.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    public static final int PORT = Common.PORT;
    private final String seperator = "[|]";                        //消息分隔符
    private final Charset charset = StandardCharsets.UTF_8;    //字符集
    private ByteBuffer buffer = ByteBuffer.allocate(1024);        //缓存
    private Map<String, SocketChannel> onlineUsers = new ConcurrentHashMap<>();
    private Selector selector;
    private ServerSocketChannel server;
    private static ThreadLocal<ByteBuffer> THREAD_BUFFER = ThreadLocal.withInitial(() -> ByteBuffer.allocate(Common.MAX_MSG_SIZE));

    public static void main(String[] args) throws IOException {
        new ChatServer().startServer();
    }

    public void startServer() throws IOException {
        //NIO server初始化固定流程：5步
        selector = Selector.open();                    //1.selector open
        server = ServerSocketChannel.open();        //2.ServerSocketChannel open
        server.bind(new InetSocketAddress(PORT));    //3.serverChannel绑定端口
        server.configureBlocking(false);            //4.设置NIO为非阻塞模式
        server.register(selector, SelectionKey.OP_ACCEPT);//5.将channel注册在选择器上

        //NIO server处理数据固定流程:5步
        SocketChannel client;
        SelectionKey key;
        Iterator<SelectionKey> keyIterator;

        while (true) {
            // 用select()方法阻塞，一直到有可用连接加入
            int select = selector.select(100);
            if (select == 0) {
                continue;
            }

            // 到了这步，说明有可用连接到底，取出所有可用连接
            keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                key = keyIterator.next();
                if (key.isAcceptable()) {                    //4.对每个连接感兴趣的事做不同的处理
                    //对于客户端连接，注册到服务端
                    client = server.accept();            //获取客户端首次连接
                    client.configureBlocking(false);
                    //不用注册写，只有当写入量大，或写需要争用时，才考虑注册写事件
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("+++++客户端：" + client.getRemoteAddress() + "，建立连接+++++");

                    ByteBuffer byteBuffer = THREAD_BUFFER.get();
                    byteBuffer.clear();

                    String str = "请输入自定义用户名：";
                    Common.writeOneMsg(client, str);
                }
                if (key.isReadable()) {
                    operateReadEvent(key);
                }
                keyIterator.remove();                    //5.处理完一次事件后，要显式的移除
            }
        }
    }

    private void operateReadEvent(SelectionKey key) throws IOException {
        SocketChannel clientSocketChannel;
        // 通过key取得客户端channel
        clientSocketChannel = (SocketChannel) key.channel();
        List<String> msgList = new ArrayList<>();

        try {
            msgList.add(Common.readOneMsg(clientSocketChannel));
        } catch (IOException e) {
            //如果client.read(buffer)抛出异常，说明此客户端主动断开连接，需做下面处理
            clientSocketChannel.close();            //关闭channel
            key.cancel();            //将channel对应的key置为不可用
            onlineUsers.values().remove(clientSocketChannel);    //将问题连接从map中删除
            System.out.println("-----用户'" + key.attachment().toString() + "'退出连接，当前用户列表：" + onlineUsers.keySet().toString() + "-----");
            throw new RemoteException();
        }
        if (msgList.size() > 0) {
            processMsg(msgList.get(0), clientSocketChannel, key);
        }
    }

    /**
     * 处理客户端传来的消息
     *
     * @param msg 格式：user_to|body|user_from
     * @throws IOException
     * @Key 这里主要用attach()方法，给通道定义一个表示符
     */
    private void processMsg(String msg, SocketChannel client, SelectionKey key) throws IOException {
        String[] ms = msg.split(seperator);
        if (ms.length == 1) {
            String user = ms[0];    //输入的是自定义用户名
            if (onlineUsers.containsKey(user)) {
                Common.writeOneMsg(client, "当前用户已存在，请重新输入用户名：");
            } else {
                onlineUsers.put(user, client);
//                key.attach(user);    //给通道定义一个表示符
//                //  |字符来作为消息之间的分割符
//                client.write(charset.encode("您的昵称通过验证 "+user+"|"));
                Common.writeOneMsg(client, "您的昵称通过验证 " + user);
                String welCome = "\t欢迎'" + user + "'上线，当前在线人数" + this.getOnLineNum() + "人。用户列表：" + onlineUsers.keySet().toString();
                this.broadCast(welCome+"|");    //给所用用户推送上线信息，包括自己
            }
        } else if (ms.length == 3) {
            String user_to = ms[0];
            String msg_body = ms[1];
            String user_from = ms[2];

            SocketChannel channel_to = onlineUsers.get(user_to);
            if (channel_to == null) {
                Common.writeOneMsg(client, "用户'" + user_to + "'不存在，当前用户列表：" + onlineUsers.keySet());
            } else {
                Common.writeOneMsg(channel_to, "来自'" + user_from + "'的消息：" + msg_body);
            }
        }
    }

    //map中的有效数量已被很好的控制，可以从map中获取，也可以用下面的方法取
    private int getOnLineNum() {
        int count = 0;
        Channel channel;
        for (SelectionKey k : selector.keys()) {
            channel = k.channel();
            if (channel instanceof SocketChannel) {    //排除ServerSocketChannel
                count++;
            }
        }
        return count;
    }

    //广播上线消息
    private void broadCast(String msg) throws IOException {
        Channel channel;
        for (SelectionKey k : selector.keys()) {
            channel = k.channel();
            if (channel instanceof SocketChannel) {
                SocketChannel client = (SocketChannel) channel;
                Common.writeOneMsg(client, msg);
            }
        }
    }

}


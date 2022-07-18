package com.lkn.nio.netty.head_and_body;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

/**
 * @author xijiu
 * @since 2021/12/15 上午8:19
 */
public class Client {
    private final String host = "localhost";

    private static int clientNum = 2;

    private static volatile boolean[] connectFlag = new boolean[clientNum];

    private static MyClientHandler[] myClientHandler = new MyClientHandler[clientNum];

    static {
        for (int i = 0; i < clientNum; i++) {
            myClientHandler[i] = new MyClientHandler(i);
        }
    }

    @Test
    public void testMultiClient() throws Exception {
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                try {
                    startClient(finalI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
    }

//    @Test
    public void startClient(int index) throws Exception {
        // 启动client线程
        new Thread(() -> {
            try {
                Client client = new Client();
                client.doRun(index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // 判断client线程是否与server端建立连接
        while (!connectFlag[index]) {
            Thread.sleep(1);
        }

        // 发送数据
        for (int i = 0; i < 10; i++) {
            System.out.println("准备发送消息");
            syncSendMsg(index, ("msg " + i).getBytes());
            System.out.println("发送消息结束");
        }

        Thread.sleep(100);
    }

    private void doRun(int index) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, Server.PORT))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(409600, 0, 4, 0, 4));
                        ch.pipeline().addLast(new LengthFieldPrepender(4));
                        ch.pipeline().addLast(new ByteArrayDecoder());
                        ch.pipeline().addLast(myClientHandler[index]);
                    }
                }).option(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture f = bootstrap.connect().sync();
        f.channel().closeFuture().sync();
        System.out.println("!!! finish !!!");
        group.shutdownGracefully().sync();
    }

    public void syncSendMsg(int index, byte[] msg) {
        myClientHandler[index].syncSendMsg(msg);
    }


    private static class MyClientHandler extends SimpleChannelInboundHandler<byte[]> {

        private int index;

        public MyClientHandler(int index) {
            this.index = index;
        }

        private ChannelHandlerContext ctx;

        @Override							//和服务器的连接建立起来后被调用
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("client连接成功");
            this.ctx = ctx;
            connectFlag[index] = true;
        }

        @Override							//从服务器收到一条消息时被调用
        protected void channelRead0(ChannelHandlerContext ctx, byte[] msgByteArr) throws Exception {
            System.out.println("client receive msg " + new String(msgByteArr));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        public void sendMsg(byte[] msg) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(msg));
        }

        public void syncSendMsg(byte[] msg) {
            ChannelFuture channelFuture = ctx.writeAndFlush(Unpooled.copiedBuffer(msg));
            try {
                channelFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}

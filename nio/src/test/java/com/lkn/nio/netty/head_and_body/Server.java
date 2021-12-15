package com.lkn.nio.netty.head_and_body;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * 为了解决TCP协议的粘包问题，每次发送消息都指定消息长度，比较通用
 *
 * @author xijiu
 * @since 2021/12/14 下午8:39
 */
public class Server {

    public static final int PORT = 8887;

    @Test
    public void startServer() throws Exception {
        Server server = new Server();
        server.doRun();
    }

    private void doRun() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(PORT))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // 这里将LengthFieldBasedFrameDecoder添加到pipeline的首位，因为其需要对接收到的数据
                        // 进行长度字段解码，这里也会对数据进行粘包和拆包处理
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(409600, 0, 4, 0, 4));
                        ch.pipeline().addLast(new LengthFieldPrepender(4));
                        ch.pipeline().addLast(new ByteArrayDecoder());
                        ch.pipeline().addLast(new MyServerHandler());
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        channelFuture.channel().closeFuture().sync();
        group.shutdownGracefully().sync();
    }


    @ChannelHandler.Sharable
    private static class MyServerHandler extends ChannelInboundHandlerAdapter {

        public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.println("新的请求连接进来了");
            channels.add(ctx.channel());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object jsonObj) throws Exception {
            byte[] bytes = (byte[]) jsonObj;
            String msg = new String(bytes);
            System.out.println("receive msg " + msg);

            notifyClientToSendData(ctx, "收到");
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }


        private static void notifyClientToSendData(ChannelHandlerContext ctx, String msg) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(msg.toCharArray(), Charset.defaultCharset()));
        }

    }
}

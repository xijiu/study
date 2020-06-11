package com.lkn.nio.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.net.InetSocketAddress;

/**
 * Netty 客户端代码
 *
 * @author lihzh
 * @alia OneCoder
 * @blog http://www.coderli.com
 */
public class EchoClient {
	private final String host = "localhost";
	private final int port = 12000;

	private EchoClientHandler echoClientHandler = new EchoClientHandler();

	public static void main(String[] args) throws Exception {
		EchoClient echoClient = new EchoClient();
		echoClient.start();
	}

	public void start() throws Exception {
		new Thread(() -> {
			EventLoopGroup group = new NioEventLoopGroup();
			try {
				Bootstrap bootstrap = new Bootstrap();  //创建Bootstrap
				bootstrap.group(group)  //指定EventLoopGroup来处理客户端事件；需要EventLoopGroup的NIO实现
						.channel(NioSocketChannel.class)    //用于NIO传输的Channel类型
						.remoteAddress(new InetSocketAddress(host, port))  //设置服务器的InetSocketAddress
						.handler(new ChannelInitializer<SocketChannel>() {  //当一个Channel创建时，把一个EchoClientHandler加入它的pipeline中
							@Override
							protected void initChannel(SocketChannel ch) throws Exception {
								ByteBuf buf = Unpooled.copiedBuffer("^".getBytes());
								ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf))
										.addLast(new StringDecoder())
										.addLast(echoClientHandler);
							}
						}).option(ChannelOption.SO_KEEPALIVE, true);
				ChannelFuture f = bootstrap.connect().sync();      //连接到远端，一直等到连接完成
				f.channel().closeFuture().sync();        //一直阻塞到Channel关闭
				System.out.println("!!!结束");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				try {
					group.shutdownGracefully().sync();        //关闭所有连接池，释放所有资源
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		Thread.sleep(500);

		System.out.println(123);

		for (int i = 0; i < 10; i++) {
			System.out.println(i);
			echoClientHandler.sendMsg("发送消息" + i);
			Thread.sleep(100);
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100000; i++) {
			sb.append(i).append(",");
		}
		echoClientHandler.sendMsg(sb.toString());

	}

}

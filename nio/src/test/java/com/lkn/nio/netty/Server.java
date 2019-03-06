package com.lkn.nio.netty;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Server {

	public static void main(String[] args) throws InterruptedException {
		new Server().start();
	}

	public Server() {
		// TODO Auto-generated constructor stub
	}
	// 引导类
	public void start() throws InterruptedException {
		ServerHandler sHandler = new ServerHandler();
		InetSocketAddress localSocket = new InetSocketAddress("127.0.0.1", 9990);
		ServerBootstrap b = new ServerBootstrap();
		b.group(new NioEventLoopGroup())
				.localAddress(localSocket)
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						//添加译码器解码器
						ch.pipeline().addLast(new StringDecoder());
						ch.pipeline().addLast(new StringEncoder());
						ch.pipeline().addLast(sHandler);
					}

				});
		final ChannelFuture f = b.bind().sync();
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				// TODO Auto-generated method stub
				if (f.isSuccess()) {
					System.out.println("服务器开启成功");
				} else {
					System.out.println("服务器开启失败");
					f.cause().printStackTrace();
				}
			}
		});
	}
}

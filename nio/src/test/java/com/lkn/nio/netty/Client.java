package com.lkn.nio.netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Client {

	public void start() throws IOException, InterruptedException {
		Bootstrap b = new Bootstrap();
		EventLoopGroup g = new NioEventLoopGroup();
		//创建对象，后面调用closeChannel()主动关闭连接
		ClientHandler cHandler = new ClientHandler();

		b.group(g)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel sc) throws Exception {
						// TODO Auto-generated method stub
						//添加编码器、译码器
						sc.pipeline().addLast(new StringDecoder());
						sc.pipeline().addLast(new StringEncoder());
						sc.pipeline().addLast(cHandler);
					}
				});
		final ChannelFuture f = b.connect("127.0.0.1", 9990).sync();
		f.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture arg0) throws Exception {
				// TODO Auto-generated method stub
				if (f.isSuccess()) {
					System.out.println("连接服务器成功");

				} else {
					System.out.println("连接服务器失败");
					f.cause().printStackTrace();
				}
			}
		});
		Channel channel = f.channel();
		/*
		 * 获取控制台输入
		 * 当输入了“再见”或“bye”时，停止输入
		 * 主动关闭连接
		 */
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String in = br.readLine();
		while (!(in.equals("再见") || in.equals("bye"))) {
			channel.writeAndFlush(in);
			in = br.readLine();
		}
		channel.writeAndFlush(in);
		cHandler.closeChannel(true);
		g.shutdownGracefully().sync();

	}

	public Client() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		new Client().start();
	}
}

package com.lkn.nio.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.junit.Test;

import java.net.InetSocketAddress;

/**
 * Netty 服务端代码
 */
public class EchoServer {
	private final int port = 12000;

	@Test
	public void start() throws Exception {
		final EchoServerHandler serverHandler = new EchoServerHandler();
		EventLoopGroup group = new NioEventLoopGroup();  //创建EventLoopGroup
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();  //创建ServerBootstrap
			serverBootstrap
					.group(group)
					.channel(NioServerSocketChannel.class)    //指定使用一个NIO传输Channel
					.localAddress(new InetSocketAddress(port))  //用指定的端口设置socket地址
					.childHandler(new ChannelInitializer<SocketChannel>() {  //在Channel的ChannelPipeline中加入EchoServerHandler
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(serverHandler);//EchoServerHandler是@Sharable的，所以我们可以一直用同一个实例
						}
					});
			ChannelFuture channelFuture = serverBootstrap.bind().sync();//异步的绑定服务器，sync()一直等到绑定完成
			channelFuture.channel().closeFuture().sync();//获得这个Channel的CloseFuture，阻塞当前线程直到关闭操作完成
		} finally {
			group.shutdownGracefully().sync();//关闭EventLoopGroup，释放所有资源
		}

	}

}

package com.lkn.nio.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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

	@Test
	public void start() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();  //创建Bootstrap
			bootstrap.group(group)  //指定EventLoopGroup来处理客户端事件；需要EventLoopGroup的NIO实现
					.channel(NioSocketChannel.class)    //用于NIO传输的Channel类型
					.remoteAddress(new InetSocketAddress(host, port))  //设置服务器的InetSocketAddress
					.handler(new ChannelInitializer<SocketChannel>() {  //当一个Channel创建时，把一个EchoClientHandler加入它的pipeline中
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new EchoClientHandler());
						}
					});
			ChannelFuture f = bootstrap.connect().sync();      //连接到远端，一直等到连接完成
			f.channel().closeFuture().sync();        //一直阻塞到Channel关闭
		} finally {
			group.shutdownGracefully().sync();        //关闭所有连接池，释放所有资源
		}

	}

}

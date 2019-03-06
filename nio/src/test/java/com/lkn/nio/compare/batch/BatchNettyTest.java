package com.lkn.nio.compare.batch;

import com.google.common.collect.Lists;
import com.lkn.nio.compare.BatchThreadNioAndBioCompare;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author likangning
 * @since 2019/3/5 上午9:45
 */
public class BatchNettyTest extends BatchThreadNioAndBioCompare {


	private List<Channel> channels = Lists.newArrayList();

	public void jettyClientStart() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();  //创建Bootstrap
			bootstrap.group(group)  //指定EventLoopGroup来处理客户端事件；需要EventLoopGroup的NIO实现
					.channel(NioSocketChannel.class)    //用于NIO传输的Channel类型
					.remoteAddress(new InetSocketAddress(SERVER_HOST, PORT));

			List<Thread> list = Lists.newArrayList();
			for (int i = 0; i < THREAD_NUM; i++) {
				int num = i;
				bootstrap.handler(new ChannelInitializer<SocketChannel>() {  //当一个Channel创建时，把一个EchoClientHandler加入它的pipeline中
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new MyClientHandler(num));
					}
				});
				ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(SERVER_HOST, PORT));
				Channel channel = channelFuture.channel();
				channels.add(channel);
				Thread thread = new Thread(() -> {
					try {
						channel.closeFuture().sync();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
				thread.start();
				list.add(thread);
				Thread.sleep(2);
			}
			for (Thread thread : list) {
				thread.join();
			}
//			ChannelFuture f = bootstrap.connect().sync();      //连接到远端，一直等到连接完成
//			f.channel().closeFuture().sync();        //一直阻塞到Channel关闭
//			Thread.sleep(Long.MAX_VALUE);
		} finally {
//			group.shutdownGracefully().sync();        //关闭所有连接池，释放所有资源
		}
	}

	//	@ChannelHandler.Sharable            //标记这个类的实例可以被多个Channel共享
	public class MyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

		private int clientNum;

		private String clientName;

		private int clientSendHelloTimes = 0;

		private MyClientHandler(int num) {
			this.clientNum = num;
			clientName = "client_" + num;
		}

		@Override              //和服务器的连接建立起来后被调用
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			//当收到连接成功的通知，发送一条消息
			ctx.writeAndFlush(Unpooled.copiedBuffer("login " + clientName, CharsetUtil.UTF_8));
		}

		@Override              //从服务器收到一条消息时被调用
		protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
			String msgStr = msg.toString(CharsetUtil.UTF_8);
//			System.out.println(clientName + " ---- 收到server响应：" + msgStr);//打印收到的消息到日志
			if (clientSendHelloTimes++ < CLIENT_SAY_HELLO_TIMES) {
				ctx.writeAndFlush(Unpooled.copiedBuffer(SAY + " " + clientName + "_" + clientSendHelloTimes, CharsetUtil.UTF_8));
			} else {
				ctx.writeAndFlush(Unpooled.copiedBuffer(BYE + " " + BYE, CharsetUtil.UTF_8));
			}

		}

		@Override              //处理过程中异常发生时被调用
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();//异常发生时，记录错误日志，关闭Channel
			ctx.close();
		}

	}


	/**************************************************  华丽的分割线  *******************************************************/


	public void jettyServerStart() throws Exception {
		MyServerHandler serverHandler = new MyServerHandler();
		EventLoopGroup group = new NioEventLoopGroup();  //创建EventLoopGroup
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();  //创建ServerBootstrap
			serverBootstrap
					.group(group)
					.channel(NioServerSocketChannel.class)    //指定使用一个NIO传输Channel
					.localAddress(new InetSocketAddress(SERVER_HOST, PORT))  //用指定的端口设置socket地址
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

	@ChannelHandler.Sharable      //表明一个ChannelHandler可以被多个Channel安全的共享
	private class MyServerHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {//每次收到消息时被调用
			ByteBuf in = (ByteBuf) msg;
			String msgStr = in.toString(CharsetUtil.UTF_8);
			ctx.writeAndFlush(Unpooled.copiedBuffer("收到client消息：" + msgStr, CharsetUtil.UTF_8));
			if (msgStr.startsWith(BYE)) {
				ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
			}
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			//刷新挂起的数据到远端，然后关闭Channel
//			ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}

		@Override            //在读操作异常被抛出时被调用
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
			ctx.close();            //关闭这个Channel
		}
	}


}

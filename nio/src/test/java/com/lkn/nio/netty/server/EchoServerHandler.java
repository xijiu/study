package com.lkn.nio.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.CharsetUtil;

@Sharable      //表明一个ChannelHandler可以被多个Channel安全的共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {//每次收到消息时被调用
		ByteBuf in = (ByteBuf) msg;
		System.out.println(Thread.currentThread().getName() + " 服务器端收到消息: " + in.toString(CharsetUtil.UTF_8));    //把消息打印到控制台
		ctx.write(Unpooled.copiedBuffer("已收到client消息内容", CharsetUtil.UTF_8));        //将收到的消息写入发送方，不刷新输出消息
	}

	@Override            //用来通知handler上一个ChannelRead()是被这批消息中的最后一个消息调用
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		//刷新挂起的数据到远端，然后关闭Channel
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	@Override            //在读操作异常被抛出时被调用
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();            //关闭这个Channel
	}
}

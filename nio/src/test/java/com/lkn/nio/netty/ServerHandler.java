package com.lkn.nio.netty;

import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Vector;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

	private static final int MAX_CONN = 2;//指定最大连接数
	private int connectNum = 0;//当前连接数
	//channelHandlerContext表
	private Vector<ChannelHandlerContext> contexts = new Vector<>(2);

	//获取当前时间
	private String getTime() {

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int date = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		return new String(year + "/" + month + "/" + date + " " + hour + ":" + minute + ":" + second);

	}

	/*
	 * 重写channelActive()方法
	 * 更新当前连接数
	 * 控制连接客户端的个数，超过则关闭该channel
	 * 更新contexts数组
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		connectNum++;
		//控制客户端连接数量，超过则关闭
		if (connectNum > MAX_CONN) {
			ctx.writeAndFlush(Unpooled.copiedBuffer("达到人数上限".getBytes()));
			ctx.channel().close();
			//当前连接数的更新放在channelInactive()里
		}
		//更新contexts
		contexts.add(ctx);
		//控制台输出相关信息
		InetSocketAddress socket = (InetSocketAddress) ctx.channel().remoteAddress();
		System.out.println(socket.getAddress().getHostAddress() + ":" + socket.getPort() + "已连接");
		System.out.println("当前连接数：" + connectNum);
		ctx.writeAndFlush("hello client");
	}

	/*
	 * 重写channelInactive()方法
	 * 更新当前连接数
	 * 更新contexts数组
	 * 控制台输出相关信息
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//更新当前连接数
		connectNum--;
		//更新contexts数组
		contexts.remove(ctx);
		//控制台输出相关信息
		InetSocketAddress socket = (InetSocketAddress) ctx.channel().remoteAddress();
		System.out.println(getTime() + ' ' + socket.getAddress().getHostAddress() + ":" + socket.getPort() + "已退出");
		System.out.println("当前连接数：" + connectNum);
		//对另一个客户端发出通知
		if (contexts.size() == 1) {
			contexts.get(0).writeAndFlush("对方退出聊天");
		}
	}

	/*
	 * 重写channelRead()函数
	 * 读取数据
	 * 转发消息
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		String in = (String) msg;
		System.out.println(getTime() + " 客户端" + ctx.channel().remoteAddress() + ":" + in);
		//当只有一方在线时，发送通知
		if (contexts.size() < 2) {
			ctx.writeAndFlush("对方不在线");
			return;
		}
		//获取另一个channelhandlercontxt的下表
		int currentIndex = contexts.indexOf(ctx);
		int anotherIndex = Math.abs(currentIndex - 1);
		//给另一个客户端转发信息
		contexts.get(anotherIndex).writeAndFlush(in);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		if (!ctx.channel().isActive()) {
			System.out.println(ctx.channel().remoteAddress() + "客户端异常");
		}
		cause.printStackTrace();
		ctx.close();
	}

}
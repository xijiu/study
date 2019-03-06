package com.lkn.nio.netty;

import java.util.Calendar;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

//因为加入了String类型的编码器和译码器，所以接口实例化为String类型
public class ClientHandler extends SimpleChannelInboundHandler<String> {

	//保存当前ChannelHandlerContext，在后面的closeChannel()中使用
	private ChannelHandlerContext chc = null;

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

	//主动关闭连接
	public void closeChannel(boolean readyToClose) throws InterruptedException {
		if (readyToClose) {
			System.out.println("即将关闭连接");
			chc.channel().closeFuture();
			chc.channel().close();
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//保存当前ChannelHandlerContext
		chc = ctx;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String in) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(getTime() + " " + in);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(getTime() + " 断开连接");
		ctx.channel().closeFuture();
		ctx.channel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		cause.printStackTrace();
		System.out.println("有异常");
		ctx.channel().close();
	}

}
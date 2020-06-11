package com.lkn.nio.netty.client;
 
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
 
@Sharable						//标记这个类的实例可以被多个Channel共享
public class EchoClientHandler extends SimpleChannelInboundHandler<String>{

	private ChannelHandlerContext ctx;


	@Override							//和服务器的连接建立起来后被调用
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//当收到连接成功的通知，发送一条消息
		ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
		this.ctx = ctx;
	}
 
	@Override							//从服务器收到一条消息时被调用
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println("接收到server发送的内容：" + msg);//打印收到的消息到日志
	}
	
	@Override							//处理过程中异常发生时被调用
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();//异常发生时，记录错误日志，关闭Channel
		ctx.close();
	}

	public void sendMsg(String msg) {
		ctx.writeAndFlush(Unpooled.copiedBuffer((msg + "^").getBytes()));
	}
	
}

package com.lkn.nio.jdk.jdk_server;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author likangning
 * @since 2018/9/28 上午8:50
 */
public class Server {
	private static final int BUF_SIZE = 1024;
	private static final int PORT = 8080;
	private static final int TIMEOUT = 3000;


	@Test
	public void selector() throws Exception {
		System.out.println(Integer.MAX_VALUE);
//		selector.close();
//		ssc.close();
	}

	private void handleAccept(SelectionKey key) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
	}

	private void handleRead(SelectionKey key) throws Exception {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		ByteBuffer buf = (ByteBuffer) key.attachment();
		long bytesRead = socketChannel.read(buf);
		while (bytesRead > 0) {
			buf.flip();
			System.out.print(Thread.currentThread().getName() + " : ");
			while (buf.hasRemaining()) {
				System.out.print((char) buf.get());
			}
			System.out.println();
			buf.clear();
			bytesRead = socketChannel.read(buf);
			System.out.println("查询DB需要消耗3秒时间");
			Thread.sleep(3000);
		}
		if (bytesRead == -1) {
			socketChannel.close();
		}
	}

	private void handleWrite(SelectionKey key) throws IOException {
		ByteBuffer buf = (ByteBuffer) key.attachment();
		buf.flip();
		SocketChannel sc = (SocketChannel) key.channel();
		while (buf.hasRemaining()) {
			sc.write(buf);
		}
		buf.compact();
	}


}


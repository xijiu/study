package com.lkn.nio.jdk.jdk_client;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @author likangning
 * @since 2018/9/28 上午8:47
 */
public class Client {

	@Test
	public void client() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		SocketChannel socketChannel = null;

		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress("localhost", 8080));
		if (socketChannel.finishConnect()) {
			int i = 0;
			while (true) {
				TimeUnit.SECONDS.sleep(1);
				String info = "I'm " + i++ + "-th information from client";
				buffer.clear();
				buffer.put(info.getBytes());
				buffer.flip();
				while (buffer.hasRemaining()) {
					System.out.println(buffer);
					socketChannel.write(buffer);
				}
			}
		}
		socketChannel.close();
	}
}

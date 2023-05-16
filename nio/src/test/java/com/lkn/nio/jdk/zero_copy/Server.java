package com.lkn.nio.jdk.zero_copy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

/**
 * @author likangning
 * @since 2018/9/28 上午8:50
 */
public class Server {
	private static final int BUF_SIZE = 1024;
	public static final int PORT = 8081;
	private static final int TIMEOUT = 3000;

	private static File file = new File("/Users/likangning/test/zero_copy_test.log");
	private static FileChannel fileChannel = null;

	@Before
	public void before() throws Exception {
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ);
		ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
		byteBuffer.putInt(1);
		byteBuffer.putInt(2);
		byteBuffer.putInt(3);
		byteBuffer.putInt(4);
		byteBuffer.putInt(5);
		byteBuffer.putInt(6);
		byteBuffer.putInt(7);
		byteBuffer.putInt(8);
		byteBuffer.flip();
		fileChannel.write(byteBuffer, 0);
	}

	@After
	public void after() {
		if (file.exists()) {
			file.delete();
		}
	}


	@Test
	public void selector() throws Exception {
		Selector selector = Selector.open();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		while (true) {
			if (selector.select(TIMEOUT) == 0) {
				System.out.println("==");
				continue;
			}
			Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
			while (iter.hasNext()) {
				SelectionKey selectionKey = iter.next();
				if (selectionKey.isAcceptable()) {
					handleAccept(selectionKey);
				}
				if (selectionKey.isReadable()) {
					handleRead(selectionKey, selector);
				}
				if (selectionKey.isWritable() && selectionKey.isValid()) {
					handleWrite(selectionKey, selector);
				}
				if (selectionKey.isConnectable()) {
					System.out.println("isConnectable = true");
				}
				iter.remove();
			}
		}
//		selector.close();
//		ssc.close();
	}

	private void handleAccept(SelectionKey key) throws IOException {
		System.out.println("handleAccept");
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		// 这里是为了方便，只用到了一个selector，真实生产环境中，会区分多个selector来管理不同的请求
		socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
		System.out.println("Accept over");
	}

	private void handleRead(SelectionKey key, Selector selector) throws Exception {
		System.out.println("handleRead");
		SocketChannel socketChannel = (SocketChannel) key.channel();
		ByteBuffer buf = ByteBuffer.allocate(4096);
		long bytesRead = socketChannel.read(buf);
		while (bytesRead > 0) {
			buf.flip();
			StringBuilder receive = new StringBuilder();
			while (buf.hasRemaining()) {
				receive.append((char) buf.get());
			}
			System.out.println("receive client msg: " + receive);
			buf.clear();
			bytesRead = socketChannel.read(buf);
		}

		if (bytesRead == -1) {
			socketChannel.close();
		}

		socketChannel.register(selector, SelectionKey.OP_WRITE);
		System.out.println("read over");
	}

	private void handleWrite(SelectionKey key, Selector selector) throws IOException {
		System.out.println("handleWrite");

		SocketChannel socketChannel = (SocketChannel) key.channel();

		// 零拷贝，直接将磁盘中的4个字节的数据发送给网卡
		ByteBuffer b1 = ByteBuffer.allocate(4);
		b1.clear();
		fileChannel.read(b1, 0);
		b1.flip();
		System.out.println("b11111: " + b1.getInt());

		fileChannel.transferTo(8, 4, socketChannel);

		socketChannel.register(selector, SelectionKey.OP_READ);

		System.out.println("write over");
	}


}


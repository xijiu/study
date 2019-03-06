package com.lkn.nio.compare.batch;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lkn.nio.compare.BatchThreadNioAndBioCompare;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author likangning
 * @since 2019/3/5 上午9:45
 */
public class BatchNioTest extends BatchThreadNioAndBioCompare {

	private static ExecutorService SERVER_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);

	public void nioClientStart() throws Exception {
		List<Thread> list = Lists.newArrayList();
		for (int i = 0; i < THREAD_NUM; i++) {
			list.add(startClient(i));
			Thread.sleep(2);
		}
		for (Thread t : list) {
			t.join();
		}
	}

	private Thread startClient(int clientNum) throws Exception {
		SocketChannel socketChannel = SocketChannel.open();
		// 会出发server端的注册行为
		socketChannel.connect(new InetSocketAddress(SERVER_HOST, PORT));
		// 设置非阻塞
		socketChannel.configureBlocking(false);
		Selector selector = Selector.open();
		//将当前客户端注册到多路复用器上,并设置为可读状态
		socketChannel.register(selector, SelectionKey.OP_READ);
		Thread thread = new Thread(() -> {
			boolean stop = false;
			AtomicInteger sendTimes = new AtomicInteger(0);
			while (!stop) {
				try {
					//多路复用器开始监听
					selector.select();
					//获取已经注册在多了复用器上的key通道集
					Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
					//遍历
					while (keys.hasNext()) {
						SelectionKey key = keys.next();//获取key
						//如果是有效的
						if (key.isValid()) {
							// 如果为可读状态,读取服务端返回的数据
							if (key.isReadable()) {
								boolean isStop = clientRead(key, clientNum, sendTimes);
								if (isStop) {
									stop = true;
									break;
								}
							}
						}
						//从容器中移除处理过的key
						keys.remove();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		writeMsg(socketChannel, "login nio_client_" + clientNum);
		return thread;
	}

//	private Thread startClient(int clientNum) throws Exception {
//		SocketChannel socketChannel = SocketChannel.open();
//		// 会出发server端的注册行为
//		socketChannel.connect(new InetSocketAddress(SERVER_HOST, PORT));
//		// 设置非阻塞
//		socketChannel.configureBlocking(false);
//		Selector selector = Selector.open();
//		//将当前客户端注册到多路复用器上,并设置为可读状态
//		socketChannel.register(selector, SelectionKey.OP_READ);
//		Thread thread = new Thread(() -> {
//			try {
//				int nSelectedKeys = selector.select(5000);
//				if (nSelectedKeys > 0) {
//					for (SelectionKey skey : selector.selectedKeys()) {
//						if (skey.isConnectable()) {
//							SocketChannel connChannel = (SocketChannel) skey.channel();
//							connChannel.configureBlocking(false);
//							connChannel.register(selector, SelectionKey.OP_READ);
//							connChannel.finishConnect();
//						} else if (skey.isReadable()) {
//							SocketChannel readChannel = (SocketChannel) skey.channel();
//							StringBuilder sb = new StringBuilder();
//							/*
//							 * 定义一个ByteBuffer的容器，容量为1k
//							 */
//							ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//
//							int readBytes = 0;
//							int ret = 0;
//							/*
//							 * 注意，对ByteBuffer的操作，需要关心的是flip，clear等。
//							 */
//							while ((ret = readChannel.read(byteBuffer)) > 0) {
//								readBytes += ret;
//								byteBuffer.flip();
//								sb.append(Charset.forName("UTF-8").decode(byteBuffer).toString());
//								byteBuffer.clear();
//							}
//
//							if (readBytes == 0) {
//								System.err.println("handle opposite close Exception");
//								readChannel.close();
//							}
//						}
//					}
//					selector.selectedKeys().clear();
//				}
//
//
//				ByteBuffer buffer = ByteBuffer.allocate(1024);
//				for (int i = 0; i < CLIENT_SAY_HELLO_TIMES; i++) {
//					int readLen = 0;
//					while (true) {
//						readLen = socketChannel.read(buffer);
//						if (readLen > 0) {
//							break;
//						}
//					}
//
//					buffer.flip();
//					byte[] bytes = new byte[readLen];
//					buffer.get(bytes);
//					buffer.clear();
//					System.out.println("服务器响应: " + new String(bytes));
//					writeMsg(socketChannel, SAY + " hello_server_" + clientNum + "_" + i);
//				}
//				writeMsg(socketChannel, BYE + " " + BYE);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//		thread.start();
//		writeMsg(socketChannel, "login nio_client_" + clientNum);
//		return thread;
//	}

	//客户端获取服务端返回的数据
	private boolean clientRead(SelectionKey key, int clientNum, AtomicInteger sendTimes) {
		boolean stop = false;
		try {
			//建立写缓冲区
			ByteBuffer readBuf = ByteBuffer.allocate(128);
			//2 获取之前注册的socket通道对象
			SocketChannel sc = (SocketChannel) key.channel();
			//3 读取数据
			int count = sc.read(readBuf);
			//4 如果没有数据
			if (count == -1) {
				key.channel().close();
				key.cancel();
				return false;
			}
			//5 有数据则进行读取 读取之前需要进行复位方法(把position 和limit进行复位)
			readBuf.flip();
			//6 根据缓冲区的数据长度创建相应大小的byte数组，接收缓冲区的数据
			byte[] bytes = new byte[readBuf.remaining()];
			//7 接收缓冲区数据
			readBuf.get(bytes);
			//8 打印结果
			String body = new String(bytes).trim();
//			System.out.println("服务器响应: " + body);

			String command;
			if (sendTimes.incrementAndGet() <= CLIENT_SAY_HELLO_TIMES) {
				command = SAY + " hello_server_" + clientNum + "_" + sendTimes.get();
			} else {
				stop = true;
				command = BYE + " " + BYE;
			}
			writeMsg(sc, command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stop;
	}

	private void writeMsg(SocketChannel socketChannel, String msg) throws IOException {
		ByteBuffer writeBuf = ByteBuffer.allocate(msg.getBytes().length);
		writeBuf.put(msg.getBytes());
		writeBuf.flip();
		socketChannel.write(writeBuf);
		writeBuf.clear();
	}


	public void nioServerStart() throws Exception {
		// 打开多路复用器
		Selector selector = Selector.open();
		// 打开服务器通道
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		// 设置服务器通道为非阻塞方式
		serverSocketChannel.configureBlocking(false);
		// 绑定ip
		serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, PORT));
		// 把服务器通道注册到多路复用器上,只有非阻塞信道才可以注册选择器.并在注册过程中指出该信道可以进行Accept操作
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("服务器已经启动.....");
		while (true) {
			try {
				// 多路复用器开始监听
				selector.select();
				//获取已经注册在多了复用器上的key通道集
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
				//遍历
				while (keys.hasNext()) {
					SelectionKey key = keys.next();
					//如果是有效的
					if (key.isValid()) {
						// 新连接接入
						if (key.isAcceptable()) {
							accept(selector, key);
						}
						// 如果为可读状态,一般是客户端通道
						if (key.isReadable()) {
							read(key);
						}
					}
					//从容器中移除处理过的key
					keys.remove();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static Map<SocketChannel, String> socketChannelName = Maps.newHashMap();

	private void read(SelectionKey selectionKey) throws IOException {
		ByteBuffer readBuf = ByteBuffer.allocate(64);
		ByteBuffer writeBuf = ByteBuffer.allocate(64);
		//2 获取之前注册的socket通道对象
		SocketChannel sc = (SocketChannel) selectionKey.channel();
		//3 读取数据
		int count = sc.read(readBuf);
		//4 如果没有数据
		if (count == -1) {
			selectionKey.channel().close();
			selectionKey.cancel();
			return;
		}
		//5 有数据则进行读取 读取之前需要进行复位方法(把position 和limit进行复位)
		readBuf.flip();
		//6 根据缓冲区的数据长度创建相应大小的byte数组，接收缓冲区的数据
		byte[] bytes = new byte[readBuf.remaining()];
		//7 接收缓冲区数据
		readBuf.get(bytes);
		//8 打印结果
		String body = new String(bytes).trim();
		String[] commands = body.split(" ");
		if (Objects.equal(commands[0], "login")) {
			socketChannelName.put(sc, commands[1]);
			System.out.println(commands[1] + " 已登录");
		}
		// 向客户端发送响应的过程交给线程池来做，避免因为一些耗时操作，导致IO阻塞
		SERVER_POOL.submit(() -> {
			try {
				// 模拟server端的耗时操作
//				Thread.sleep(2);
//				Thread.sleep((long) (Math.random() * 10));
				//9 告诉客户端已收到数据
//				String msg = socketChannelName.get(sc) + ": " + body;
				String msg = "hav rev msg : " + body;
				writeBuf.put(msg.getBytes());
				//对缓冲区进行复位
				writeBuf.flip();
				//写出数据到服务端
				sc.write(writeBuf);
				//清空缓冲区数据
				writeBuf.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void accept(Selector selector, SelectionKey selectionKey) throws IOException {
		//1 获取服务通道
		ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
		//2 执行阻塞方法,当有客户端请求时,返回客户端通信通道
		SocketChannel sc = ssc.accept();
		//3 设置阻塞模式
		sc.configureBlocking(false);
		//4 注册到多路复用器上，并设置可读标识
		sc.register(selector, SelectionKey.OP_READ);
	}

}

package com.lkn.nio.jdk.zero_copy;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author likangning
 * @since 2018/9/28 上午8:47
 */
public class Client {

	@Test
	public void client() throws Exception {
		SocketChannel socketChannel = null;

		Selector selector = Selector.open();
		socketChannel = SocketChannel.open();
		InputX in = new InputX();
		socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress("localhost", Server.PORT));
		SelectionKey clientKey = socketChannel.register(selector, SelectionKey.OP_CONNECT);

		while (true) {
			System.out.println("123444444");
			selector.select();
			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = readyKeys.iterator();

			while (iterator.hasNext()) {
				System.out.println("123");
				SelectionKey key = iterator.next();
				iterator.remove();

				if (key.isConnectable()) {
					SocketChannel connection = (SocketChannel) key.channel();

					connection.configureBlocking(false);
					if(connection.isConnectionPending()) {
						connection.finishConnect();
					}

					System.out.println("建立连接");

//					connection.register(selector, SelectionKey.OP_READ);
					connection.register(selector, SelectionKey.OP_WRITE);
//					in.start();
					System.out.println("456");
				} else {
					if (key.isWritable()) {
						System.out.println("客户端写");
						Thread.sleep(1000);
						SocketChannel connection = (SocketChannel)key.channel();
						if (in.getRetu() != null && (!in.getRetu().equals("")) ){
							ByteBuffer buffer = ByteBuffer.allocate(1024);
							String str = "" ;
							String read = in.getRetu();
							str = str+read;
							buffer.put(str.getBytes("UTF-8"));
							buffer.put((byte) '\r');
							buffer.put((byte) '\n');
							//排空buffer，将limit设置为当前位置，再将position设置为0
							buffer.flip();
							connection.write(buffer);
							in.setRetu("i am client");
						}
						connection.register(selector, SelectionKey.OP_READ);
					} else if (key.isReadable()) {
						SocketChannel connection = (SocketChannel)key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						//服务器关闭后，下行代码异常
						connection.read(buffer);
						buffer.flip();
						System.out.println("客户端读 : " + buffer.getInt());
						buffer.clear();
						connection.register(selector, SelectionKey.OP_WRITE);
					}
				}
			}
		}
	}


	class InputX extends Thread{

		private String retu = "i am client";

		@Override
		public void run() {
			Scanner scan = new Scanner(System.in);
			while(true) {
				retu = scan.nextLine();
			}


		}

		public String getRetu() {
			return retu;
		}

		public void setRetu(String s) {
			this.retu = s;
		}

	}


}

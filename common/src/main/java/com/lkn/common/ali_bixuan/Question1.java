package com.lkn.common.ali_bixuan;

import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 1.基于BIO实现server端，当建立了100个连接时，会有多少个线程？如果基于NIO，又会是多少个线程？为什么？
 *
 * @author likangning
 * @since 2019/3/1 上午10:11
 */
public class Question1 {

	private static int SERVER_SOCKET_PORT = 10086;

	@Test
	public void bioServer() throws Exception {
		BIOServer bioServer = new BIOServer();
		bioServer.startServer();
	}

	@Test
	public void bioClient() throws Exception {
		BIOClient bioClient = new BIOClient("客户端1");
		bioClient.sendToServer();
	}

	private class BIOServer {
		private void startServer() throws IOException {
			ServerSocket serverSocket = new ServerSocket(SERVER_SOCKET_PORT);
			System.out.println("bio server端开始监控端口：" + SERVER_SOCKET_PORT);
			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(() -> {
					try {
						InputStream is = socket.getInputStream();
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader br = new BufferedReader(isr);
						String info;
						while ((info = br.readLine()) != null) {
							System.out.println("我是服务器，客户端说：" + info);
							//4、获取输出流，响应客户端的请求
							OutputStream os = socket.getOutputStream();
							PrintWriter pw = new PrintWriter(os);
							pw.println("欢迎您！");
							pw.flush();
							pw.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).start();
			}
		}
	}

	private class BIOClient {
		private String name;
		private BIOClient(String name) {
			this.name = name;
		}
		private void sendToServer() throws IOException {
			Socket socket = new Socket("localhost", SERVER_SOCKET_PORT);
			OutputStream os = socket.getOutputStream();//字节输出流
			PrintWriter pw = new PrintWriter(os);//将输出流包装成打印流
			pw.write(name + " -- 用户名：admin；密码：123");
			pw.flush();
			socket.shutdownOutput();
			//3、获取输入流，并读取服务器端的响应信息
			InputStream is = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String info = null;
			while((info = br.readLine()) != null){
				System.out.println("我是客户端，服务器说：" + info);
			}
			//4、关闭资源
			br.close();
			is.close();
			pw.close();
			os.close();
			socket.close();
		}
	}
}

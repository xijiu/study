package com.lkn.nio.compare.batch;

import com.google.common.base.Objects;
import com.lkn.nio.compare.BatchThreadNioAndBioCompare;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author likangning
 * @since 2019/3/5 上午9:45
 */
public class BatchBioTest extends BatchThreadNioAndBioCompare {


	public void bioClientStart() throws InterruptedException {
		Thread[] clientThreadArr = new Thread[THREAD_NUM];
		for (int i = 0; i < THREAD_NUM; i++) {
			final int num = i;
			final String clientName = "client_" + num;
			Thread thread = new Thread(() -> {
				try {
					Socket socket = new Socket(SERVER_HOST, PORT);
					PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					sendMsg(pw, "login" + " " + clientName);
					if (br.readLine().equals(LOGIN_SUCCESS)) {
						for (int j = 0; j < CLIENT_SAY_HELLO_TIMES; j++) {
							sendMsg(pw, SAY + " " + "hello_server_" + j);
							String serverResponse = br.readLine();
//							System.out.println("服务器响应：" + serverResponse);
						}

						sendMsg(pw, BYE + " " + BYE);
						br.readLine();
					}
					pw.close();
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			clientThreadArr[i] = thread;
			thread.start();
			Thread.sleep(2);
		}
		for (Thread thread : clientThreadArr) {
			thread.join();
		}
	}

	private void sendMsg(PrintWriter pw, String content) {
		pw.println(content);
		pw.flush();
	}



	/*******************************************   华丽的分割线  ***********************************/


//	private static ExecutorService SERVER_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);


	public void bioServerStart() throws Exception {
		Thread serverThread = new Thread(() -> {
			try {
				ServerSocket serverSocket = new ServerSocket(PORT);
				while (true) {
					System.out.println("bio server端已经做好接受数据准备。。。。");
					Socket socket = serverSocket.accept();

					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String loginMessage = bufferedReader.readLine();
					String judge[] = loginMessage.split(" ");

					System.out.println(judge[1] + "登陆成功");
					BioServerThread bioServerThread = new BioServerThread(socket, judge[1]);
					bioServerThread.start();
//					SERVER_POOL.submit(bioServerThread);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		serverThread.start();
		serverThread.join();
	}

	/**
	 * bio线程
	 */
	private class BioServerThread extends Thread {
		private BufferedReader bufferedReader = null;
		private PrintWriter pw = null;
		private String name;
		private Socket socket;

		private BioServerThread(Socket socket, String name) {
			this.name = name;
			this.socket = socket;
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				pw = new PrintWriter(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			while (true) {
				boolean stop = false;
				try {
					sendMsg(pw, LOGIN_SUCCESS);
					String content;
					while ((content = bufferedReader.readLine()) != null) {
						String[] split = content.split(" ");
						String command = split[0];
						String info = split[1];
						if (Objects.equal(SAY, command)) {
							// 模拟服务器端耗时操作
//							Thread.sleep(2);
//							Thread.sleep((long) (Math.random() * 10));
							sendMsg(pw, "has rev msg : " + info);
						} else if (Objects.equal(BYE, command)) {
							sendMsg(pw, BYE);
							bufferedReader.close();
							pw.close();
							stop = true;
							break;
						}
					}
					if (stop) {
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

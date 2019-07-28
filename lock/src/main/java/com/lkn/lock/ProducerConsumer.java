package com.lkn.lock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * java多线程模拟生产者消费者问题
 *
 * ProducerConsumer是主类，Producer生产者，Consumer消费者，Product产品，Storage仓库
 *
 * @author 林计钦
 * @version 1.0 2013-7-24 下午04:49:02
 */
public class ProducerConsumer {

	public static void main(String[] args) {
		ProducerConsumer pc = new ProducerConsumer();
		Storage s = pc.new Storage();
		ExecutorService service = Executors.newCachedThreadPool();
		Producer p1 = pc.new Producer("生产者1", s);
		Producer p2 = pc.new Producer("生产者2", s);
		Consumer c1 = pc.new Consumer("消费者1", s);
		Consumer c2 = pc.new Consumer("消费者2", s);
		Consumer c3 = pc.new Consumer("消费者3", s);
		service.submit(p1);
		service.submit(p2);
		service.submit(c1);
		service.submit(c2);
		service.submit(c3);
	}

	/**
	 * 消费者
	 *
	 * @author 林计钦
	 * @version 1.0 2013-7-24 下午04:53:30
	 */
	class Consumer implements Runnable {
		private String name;
		private Storage s;

		private Consumer(String name, Storage s) {
			this.name = name;
			this.s = s;
		}

		public void run() {
			try {
				while (true) {
					System.out.println(name + "准备消费产品");
					Product product = s.pop();
					System.out.println(name + "已消费(" + product.toString() + ")");
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * 生产者
	 *
	 * @author 林计钦
	 * @version 1.0 2013-7-24 下午04:53:44
	 */
	class Producer implements Runnable {
		private String name;
		private Storage storage;

		private Producer(String name, Storage s) {
			this.name = name;
			this.storage = s;
		}

		public void run() {
			try {
				int num = 1;
				while (true) {
					Product product = new Product(num++);
					System.out.println("::::::::::::::::::" + name + " 准备生产(" + product.toString() + ")");
					storage.push(product);
					System.out.println("::::::::::::::::::" + name + " 已生产(" + product.toString() + ")");
					Thread.sleep(1);
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 仓库，用来存放产品
	 *
	 * @author 林计钦
	 * @version 1.0 2013-7-24 下午04:54:16
	 */
	public class Storage {
		BlockingQueue<Product> queues = new LinkedBlockingQueue<>(10);

		/**
		 * 生产
		 *
		 * @param p
		 *            产品
		 * @throws InterruptedException
		 */
		public void push(Product p) throws InterruptedException {
			queues.put(p);
		}

		/**
		 * 消费
		 *
		 * @return 产品
		 * @throws InterruptedException
		 */
		public Product pop() throws InterruptedException {
			return queues.take();
		}
	}

	/**
	 * 产品
	 *
	 * @author 林计钦
	 * @version 1.0 2013-7-24 下午04:54:04
	 */
	public class Product {
		private int id;

		public Product(int id) {
			this.id = id;
		}

		public String toString() {// 重写toString方法
			return "产品：" + this.id;
		}
	}

}

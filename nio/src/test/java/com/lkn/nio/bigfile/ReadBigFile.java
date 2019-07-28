package com.lkn.nio.bigfile;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author likangning
 * @since 2019/7/11 下午3:06
 */
public class ReadBigFile {
	private File file = new File("/Users/likangning/Downloads/dianying_wushuang.mp4");

	@Test
	public void test() throws Exception {
		for (int i = 0; i < 10; i++) {
			concurrentRead(i + 1);
		}
	}

	private void concurrentRead(int concurrentNum) throws Exception {
		long begin = System.currentTimeMillis();
		long fileSize = 3104481280L;
		long perSize = fileSize / concurrentNum;
		Thread[] threads = new Thread[concurrentNum];
		for (int i = 0; i < concurrentNum; i++) {
			long beginPosition = perSize * i;
			threads[i] = new Thread(() -> {
				try {
					FileInputStream fin = new FileInputStream(file);
					FileChannel channel = fin.getChannel();
					channel.position(beginPosition);
					int capacity = 4096;
					ByteBuffer bf = ByteBuffer.allocate(capacity);

					long readSize = 0;
					while ((channel.read(bf)) != -1) {
						byte[] bytes = bf.array();
						readSize = readSize + bytes.length;
						bf.clear();
						if (readSize >= perSize) {
							break;
						}
					}
					channel.close();
					fin.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
		long end = System.currentTimeMillis();
		System.out.println("并发数为 " + concurrentNum + ", 耗时：" + (end - begin));
	}

}

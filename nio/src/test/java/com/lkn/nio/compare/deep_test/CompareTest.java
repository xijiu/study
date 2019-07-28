package com.lkn.nio.compare.deep_test;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author likangning
 * @since 2019/7/25 上午8:13
 */
public class CompareTest {

	private File channelFile = new File("/Users/likangning/test/io/data.index");

	@Before
	public void init () throws IOException {
		if (channelFile.exists()) {
			channelFile.delete();
		}
		channelFile.createNewFile();
	}

	private int totalNumber = 12800000;

	@Test
	public void abc() {
		long begin = System.currentTimeMillis();
		List<Message> messages = new ArrayList<>(totalNumber);
		for (int i = 0; i < totalNumber; i++) {
			long l = System.currentTimeMillis();
			messages.add(new Message(l, l, null));
		}

		storeMsg(messages, channelFile);
		long end = System.currentTimeMillis();
		System.out.println("整体耗时： " + (end - begin));
	}

	@Test
	public void abc222() throws Exception {
		long begin = System.currentTimeMillis();
		Thread[] threads = new Thread[10];

		for (int i = 0; i < threads.length; i++) {
			File channelFile = new File("/Users/likangning/test/io/data" + i + ".index");
			if (channelFile.exists()) {
				channelFile.delete();
			}

			List<Message> messages = new ArrayList<>();
			for (int j = 0; j < totalNumber / threads.length; j++) {
				long l = System.currentTimeMillis();
				messages.add(new Message(l, l, null));
			}

			channelFile.createNewFile();
			Thread thread = new Thread(() -> {
				try {
					storeMsg(messages, channelFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			threads[i] = thread;
			thread.start();
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		long end = System.currentTimeMillis();
		System.out.println("耗时： " + (end - begin));
	}


	/**
	 * 保存 a、t 的索引数据
	 */
	private static void storeMsg(List<Message> messages, File msgFile) {
		long totalSpace = messages.size() * TreeConst.LEAF_NODE_ELEMENT_SPACE;
		try (FileChannel fileChannel = FileChannel.open(Paths.get(msgFile.toURI()), StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, totalSpace);

			int index = 0;
			ByteBuffer byteBuffer = ByteBuffer.allocate(TreeConst.NODE_SPACE);
			for (Message message : messages) {
				index++;
				byteBuffer.putLong(message.getA());
				byteBuffer.putLong(message.getT());
				if (index % TreeConst.LEAF_NODE_SIZE == 0) {
					mappedByteBuffer.put(byteBuffer.array());
					byteBuffer.clear();
				}
			}

			putRemainingData(byteBuffer, mappedByteBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将byteBuffer中最终残留的数据放入mappedByteBuffer中
	 * @param byteBuffer	字节码封装类
	 * @param mappedByteBuffer	映射
	 */
	private static void putRemainingData(ByteBuffer byteBuffer, MappedByteBuffer mappedByteBuffer) {
		// 处理最后数据
		byteBuffer.flip();
		if (byteBuffer.hasRemaining()) {
			int limit = byteBuffer.limit();
			byte[] data = new byte[limit];
			byteBuffer.get(data);
			mappedByteBuffer.put(data);
		}
	}


	@Test
	public void abc2() {
		int a = 3233322;
		int b = 78348;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 1000000000; i++) {
//			int c = (i + 100000) / (i + 1);
			int c = myDiv(i + 1, i + 100);
		}
//		System.out.println(myDiv(a, b));

		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}

	public static int myDiv(int a,int b) {
		int msb=0;
		while((b<<msb)<a)
		{
			msb++;
		}
		int q=0;
		for(int i=msb;i>=0;i--)
		{
			if((b<<i)>a) continue;
			q|=(1<<i);
			a-=(b<<i);
		}
		return q;
	}
}

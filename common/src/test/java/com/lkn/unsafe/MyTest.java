//package com.lkn.unsafe;
//
//import org.junit.Test;
//import sun.jvm.hotspot.utilities.Bits;
//import sun.nio.ch.DirectBuffer;
//
//import java.nio.ByteBuffer;
//
///**
// * @author likangning
// * @since 2019/8/7 下午3:06
// */
//public class MyTest {
//
//	private static int MSG_SIZE = 2;
//	private static int MSG_NUM = 200000000;
//	private static int ALLOCATE_SIZE = MSG_NUM * MSG_SIZE;
//
//	@Test
//	public void test() {
//		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(ALLOCATE_SIZE);
//		long beginPosition = ((DirectBuffer) byteBuffer).address();
//
//		putMsgToDirectMemory(beginPosition);
//
//		long begin = System.currentTimeMillis();
//		readFromDirectMemory(byteBuffer);
//		long end = System.currentTimeMillis();
//		System.out.println("读取耗时： " + (end - begin));
//	}
//
//	private void readFromDirectMemory(ByteBuffer byteBuffer) {
//		for (int i = 0; i < MSG_NUM; i++) {
////			short aShort = byteBuffer.getShort(i * MSG_SIZE);
//			short aShort = byteBuffer.getShort(i << 1);
//		}
//	}
//
//	private void putMsgToDirectMemory(long beginPosition) {
//		for (int i = 0; i < MSG_NUM; i++) {
//			byte[] array = ByteBuffer.allocate(MSG_SIZE).putShort((short) 3239).array();
//			UnsafeUtil.UNSAFE.copyMemory(array, 16, null, beginPosition, MSG_SIZE);
//			beginPosition += MSG_SIZE;
//		}
//	}
//
//	@Test
//	public void b() {
//		System.out.println(2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2);
//		System.out.println((11 * 8641 + 3 * 4375) / (8641 + 4375));
//		System.out.println((double)(4 * 4375 + 12 * 8614) /  13016);
////		long begin = System.currentTimeMillis();
////		int a = 5;
////		for (int i = 0; i < 2000000000; i++) {
////			a++;
////		}
////		long end = System.currentTimeMillis();
////		System.out.println("读取耗时： " + (end - begin));
//	}
//
//	private int[] arr() {
//		return new int[2];
//	}
//
//	private int arr2() {
//		return 0;
//	}
//
//
//	@Test
//	public void c() throws InterruptedException {
//		ByteBuffer byteBuffer = ByteBuffer.allocate(1);
//		int end = 200000000;
//		int[] aArr = new int[end];
//		int[] tArr = new int[end];
//		for (int i = 0; i < end; i++) {
//			aArr[i] = i;
//			tArr[i] = i;
//		}
//
//		int threadNum = 10;
//		Thread[] threads = new Thread[threadNum];
//		long begin = System.currentTimeMillis();
//		for (int i = 0; i < threadNum; i++) {
//			threads[i] = new Thread(() -> {
//				int num = 0;
//				for (int j = 0; j < end; j++) {
//					int a = aArr[j];
//					int t = tArr[j];
//					if (a >= 5000000 && a <= 150000000 && t >= 5000000 && t <= 150000000) {
//						num++;
//					}
//				}
//			});
//			threads[i].join();
//		}
//		for (Thread thread : threads) {
//			thread.join();
//		}
//		long endTime = System.currentTimeMillis();
//		System.out.println("耗时： " + (endTime - begin));
//	}
//
//	@Test
//	public void d() {
//		int a = 7;
//		int i = Bits.nthBit(2);
//		System.out.println(i);
//	}
//
//
//}

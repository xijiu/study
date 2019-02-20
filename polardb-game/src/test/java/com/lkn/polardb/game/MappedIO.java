package com.lkn.polardb.game;

/**
 * Stream与MappedByteBuffer的性能对比.
 *
 Stream Write : 3.59
 Mapped Write : 0.27
 Stream Read : 3.17
 Mapped Read : 0.21
 Stream Read/Write : 30.86
 Mapped Read/Write : 0.15
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedIO {

	private static int numOfInts = 40000000;
	private static int numOfUbuffInts = 200000;

	private abstract static class Tester {

		private String name;

		public Tester(String name){
			this.name = name;
		}

		public void runTest(){
			System.out.print(name + " : ");
			try {
				long start = System.nanoTime();
				test();
				double duration = System.nanoTime() - start;
				System.out.format("%.2f\n", duration/1.0e9);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public abstract void test() throws IOException ;
	}

	private static Tester[] tests = {
//			new Tester("Stream Write"){
//				public void test() throws IOException{
//					DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File("/Users/likangning/study/study/polardb-game/db/temp.tmp"))));
//					for(int i = 0; i < numOfInts; i++){
//						dos.writeInt(i);
//					}
//					dos.close();
//				}
//			},
			new Tester("Mapped Write"){
				public void test() throws IOException{
					File file = new File("/Users/likangning/study/study/polardb-game/db/temp.tmp");
					FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
					IntBuffer ib = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, numOfInts * 4).asIntBuffer();

					for(int i = 0; i < numOfInts; i++){
						ib.put(i);
					}
					fileChannel.close();
				}
			},
			new Tester("Mapped Write Test"){
				public void test() throws IOException {
					int pageSize = 1000000;
					File file = new File("/Users/likangning/study/study/polardb-game/db/temp.tmp");
					FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
					IntBuffer ib = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 4 * pageSize).asIntBuffer();

					for(int i = 0; i < numOfInts; i++) {
						ib.put(i);
						if (i % pageSize == 0) {
							ib = fileChannel.map(FileChannel.MapMode.READ_WRITE, file.length(), 4 * pageSize).asIntBuffer();
						}
					}
					fileChannel.close();
				}
			},
//			new Tester("Stream Read"){
//				public void test() throws IOException{
//					DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(new File("d:\\temp.tmp"))));
//					for(int i = 0; i < numOfInts; i++){
//						dis.readInt();
//					}
//					if(dis != null){
//						dis.close();
//					}
//				}
//			},
//			new Tester("Mapped Read"){
//				public void test() throws IOException{
//					FileChannel fc = new FileInputStream("d:\\temp.tmp").getChannel();
//					IntBuffer ib = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size()).asIntBuffer();
//					while(ib.hasRemaining()){
//						ib.get();
//					}
//					if(fc != null){
//						fc.close();
//					}
//				}
//			},
//			new Tester("Stream Read/Write"){
//				public void test() throws IOException{
//					RandomAccessFile raf = new RandomAccessFile("d:\\temp.tmp","rw");
//					raf.writeInt(1);
//					for(int i = 0; i < numOfUbuffInts; i++){
//						raf.seek(raf.length() - 4);//绝对定位，离文件头的字节数.skipBytes相对定位，跳过指定长度。
//						raf.writeInt(raf.readInt());
//					}
//					if(raf != null){
//						raf.close();
//					}
//				}
//			},
//			new Tester("Mapped Read/Write"){
//				public void test() throws IOException{
//					FileChannel fc = new RandomAccessFile("d:\\temp.tmp", "rw").getChannel();
//					IntBuffer ib = fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size()).asIntBuffer();
//					ib.put(0);
//					for(int i = 1; i < numOfUbuffInts; i++){
//						ib.put(ib.get( i - 1));
//					}
//					if(fc != null){
//						fc.close();
//					}
//				}
//			}
	};

	public static void main(String[] args) {
		for(Tester tst : tests){
			tst.runTest();
		}
	}
}

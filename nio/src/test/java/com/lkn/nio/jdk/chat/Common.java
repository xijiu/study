package com.lkn.nio.jdk.chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Common {

    public static final int PORT = 6821;

    public static final int MAX_MSG_SIZE = 2 * 1024 * 1024;

    private static final Charset charset = Charset.forName("UTF-8");

    private static final ThreadLocal<ByteBuffer> THREAD_BUFFER = ThreadLocal.withInitial(() -> ByteBuffer.allocate(Common.MAX_MSG_SIZE));


    public static String readOneMsg(SocketChannel socketChannel) throws IOException {
        try {
            int msgLength = readMsgLength(socketChannel);
            ByteBuffer msgByteBuffer = readMsgContent(socketChannel, msgLength);
            return charset.decode(msgByteBuffer).toString();
        } catch (IOException e) {
            throw new IOException();
        }
    }

    private static ByteBuffer readMsgContent(SocketChannel clientSocketChannel, int msgLength) throws IOException {
        ByteBuffer byteBuffer = THREAD_BUFFER.get();
        byteBuffer.clear();

        System.out.println("msgLength is " + msgLength);
        byteBuffer.limit(msgLength);
        readFull(clientSocketChannel, byteBuffer);
        byteBuffer.flip();
        return byteBuffer;
    }

    private static int readMsgLength(SocketChannel clientSocketChannel) throws IOException {
        ByteBuffer msgSizeBuffer = ByteBuffer.allocate(4);
        readFull(clientSocketChannel, msgSizeBuffer);
        msgSizeBuffer.flip();
        return msgSizeBuffer.getInt();
    }

    private static void readFull(SocketChannel socketChannel, ByteBuffer byteBuffer) throws IOException {

        socketChannel.read(byteBuffer);

//        do {
//            socketChannel.read(byteBuffer);
//        } while (byteBuffer.position() != byteBuffer.limit());
    }

    public static void writeOneMsg(SocketChannel socketChannel, String msg) throws IOException {
        ByteBuffer byteBuffer = THREAD_BUFFER.get();
        byteBuffer.clear();

        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();

        socketChannel.write(byteBuffer);
    }
}

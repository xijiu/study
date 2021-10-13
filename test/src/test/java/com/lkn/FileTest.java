package com.lkn;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class FileTest {

    @Test
    public void test() throws Exception {
        File file = new File("/Users/likangning/test/index.data");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        FileChannel fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE);
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(0);
        ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(100);
        byteBuffer2.putLong(100);
        byteBuffer2.putLong(200);
        byteBuffer2.putLong(300);
        byteBuffer2.putLong(400);
        byteBuffer2.flip();
        ByteBuffer[] byteBuffers = {byteBuffer1, byteBuffer2};
        long write = fileChannel.write(byteBuffers);
        System.out.println(write);
        fileChannel.force(true);
        fileChannel.close();
        System.out.println(Arrays.toString(byteBuffers));
    }
}

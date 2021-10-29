package com.lkn;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class FileTest {

    @Test
    public void test() throws Exception {
        File file = new File("/Users/likangning/test/index.data");
        System.out.println(file.length());
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        System.out.println(randomAccessFile.length());
//        randomAccessFile.setLength(1L * 1024 * 1024 * 1024);

        MappedByteBuffer mappedByteBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 10 * 1024 * 1024);

        System.out.println("position is " + mappedByteBuffer.position());
        System.out.println("limit is " + mappedByteBuffer.limit());
        randomAccessFile.close();
    }
}

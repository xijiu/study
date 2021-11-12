package file_test;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * @author xijiu
 * @since 2021/11/11 下午4:18
 */
public class FileWriteCompare {

    private static String filePath = "/Users/likangning/test/index3.data";

    private static int fileSize = 1024 * 1024 * 1024;

    private static boolean warmFile = true;

    private static int batchSize = 4096 * 1000;

    @Test
    public void test() throws Exception {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        FileChannel fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.READ);
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);

        warmFile(mappedByteBuffer);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(batchSize);
        long beginTime = System.currentTimeMillis();
        mappedByteBuffer.position(0);
        while (mappedByteBuffer.remaining() >= batchSize) {
            byteBuffer.position(batchSize);
            byteBuffer.flip();
            mappedByteBuffer.put(byteBuffer);
        }
        System.out.println("time cost is : " + (System.currentTimeMillis() - beginTime));
    }

    private void warmFile(MappedByteBuffer mappedByteBuffer) {
        if (!warmFile) {
            return;
        }
        int pageSize = 4096;
        long begin = System.currentTimeMillis();
        for (int i = 0, j = 0; i < fileSize; i += pageSize, j++) {
            mappedByteBuffer.put(i, (byte) 0);
        }
        System.out.println("warm file time cost " + (System.currentTimeMillis() - begin));
    }
}

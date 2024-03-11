import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileTest {

    public static void main(String[] args) throws Exception {
        long begin = System.currentTimeMillis();
        String cmd = "du -sm /opt/rocketmq/store";
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        InputStream inputStream = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null) {
            break;
        }
        br.close();
        long cost = System.currentTimeMillis() - begin;

        assert line != null;

        String[] str = line.split("\t");
        for (String s : str) {
            System.out.println(":::" + s + ":::");
        }
    }


    private void test() throws Exception {
        long begin = System.currentTimeMillis();
        Process process = Runtime.getRuntime().exec("du -sh /opt/rocketmq/store");
        InputStream inputStream = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();
        process.waitFor();

        System.out.println("time cost : " + (System.currentTimeMillis() - begin));
    }


    public static long getUsedDiskSpaceByPath(String path) {
        if (null == path || path.isEmpty()) {
            return 0;
        }

        try {
            File file = new File(path);
            if (!file.exists()) {
                return -1;
            }

            File[] files = file.listFiles();
            for (File fileTmp : files) {
                long usedDiskSpaceByFile = getUsedDiskSpaceByFile(fileTmp);
                System.out.println(fileTmp.getPath() + " ::: " + (usedDiskSpaceByFile / 1024 / 1024));
            }

            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public static long getUsedDiskSpaceByFile(File file) {
        long fileTotalSize = 0L;

        if (file.isFile()) {
            fileTotalSize = file.length();
            if (file.length() > 50 * 1024 * 1024) {
                System.out.println(file.getPath() + " ::: " + (file.length() / 1024 / 1024) + " MB");
            }
        } else {
            File[] files = file.listFiles();
            if (files != null && file.length() > 0) {
                for (File fileTmp : files) {
                    fileTotalSize += getUsedDiskSpaceByFile(fileTmp);
                }
            }
        }
        return fileTotalSize;
    }

    /**
     * 查询磁盘的总存储量，在取逻辑上限与真实大小，二者中取最小值
     *
     * @param file  目标哦文件
     * @param logicLimitGi    逻辑上限，如果为-1，则表明逻辑不设限
     * @return  结果
     */
    public static long queryDiskTotalSpace(File file, long logicLimitGi) {
        long actualTotalSpace = file.getTotalSpace();
        if (logicLimitGi == -1) {
            return actualTotalSpace;
        }
        long logicLimitByte = logicLimitGi * 1024 * 1024 * 1024;
        return Math.min(actualTotalSpace, logicLimitByte);
    }
}

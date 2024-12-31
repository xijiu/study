package junit_test;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xijiu
 * @since 2022/10/12 下午5:09
 */
public class Test3 {

    private static final LoadingCache<String, String> allQuotaInfoCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    System.out.println("load key " + key);
                    return "";
                }
            });

    @Before
    public void before() {
//        System.out.println("test_3 start");
    }

    @After
    public void after() {
//        System.out.println("test_3 over");
    }

    private static enum A {
        AAA,
        BBB
    }

    @Test
    public void test() throws InterruptedException {
        allQuotaInfoCache.put("a", "a1");
        System.out.println(allQuotaInfoCache.getUnchecked("a"));
        Thread.sleep(5500);
        System.out.println(allQuotaInfoCache.getUnchecked("a"));
    }


    @Test
    public void en() {
        int p = 3;
        int q = 5;
        int n = p * q;
        int z = (p - 1) * (q -1);
        int e = 7;
        int d = 15;

        int source = 13;
        System.out.println("原明文是： (" + source + ")");
        double pow = Math.pow(source, e);
        long result = (long) Math.abs(pow);
        System.out.println("加密计算的中间值：" + result);
        result = result % n;
        System.out.println("=======================================> 加密后的密文： " + result);

        // 以下开始解密
        double pow2 = Math.pow(result, d);
        long result2 = (long) Math.abs(pow2);
        System.out.println("解密计算的中间值：" + result2);
        result2 = result2 % n;
        System.out.println("=======================================> 解密后的明文： " + result2);
    }


    @Test
    public void en2() {
        int p = 3;
        int q = 5;
        int n = p * q;
        int z = (p - 1) * (q -1);
        int e = 3;  //  (e < n，与z互质，随便取一个小的)
        int d = 11; // 当e确定后，d的取值可能会有多个，随便选择一个即可

        int source = 12;
        System.out.println("原明文是： (" + source + ")");
        double pow = Math.pow(source, e);
        long result = (long) Math.abs(pow);
        System.out.println("加密计算的中间值：" + result);
        result = result % n;
        System.out.println("=======================================> 加密后的密文： " + result);

        // 以下开始解密
        double pow2 = Math.pow(result, d);
        long result2 = (long) Math.abs(pow2);
        System.out.println("解密计算的中间值：" + result2);
        result2 = result2 % n;
        System.out.println("=======================================> 解密后的明文： " + result2);
    }


    @Test
    public void encrypt() {
        String str = "Attack tomorrow morning";
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (chars[i] + 1);
        }
        System.out.println(new String(chars));
    }

    @Test
    public void decrypt() {
        String str = "Buubdl!upnpsspx!npsojoh";
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (chars[i] - 1);
        }
        System.out.println(new String(chars));
    }


    @Test
    public void encrypt2() {
        String str = "Attack tomorrow morning";
        byte[] bytes = str.getBytes();
        BigDecimal sum = new BigDecimal(0);
        for (byte aByte : bytes) {
            System.out.println(aByte);
            sum = sum.multiply(BigDecimal.valueOf(10)).add(BigDecimal.valueOf(aByte));
        }
        System.out.println(sum);

        BigDecimal bigDecimal1 = BigDecimal.valueOf(1125899839733759L);
        BigDecimal bigDecimal2 = BigDecimal.valueOf(18014398241046527L);
        System.out.println(bigDecimal1.multiply(bigDecimal2).toString());
    }





    @Test
    public void enBig() {
        BigInteger p = new BigInteger("1125899839733759");
        BigInteger q = new BigInteger("18014398241046527");
        BigInteger n = p.multiply(q);
        BigInteger z = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));
        BigInteger e = z.subtract(BigInteger.valueOf(1));
        BigInteger x = new BigInteger("20282408092494375639463130824708").add(new BigInteger("20282408092494375639463130824707"));

        System.out.println("p = " + p.toString());
        System.out.println("q = " + q.toString());
        System.out.println("n = " + n.toString());
        System.out.println("e = " + e.toString());
        System.out.println("z = " + z.toString());
        System.out.println("x = " + x.toString());

        String str = "attack 9:00";
        byte[] bytes = str.getBytes();
        BigInteger source = new BigInteger(1, bytes);

        System.out.println("原明文字符串是： " + str);
        System.out.println("原明文转换为10进制后的数字为： " + source.toString());

        BigInteger c = source.modPow(e, n);
        System.out.println("=======================================> 加密后的密文： " + c);

        System.out.println("开始解密");
        BigInteger newSource = c.modPow(x, n);
        System.out.println("解密完成");
        String newSourceStr = new String(newSource.toByteArray(), StandardCharsets.UTF_8);
        System.out.println("=======================================> 解密后的10进制数： " + newSource);
        System.out.println("=======================================> 解密后的字符串为： " + newSourceStr);
    }

    @Test
    public void test6() {
        String str1 = "jackson-annotations-2.16.2\n" +
                "jackson-core-2.16.2\n" +
                "jackson-databind-2.16.2\n" +
                "jackson-dataformat-csv-2.16.2\n" +
                "jackson-dataformat-yaml-2.16.2\n" +
                "jackson-datatype-jdk8-2.16.2\n" +
                "jackson-jaxrs-base-2.16.2\n" +
                "jackson-jaxrs-json-provider-2.16.2\n" +
                "jackson-module-blackbird-2.16.2\n" +
                "jackson-module-jaxb-annotations-2.16.2\n" +
                "jackson-module-scala_2.13-2.16.2";

        String str2 = "jackson-jakarta-rs-base-2.16.2\n" +
                "jackson-jakarta-rs-json-provider-2.16.2\n" +
                "jackson-module-jakarta-xmlbind-annotations-2.16.2";
        sort(str1, str2);
    }

    private void sort(String str1, String str2) {
        String[] split1 = str1.split("\n");
        List<String> list = new ArrayList<>(Arrays.asList(split1));
        String[] split2 = str2.split("\n");
        list.addAll(Arrays.asList(split2));
        Collections.sort(list);

        for (String ele : list) {
            System.out.println(ele.trim());
        }
    }
}

package junit_test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author xijiu
 * @since 2022/10/12 下午5:09
 */
public class Test1 {

    @Before
    public void before() {
        System.out.println("test_1 start");
    }

    @After
    public void after() {
        System.out.println("test_1 over");
    }

    @Test
    public void test() {
        System.out.println("I am 1");
    }
}

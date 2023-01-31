package junit_test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author xijiu
 * @since 2022/10/12 下午5:09
 */
public class Test2 {

    @Before
    public void before() {
        System.out.println("test_2 start");
    }

    @After
    public void after() {
        System.out.println("test_2 over");
    }

    @Test
    public void test() {
        System.out.println("I am 2");
    }
}

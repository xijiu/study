package post_consutruct;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author xijiu
 * @since 2022/3/17 下午4:31
 */
public class User {

    public User() {
        System.out.println("我是构造方法");
    }

    @PostConstruct
    public void init() {
        System.out.println("我是@PostConstruct");
    }

    public void test() {
        System.out.println("我是test方法");
    }
}

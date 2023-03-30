package stat;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author xijiu
 * @since 2023/3/2 下午3:22
 */
public final class InterceptTest {

    @Test
    public void test() throws Exception {
        // 创建类
        ClassPool pool = ClassPool.getDefault();
        CtMethod ctMethod = pool.getMethod(Files.class.getName(), "delete");
        ctMethod.setBody("stat.InterceptTest.deleteFile(path);");

        Files.delete(Paths.get(new File("/alidata11/0000000000.log").getPath()));
    }

    public static void deleteFile(Path path) {
        System.out.println("我是被代理的方法, path is " + path);
    }


    @Test
    public void test2() throws Exception {
//        InterceptTest.deleteFile(Paths.get("aaaa"));
        // 创建类
        ClassPool pool = ClassPool.getDefault();
        CtMethod ctMethod = pool.getMethod("stat.InterceptTest", "deleteFile");
        ctMethod.setBody("System.out.println(\"嘻嘻嘻，方法内容已经发生了变化 \");");
//        InterceptTest.deleteFile(Paths.get("aaaa"));
    }

    @Test
    public void test3() throws Exception {
        // 创建类
        ClassPool pool = ClassPool.getDefault();
        CtMethod ctMethod = pool.getMethod(Files.class.getName(), "delete");
    }
}

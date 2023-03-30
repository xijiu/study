package stat;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author xijiu
 * @since 2023/3/2 下午3:22
 */
public class InterceptTest3 {

    @Test
    public void test() throws Exception {
        magic();
        Files.delete(Paths.get("aaaa"));
    }

    private void magic() throws Exception {
        // 创建类
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("java.nio.file.Files");
        CtMethod ctMethod = cc.getDeclaredMethod("delete");
        ctMethod.setBody("System.out.println(\"嘻嘻嘻，方法内容已经发生了变化!!!!!!!! \");");
//        cc.detach();
        cc.toClass();
    }

}

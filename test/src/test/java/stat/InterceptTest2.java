package stat;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author xijiu
 * @since 2023/3/2 下午3:22
 */
public class InterceptTest2 {

    @Test
    public void test2() throws Exception {
        magic();
        InterceptTest.deleteFile(Paths.get("aaaa"));
    }
























    private void magic() throws Exception {
        // 创建类
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("stat.InterceptTest");
        CtMethod ctMethod = cc.getDeclaredMethod("deleteFile");
        ctMethod.setBody("System.out.println(\"嘻嘻嘻，方法内容已经发生了变化!!!!!!!! \");");
//        cc.detach();
        cc.toClass();
    }

}

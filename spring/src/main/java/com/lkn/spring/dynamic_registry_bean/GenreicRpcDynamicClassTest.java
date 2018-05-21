package com.lkn.spring.dynamic_registry_bean;

import com.lkn.spring.SpringContextUtils;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;

/**
 * 心得：此测试类代码不到区区100行，但可预见提供了非常强悍的灵活扩展功能，不能不惊叹JavaCompile及Spring的强大啊
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:com/lkn/spring/dynamic_registry_bean/test-service.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class GenreicRpcDynamicClassTest {

  /**
   * 动态生成一个java类交由spring管理
   * 并通过反射调用来实现spring相关功能
   */
  @Test
  public void dynamicRpc() throws Exception {
    Class<?> genericClass = genericSpringClass();
    String beanName = "dynamicClass";

    // 将某个类动态交由spring进行管理
    ApplicationContext applicationContext = SpringContextUtils.getApplicationContext();
    BeanDefinitionRegistry beanDefReg = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    BeanDefinitionBuilder beanDefBuilder = BeanDefinitionBuilder.genericBeanDefinition(genericClass);
    BeanDefinition beanDef = beanDefBuilder.getBeanDefinition();
    if (!beanDefReg.containsBeanDefinition(beanName)) {
      beanDefReg.registerBeanDefinition(beanName, beanDef);
    }

    Object springBean = applicationContext.getBean(genericClass);
    Method method = genericClass.getDeclaredMethod("getName", Long.class);
    Object result = method.invoke(springBean, 20L);
    System.out.println("结果：" + result);
  }

  /**
   * 通过javaassist方式生成自带注解的spring类
   */
  private Class<?> genericSpringClass() throws Exception {
    String className = "com.lkn.spring.dynamic_registry_bean.DynamicClass";
    // 创建类
    ClassPool pool = ClassPool.getDefault();
    CtClass cls = pool.makeClass(className);
    ConstPool constpool = cls.getClassFile().getConstPool();

    CtField ctField = new CtField(pool.get("com.lkn.spring.dynamic_registry_bean.UserService"), "userService", cls);
    ctField.setModifiers(Modifier.PRIVATE);  //私有修饰

    // 属性附上注解
    Annotation referServiceAnno = new Annotation("org.springframework.beans.factory.annotation.Autowired", constpool);
    referServiceAnno.addMemberValue("required", new BooleanMemberValue(true, constpool));
    AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
    fieldAttr.addAnnotation(referServiceAnno);
    FieldInfo fieldInfo = ctField.getFieldInfo();
    fieldInfo.addAttribute(fieldAttr);

    cls.addField(ctField); //写入class文件
    CtMethod m1 = CtMethod.make("public String getName(Long id){return userService.getNameById(id);}", cls);
    cls.addMethod(m1);
    return cls.toClass();
  }

}

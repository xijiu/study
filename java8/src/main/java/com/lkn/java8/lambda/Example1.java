package com.lkn.java8.lambda;

import org.junit.Test;

/**
 * lambda表达式在新建模板时的应用
 * 注：如果接口{@link Template}中包含了多个方法，那么此时无法使用语法胶囊
 * @author likangning
 * @since 2018/5/21 上午10:00
 */
public class Example1 {

	@Test
	public void test() {
		exe(name -> System.out.println("你好： " + name), "张三");
		exe(System.out::println, "张三1");
		exe(this::myPrint, "张三2");
		exe2(this::myPrint2, 1L, "张三2");
	}

	private void exe(Template template, String name) {
		template.hello(name);
	}

	private void exe2(Template2 template2, Long id, String name) {
		template2.hello(id, name);
	}

	@FunctionalInterface
	interface Template {
		void hello(String name);
	}

	@FunctionalInterface
	interface Template2 {
		void hello(Long id, String name);
	}

	public void myPrint(String name) {
		System.out.println("输出姓名： " + name);
	}

	public void myPrint2(Long id, String name) {
		System.out.println(id + " 输出姓名： " + name);
	}
}




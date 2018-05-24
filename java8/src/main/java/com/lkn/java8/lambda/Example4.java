package com.lkn.java8.lambda;

import org.junit.Test;

/**
 * @author likangning
 * @since 2018/5/22 上午9:51
 */
public class Example4 {

	@Test
	public void test() {
		// 方式1
		doExe(new Template() {
			@Override
			public void exe(User user, String command) {
				System.out.println("do");
			}
		}, "lisi");

		// 方式2
		doExe((user, command) -> System.out.println("do"), "lisi");

		// 方式3
		doExe(this::innerSayHello, "lisi");

		// 方式4
		doExe((user, command) -> user.sayHello(command), "lisi");

		// 方式5
		doExe(User::sayHello, "lisi");
	}

	private void doExe(Template template, String command) {
		template.exe(new User(), command);
	}

	private void innerSayHello(User user, String command) {
		System.out.println("我是innerSayHello");
	}




	@FunctionalInterface
	private interface Template {
		void exe(User user, String command);
	}

	private class User {
		public void sayHello(String command) {
			System.out.println("你好： " + command);
		}
	}
}


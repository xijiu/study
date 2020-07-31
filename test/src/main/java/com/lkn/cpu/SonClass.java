package com.lkn.cpu;

import org.junit.Test;

/**
 * @author likangning
 * @since 2020/6/23 下午5:03
 */
public class SonClass extends MyClass {

	@Override
	public void aaaa() {
		System.out.println("我是son");
	}

	@Test
	public void abc() {
		MyClass myClass = new MyClass();
		myClass.aaaa();

		SonClass sonClass = (SonClass) myClass;
		sonClass.aaaa();
	}
}

package com.lkn.java8.lambda;

import org.junit.Test;

/**
 * @author likangning
 * @since 2018/5/22 上午9:10
 */


public class Example3 {

	@Test
	public void test() {
		LambdaInterface lt4 = LambdaClassTest::add;
		LambdaClassTest lct = new LambdaClassTest();
		System.out.println(lt4.add(lct, 5, 8));
	}

	private class LambdaClassTest {

		public int add(int a, int b){
			System.out.println("LambdaClassTest类的add方法");
			return a+b;
		}
	}

	private interface LambdaInterface {
		int add(LambdaClassTest lt, int a, int b);
	}
}

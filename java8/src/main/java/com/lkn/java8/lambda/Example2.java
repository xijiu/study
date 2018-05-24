package com.lkn.java8.lambda;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author likangning
 * @since 2018/5/21 上午10:00
 */
public class Example2 {

	@Test
	public void test1() {

		Runnable aNew = Example2::new;
		Arrays.asList( "a", "b", "d" ).forEach(e -> {
			System.out.print("element内容：");
			System.out.println(e);
		});
	}

	@Test
	public void convertTest() {
		List<String> collected = new ArrayList<>();
		collected.add("alpha");
		collected.add("beta");
		collected = collected.stream().map(string -> string.toUpperCase()).collect(Collectors.toList());
		System.out.println(collected);
	}

}




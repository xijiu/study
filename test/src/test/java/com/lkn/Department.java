package com.lkn;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * @author likangning
 * @since 2020/5/23 上午8:48
 */
public class Department {

	private long beginTime;

	private Long id;

	private String desc;

	@Before
	public void before() {
		beginTime = System.currentTimeMillis();
	}

	@After
	public void after() {
		System.out.println("耗时： " + (System.currentTimeMillis() - beginTime));
	}

	@Test
	public void list() {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < 5000000; i++) {
			list.add(String.valueOf(i));
		}
	}

	@Test
	public void set() {
		Map<String, Boolean> map = new HashMap<>();
		map.put("abc", true);
		if (map.get("ccc")) {
			System.out.println(123);
		} else
			System.out.println(333);
	}
}

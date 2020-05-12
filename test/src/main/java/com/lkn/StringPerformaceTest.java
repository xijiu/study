package com.lkn;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author likangning
 * @since 2020/5/12 上午8:51
 */
public class StringPerformaceTest {

	private long beginTime;

	private int exeTime = 10000000;
//	private int exeTime = 1;

	private String targetStr = "d614959183521b4b|1587457762873000|d614959183521b4b|0|311601|order|getOrder|192.168.1.3|http.status_code:200";


	@Test
	public void test() {
		for (int i = 0; i < exeTime; i++) {
			String[] split = targetStr.split("\\|");
		}
		System.out.print("全部切割 ");
	}


	@Test
	public void test2() {
		for (int i = 0; i < exeTime; i++) {
			int index = targetStr.lastIndexOf("|");
			String last = targetStr.substring(index + 1, targetStr.length());
			int firstIndex = targetStr.indexOf("|");
			String first = targetStr.substring(0, firstIndex);
			int startTimeIndex = targetStr.indexOf("|", 1);
			String startTime = targetStr.substring(startTimeIndex + 1, startTimeIndex + 17);
//			System.out.println(first + " : " + last + " : " + startTime);
		}
		System.out.print("切割最后 ");
	}

	@Before
	public void before() {
		beginTime = System.currentTimeMillis();
	}

	@After
	public void after() {
		System.out.println("耗时： " + (System.currentTimeMillis() - beginTime));
	}
}

package com.lkn;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author likangning
 * @since 2020/5/12 上午8:51
 */
public class StringPerformaceTest {

	private long beginTime;

	private int exeTime = 1;
//	private int exeTime = 1;

	private String targetStr = "1d37a8b17db8568b|1589285985482059|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|Frontend|sls.getOperator|192.168.0.15|http.status_code=200&http.url=http://localhost:9003/getAddress&component=java-web-servlet&span.kind=server&http.method=GET";


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
			System.out.println(first + " : " + last + " : " + startTime);
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

	@Test
	public void aaa() {
		List<String> list = Lists.newArrayList();
		list.add("1d37a8b17db8568b|1589285985482059|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");
		list.add("1d37a8b17db8568b|1589285985482051|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");
		list.add("1d37a8b17db8568b|1589285985482052|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");
		list.add("1d37a8b17db8568b|1589285985482066|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");
		list.add("1d37a8b17db8568b|1589285985482060|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");
		list.add("1d37a8b17db8568b|1589285985482044|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");
		list.add("1d37a8b17db8568b|1589285985482041|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");
		list.add("1d37a8b17db8568b|1589285985482049|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");
		list.add("1d37a8b17db8568b|1589285985482061|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");
		list.add("1d37a8b17db8568b|1589285985482028|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");
		list.add("1d37a8b17db8568b|1589285985482021|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");
		list.add("1d37a8b17db8568b|1589285985482010|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207|5d4ff1bb7d66b522|3d1e7e1147c1895d|1207");

		String result = list.stream().sorted(Comparator.comparing((str) -> {
			int startTimeIndex1 = str.indexOf("|", 1);
			String startTime = str.substring(startTimeIndex1 + 1, startTimeIndex1 + 17);
			return Long.parseLong(startTime);
		})).collect(Collectors.joining("\n"));

//		String result = list.stream().sorted(Comparator.comparing((str) -> {
//			String[] cols = str.split("\\|");
//			return Long.parseLong(cols[1]);
//		})).collect(Collectors.joining("\n"));

//		System.out.println(result);
	}

}

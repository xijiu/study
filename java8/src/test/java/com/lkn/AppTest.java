package com.lkn;

import static org.junit.Assert.assertTrue;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest {

	private Map<String, Long> map;
	/**
	 * Rigorous Test :-)
	 */
	@Test
	public void shouldAnswerWithTrue() {
		Map<String, Long> temp = Maps.newHashMap();
		temp.put("a", 1L);
		temp.put("b", 2L);
		temp.put("c", 3L);
		map = temp;
		print(temp);

		map.clear();
		print(temp);
	}

	private void print(Map<String, Long> temp) {
		System.out.println("map size : " + map.size());
		System.out.println("temp size : " + temp.size());
	}
}

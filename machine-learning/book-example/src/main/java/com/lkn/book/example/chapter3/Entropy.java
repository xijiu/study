package com.lkn.book.example.chapter3;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

/**
 * 熵的计算
 * @author likangning
 * @since 2018/8/27 下午3:31
 */
public class Entropy {

	@Test
	public void calc() {
		Object[][] data = prepareData();
		int dataLength = data.length;
		String[] labels = {"no surfacing", "flippers"};
		Map<String, Integer> resultNumberMap = calcResultNumber(data);
		double shannoEnt = caclShannonEnt(resultNumberMap, dataLength);
		System.out.println("最终的熵为：" + shannoEnt);
	}


	private double caclShannonEnt(Map<String, Integer> resultNumberMap, int dataLength) {
		double shannoEnt = 0D;
		for (Map.Entry<String, Integer> entry : resultNumberMap.entrySet()) {
			double prob = (double) entry.getValue() / (double) dataLength;
			shannoEnt = shannoEnt - (prob * Math.log(prob) / Math.log(2));
		}
		return shannoEnt;
	}

	private Map<String, Integer> calcResultNumber(Object[][] data) {
		Map<String, Integer> resultMap = Maps.newHashMap();
		for (int i = 0; i < data.length; i++) {
			String result = data[i][data[i].length - 1].toString();
			if (!resultMap.containsKey(result)) {
				resultMap.put(result, 0);
			}
			resultMap.put(result, resultMap.get(result) + 1);
		}
		return resultMap;
	}

	private Object[][] prepareData() {
		return new Object[][]{
				{1, 1, "yes"},
				{1, 1, "yes"},
				{1, 0, "no"},
				{0, 1, "no"},
				{0, 1, "no"}
		};
	}
}

package com.lkn.test2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author likangning
 * @since 2020/6/11 下午4:04
 */
public class BatchBean {

	public int batchNum = 0;

	public List<byte[]> cacheByteArrList = new ArrayList<>(2000);

	public int[] byteArrIndex = new int[2000];

	public int cacheByteArrIndex = 0;

	private Map<Long, Integer> traceIdAndDataMap = new ConcurrentHashMap<>();

	public BatchBean() {
		for (int i = 0; i < 2000; i++) {
			cacheByteArrList.add(new byte[18000]);
		}
	}

	public void storeLineByteArr(long traceId, byte[] lineArr, int beginIndex, int length) {
		Integer index = traceIdAndDataMap.get(traceId);
		if (index == null) {
			traceIdAndDataMap.put(traceId, cacheByteArrIndex);
			byte[] bytes = cacheByteArrList.get(cacheByteArrIndex);
			System.arraycopy(lineArr, beginIndex, bytes, byteArrIndex[cacheByteArrIndex], length);
			byteArrIndex[cacheByteArrIndex] += length;
			cacheByteArrIndex++;
		} else {
			byte[] bytes = cacheByteArrList.get(index);
			System.arraycopy(lineArr, beginIndex, bytes, byteArrIndex[index], length);
			byteArrIndex[index] += length;
		}
	}

	public byte[] getDataByTraceId(long traceId) {
		Integer index = traceIdAndDataMap.get(traceId);
		if (index != null) {
			return cacheByteArrList.get(index);
		}
		return null;
	}


	public void reset() {
		batchNum = 0;
		cacheByteArrIndex = 0;
		traceIdAndDataMap.clear();
		for (int i = 0; i < byteArrIndex.length; i++) {
			byteArrIndex[i] = 0;
		}
	}

}

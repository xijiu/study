package com.lkn.test2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author likangning
 * @since 2020/6/11 下午4:04
 */
public class BatchBean {

	public int batchNum = 0;

//	public List<byte[]> cacheByteArrList = new ArrayList<>(2000);

	public int[] byteArrIndex = new int[2000];

	public int cacheByteArrIndex = 0;

	private Map<Long, byte[]> traceIdAndDataMap = new HashMap<>();

	public BatchBean() {
//		for (int i = 0; i < 2000; i++) {
//			cacheByteArrList.add(new byte[18000]);
//		}
	}

	public void storeLineByteArr(long traceId, byte[] lineArr, int beginIndex, int length) {
		byte[] byteArr = traceIdAndDataMap.get(traceId);
		if (byteArr == null) {
			byte[] bytes = new byte[18000];
			traceIdAndDataMap.put(traceId, bytes);
			System.arraycopy(lineArr, beginIndex, bytes, 0, length);
//			byteArrIndex[cacheByteArrIndex] += length;
//			cacheByteArrIndex++;
		} else {
			System.arraycopy(lineArr, beginIndex, byteArr, 0, length);
		}
	}

	public byte[] getDataByTraceId(long traceId) {
		return traceIdAndDataMap.get(traceId);
//		Integer index = traceIdAndDataMap.get(traceId);
//		if (index != null) {
//			return cacheByteArrList.get(index);
//		}
//		return null;
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

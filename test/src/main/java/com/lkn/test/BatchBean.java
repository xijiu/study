package com.lkn.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author likangning
 * @since 2020/6/9 下午7:20
 */
public class BatchBean {

	public byte[] blockArr1;

	public byte[] blockArr2;

	public int blockGapLineNum;

	public int batchNum;

	public int[][] lineArr = new int[Constants.BATCH_SIZE][2];

	public int[] lineNextPosArr = new int[Constants.BATCH_SIZE];

	public byte[] gapLine = null;

	public Map<Long, Integer> traceIdAndIndexMap = new ConcurrentHashMap<>(1024);

	public BatchBean() {
		reset();
	}

	public void reset() {
		blockArr1 = null;
		blockArr2 = null;
		batchNum = -1;
		blockGapLineNum = -1;
		traceIdAndIndexMap.clear();
	}

}

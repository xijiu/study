package com.lkn.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Semaphore;


public class ClientProcessData1 {

    private static int BATCH_COUNT = 30;

    private static int BLOCK_COUNT = 30;

    private static BatchBean[] batchBeanArr = new BatchBean[BATCH_COUNT];

    private static List<byte[]> BLOCK_ARR_LIST = new ArrayList<>(BLOCK_COUNT);

    public static int CURRENT_CLIENT_PORT = -1;

    private static Msg msg = new Msg();

    public static volatile int CURRENT_BATCH_POS = 0;

    public static volatile int PROCESSED_BATCH_POS = 0;

    public static volatile boolean FINISH = false;

    private static Semaphore[] semaphores = new Semaphore[BATCH_COUNT];

    @Test
    public void testRun() throws Exception {
        init();
        long begin = System.currentTimeMillis();
        doRun();
        System.out.println("cost is : " + (System.currentTimeMillis() - begin));
    }

    public static void init() {
        msg.setPort(CURRENT_CLIENT_PORT);
        msg.setMsgType(1);
        CURRENT_CLIENT_PORT = 8001;
        for (int i = 0; i < BATCH_COUNT; i++) {
            semaphores[i] = new Semaphore(1);
            batchBeanArr[i] = new BatchBean();
        }
        for (int i = 0; i < BLOCK_COUNT; i++) {
            BLOCK_ARR_LIST.add(new byte[1024 * 1024 * 10]);
        }
    }


    private void doRun() throws Exception {
        InputStream input = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");
        int streamBlockNum = 0;
        byte[] data = BLOCK_ARR_LIST.get(0);
        int byteNum;
        int count = 0;
        int pos = 0;
        int lineNum = 0;
        BatchBean currentBatchBean = batchBeanArr[0];
        Set<Long> badTraceIdList = new HashSet<>(1000);
        byte[] lastByteArr = null;
        semaphores[pos].acquire();
        while ((byteNum = input.read(data)) != -1) {
            if (currentBatchBean.blockArr1 == null) {
                currentBatchBean.blockArr1 = data;
            } else {
                currentBatchBean.blockGapLineNum = lineNum;
                currentBatchBean.blockArr2 = data;
            }
            int beginPos = 0;
            int beginIndex = 0;
            while (true) {
                int i;
                for (i = beginPos; i < byteNum; i++) {
                    if (data[i] == 10) {
                        if (lastByteArr != null) {
                            byte[] gapByteArr = new byte[lastByteArr.length + i - beginIndex];
                            System.arraycopy(lastByteArr, 0, gapByteArr, 0, lastByteArr.length);
                            System.arraycopy(data, beginIndex, gapByteArr, lastByteArr.length, i - beginIndex);
                            currentBatchBean.gapLine = gapByteArr;
                            if (isBadTraceData(gapByteArr, gapByteArr.length - 1)) {
                                long traceId = longFrom8Bytes(gapByteArr, 0);
                                badTraceIdList.add(traceId);
                            }
                            lastByteArr = null;
                        } else {
                            long traceId = longFrom8Bytes(data, beginIndex);
                            Integer beginPosIndex = currentBatchBean.traceIdAndIndexMap.get(traceId);
                            currentBatchBean.lineArr[lineNum][0] = beginIndex;
                            currentBatchBean.lineArr[lineNum][1] = i - beginIndex;
                            if (beginPosIndex == null) {
                                currentBatchBean.lineNextPosArr[lineNum] = -1;
                                currentBatchBean.traceIdAndIndexMap.put(traceId, lineNum);
                            } else {
                                currentBatchBean.lineNextPosArr[lineNum] = beginPosIndex;
                                currentBatchBean.traceIdAndIndexMap.put(traceId, lineNum);
                            }

                            if (isBadTraceData(data, i - 1)) {
                                badTraceIdList.add(traceId);
                            }
                        }
                        beginIndex = i + 1;
                        count++;
                        lineNum++;

                        if (count % Constants.BATCH_SIZE == 0) {
                            pos++;
                            if (pos >= BATCH_COUNT) {
                                pos = 0;
                            }
                            int batchPos = count / Constants.BATCH_SIZE - 1;
//                            updateWrongTraceId(badTraceIdList, batchPos);
                            badTraceIdList.clear();
                            CURRENT_BATCH_POS = batchPos;
                            currentBatchBean = batchBeanArr[pos];
                            currentBatchBean.blockArr1 = data;
                            lineNum = 0;
                        }
                        beginPos = i + 100;
                        break;
                    }
                }
                if (i >= byteNum) {
                    break;
                }
            }
            if (beginIndex < byteNum) {
                int length = byteNum - beginIndex;
                lastByteArr = new byte[length];
                System.arraycopy(data, beginIndex, lastByteArr, 0, length);
            }

            data = BLOCK_ARR_LIST.get(streamBlockNum++ % BLOCK_COUNT);
        }
        if (count % Constants.BATCH_SIZE != 0) {
            CURRENT_BATCH_POS = CURRENT_BATCH_POS + 1;
        }

        FINISH = true;
        callFinish();
        input.close();
        System.out.println("count num is " + count);
    }

    private boolean isBadTraceData(byte[] lineByteArr, int endIndex) {
        if (lineByteArr.length < 20) {
            return false;
        }
        int i = endIndex;
        // error=1
        if (lineByteArr[i--] == 49) {
            if (lineByteArr[i--] == 61) {
                if (lineByteArr[i--] == 114) {
                    return true;
                }
            }
        }
        i = endIndex - 3;

        // http.status_code=
        if (lineByteArr[i--] == 61) {
            if (lineByteArr[i--] == 101) {
                return true;
            }
        }
        return false;
    }

    public static long longFrom8Bytes(byte[] input, int offset) {
        long value = 0;
        // 循环读取每个字节通过移位运算完成long的8个字节拼装
        for (int count = 0; count < 8; ++count) {
            int shift = count << 3;
            value |= ((long) 0xff << shift) & ((long) input[offset + count] << shift);
        }
        return value;
    }

    /**
     * call backend controller to update wrong tradeId list.
     * @param badTraceIdList
     * @param batchPos
     */
    private void updateWrongTraceId(Set<Long> badTraceIdList, int batchPos) {
        // 不论是否有badTrace数据，都要向server上报
        msg.setBatchPos(batchPos);
        msg.setMsgBody(JSON.toJSONString(badTraceIdList));
        String json = JSON.toJSONString(msg);
//        EchoClient.sendMsg(json);
//        LOGGER.info("suc to updateBadTraceId, batchPos:" + batchPos);
    }

    // notify backend process when client process has finished.
    private void callFinish() {
        Msg msg = new Msg();
        msg.setPort(CURRENT_CLIENT_PORT);
        msg.setMsgType(4);
        msg.setMsgBody(String.valueOf(CURRENT_BATCH_POS));
//        EchoClient.syncSendMsg(JSON.toJSONString(msg));
    }

}

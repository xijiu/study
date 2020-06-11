package com.lkn.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Semaphore;


public class ClientProcessData {

    private static List<Map<Long, byte[]>> BATCH_TRACE_LIST = new ArrayList<>();

    private static int BATCH_COUNT = 15;

    public static int CURRENT_CLIENT_PORT = -1;

    private static Msg msg = new Msg();

    public static volatile int CURRENT_BATCH_POS = 0;

    private static volatile boolean FINISH = false;

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
            BATCH_TRACE_LIST.add(new ConcurrentHashMap<>());
            semaphores[i] = new Semaphore(1);
        }
    }

    public void doRun() throws Exception {
        InputStream input = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");
        byte[] data = new byte[1024 * 1024 * 2];
        int byteNum;
        byte[] lineByteArr = "abceadsfadsfadfadsfadfadfadfafaf".getBytes();
        int count = 0;
        int pos = 0;
        long[] badTraceIdArr = new long[30];
        int badTraceIdArrIndex = 0;
        long traceId = 0;
        semaphores[pos].acquire();
        Map<Long, byte[]> traceMap = BATCH_TRACE_LIST.get(pos);
        byte[] lastByteArr = null;
        while ((byteNum = input.read(data)) != -1) {
            int beginPos = 0;
            int beginIndex = 0;
            while (true) {
                int i;
                for (i = beginPos; i < byteNum; i++) {
                    if (data[i] == 10) {
                        if (lastByteArr != null) {
                            lineByteArr = new byte[lastByteArr.length + i - beginIndex];
                            System.arraycopy(lastByteArr, 0, lineByteArr, 0, lastByteArr.length);
                            System.arraycopy(data, beginIndex, lineByteArr, lastByteArr.length, i - beginIndex);
                            lastByteArr = null;
                        } else {
                            int length = i - beginIndex;
//                            lineByteArr = new byte[length];
//                            System.arraycopy(data, beginIndex, lineByteArr, 0, length);
                            traceId = longFrom8Bytes(data, beginPos);
                            byte[] spanList = traceMap.get(traceId);
                            if (spanList == null) {
                                spanList = new byte[18000];
                                traceMap.put(traceId, spanList);
                            }
                            System.arraycopy(data, beginIndex, spanList, 0, i - beginIndex);
                        }
                        beginIndex = i + 1;

                        ++count;



                        if (isBadTraceData(data)) {
//                            badTraceIdArr[badTraceIdArrIndex++] = traceId;
                        }

                        if (count % Constants.BATCH_SIZE == 0) {
                            ++pos;
                            if (pos >= BATCH_COUNT) {
                                pos = 0;
                            }

//                            semaphores[pos].acquire();
                            traceMap = BATCH_TRACE_LIST.get(pos);
                            int batchPos = count / Constants.BATCH_SIZE - 1;
                            updateWrongTraceId(badTraceIdArr, batchPos);
                            badTraceIdArrIndex = 0;
                            CURRENT_BATCH_POS = batchPos;
                            BATCH_TRACE_LIST.get(batchPos % BATCH_COUNT).clear();
                        }
                        beginPos = i + 100;
                        break;
                    }
                }
                if (i >= byteNum) {
                    break;
                }
            }
            if (beginIndex <= byteNum) {
                int length = byteNum - beginIndex;
                lastByteArr = new byte[length];
                System.arraycopy(data, beginIndex, lastByteArr, 0, length);
            }
        }
        if (count % 20000 != 0) {
            CURRENT_BATCH_POS = CURRENT_BATCH_POS + 1;
            updateWrongTraceId(badTraceIdArr, CURRENT_BATCH_POS);
        }

        FINISH = true;
        callFinish();
        input.close();
    }

    private boolean isBadTraceData(byte[] lineByteArr) {
        if (lineByteArr.length < 20) {
            return false;
        }
        int i = lineByteArr.length - 1;
        // error=1
        if (lineByteArr[i--] == 49) {
            if (lineByteArr[i--] == 61) {
                if (lineByteArr[i--] == 114) {
                    return true;
                }
            }
        }
        i = lineByteArr.length - 4;

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
     * @param batchPos
     */
    private void updateWrongTraceId(long[] badTraceIdArr, int batchPos) {
        // 不论是否有badTrace数据，都要向server上报
        msg.setBatchPos(batchPos);
        msg.setMsgBody(JSON.toJSONString(badTraceIdArr));
        String json = JSON.toJSONString(msg);
//        EchoClient.sendMsg(json);
    }

    // notify backend process when client process has finished.
    private void callFinish() {
        Msg msg = new Msg();
        msg.setPort(CURRENT_CLIENT_PORT);
        msg.setMsgType(4);
        msg.setMsgBody(String.valueOf(CURRENT_BATCH_POS));
//        EchoClient.syncSendMsg(JSON.toJSONString(msg));
    }


    public static String getWrongTracing(String wrongTraceIdList, int batchPos) throws Exception {
        Set<Long> traceIdSet = JSON.parseObject(wrongTraceIdList, new TypeReference<Set<Long>>(){});
        Map<Long, List<String>> wrongTraceMap = new HashMap<>();
        int pos = batchPos % BATCH_COUNT;
        int previous = pos - 1;
        if (previous == -1) {
            previous = BATCH_COUNT -1;
        }
        int next = pos + 1;
        if (next == BATCH_COUNT) {
            next = 0;
        }
        getWrongTraceWithBatch(previous, traceIdSet, wrongTraceMap);
        getWrongTraceWithBatch(pos, traceIdSet,  wrongTraceMap);
        while (true) {
            if (batchPos + 1 <= CURRENT_BATCH_POS) {
                getWrongTraceWithBatch(next, traceIdSet, wrongTraceMap);
                break;
            } else {
                if (FINISH && batchPos + 1 > CURRENT_BATCH_POS) {
                    break;
                }
                Thread.sleep(1);
            }
        }
        BATCH_TRACE_LIST.get(previous).clear();
        semaphores[previous].release();
        // to clear spans, don't block client process thread. TODO to use lock/notify
        return JSON.toJSONString(wrongTraceMap);
    }

    private static void getWrongTraceWithBatch(int batchPos, Set<Long> traceIdSet, Map<Long, List<String>> wrongTraceMap) {
        Map<Long, byte[]> traceMap = BATCH_TRACE_LIST.get(batchPos);
//        for (Long traceId : traceIdSet) {
//            List<byte[]> spanList = traceMap.get(traceId);
//            if (spanList != null) {
//                // one trace may cross to batch (e.g batch size 20000, span1 in line 19999, span2 in line 20001)
//                List<String> existSpanList = wrongTraceMap.get(traceId);
//                if (existSpanList == null) {
//                    existSpanList = new ArrayList<>();
//                    wrongTraceMap.put(traceId, existSpanList);
//                }
//                for (byte[] bytes : spanList) {
//                    existSpanList.add(new String(bytes));
//                }
//            }
//        }
    }

}

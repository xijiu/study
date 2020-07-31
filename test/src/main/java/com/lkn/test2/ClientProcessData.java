package com.lkn.test2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lkn.test.Constants;
import com.lkn.test.Msg;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;


public class ClientProcessData {

    private static int BATCH_COUNT = 15;

    private static BatchBean[] BATCH_BEANS = new BatchBean[BATCH_COUNT];

    public static int CURRENT_CLIENT_PORT = -1;

    public static volatile int CURRENT_BATCH_POS = 0;

    private static Msg msg = new Msg();

    public static volatile int CONSUME_BATCH_POS = 0;

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
        CURRENT_CLIENT_PORT = 8081;
        for (int i = 0; i < BATCH_COUNT; i++) {
//            BATCH_TRACE_LIST.add(new ConcurrentHashMap<>(Constants.BATCH_SIZE));
            semaphores[i] = new Semaphore(1);
            BATCH_BEANS[i] = new BatchBean();
        }
    }


    public void doRun() {
        try {
            InputStream input = new FileInputStream("/Users/likangning/Desktop/云原生比赛/trace1.data");
            byte[] data = new byte[1024 * 1024 * 5];
            int byteNum;
            byte[] lineByteArr;
            int count = 0;
            int pos = 0;
            long[] badTraceIdArr = new long[30];
            int badTraceIdArrIndex = 0;
            semaphores[BATCH_COUNT - 2].acquire();
//            Map<Long, List<byte[]>> traceMap = BATCH_TRACE_LIST.get(pos);
            BatchBean currentBatchBean = BATCH_BEANS[0];
            byte[] lastByteArr = null;
            while ((byteNum = input.read(data)) != -1) {
                int beginPos = 0;
                int beginIndex = 0;
                while (true) {
                    int i;
                    for (i = beginPos; i < byteNum; i++) {
                        if (data[i] == 10) {
                            long traceId;
                            if (lastByteArr != null) {
//                                lineByteArr = new byte[lastByteArr.length + i - beginIndex];
//                                System.arraycopy(lastByteArr, 0, lineByteArr, 0, lastByteArr.length);
//                                System.arraycopy(data, beginIndex, lineByteArr, lastByteArr.length, i - beginIndex);
//                                traceId = longFrom8Bytes(lineByteArr, 0);
                                lastByteArr = null;
                            } else {
                                traceId = longFrom8Bytes(data, beginIndex);
                                currentBatchBean.storeLineByteArr(traceId, data, beginIndex, i - beginIndex + 1);
                                if (isBadTraceData(data, i - 1)) {
//                                    System.out.println(new String(data, beginIndex, i - beginIndex + 1));
                                    badTraceIdArr[badTraceIdArrIndex++] = traceId;
                                }
                            }
                            beginIndex = i + 1;
                            count++;
                            if (count % Constants.BATCH_SIZE == 0) {
                                pos++;
                                if (pos >= BATCH_COUNT) {
                                    pos = 0;
                                }
                                int batchPos = count / Constants.BATCH_SIZE - 1;
//                                while (batchPos - CONSUME_BATCH_POS > BATCH_COUNT - 5) {
//                                    Thread.sleep(1);
//                                }
//                                semaphores[batchPos % BATCH_COUNT].acquire();
                                updateWrongTraceId(badTraceIdArr, badTraceIdArrIndex, batchPos);
                                badTraceIdArrIndex = 0;
                                CURRENT_BATCH_POS = batchPos;
                                currentBatchBean.reset();
                                currentBatchBean = BATCH_BEANS[pos];
                            }
                            beginPos = i + 100;
                            break;
                        }
                    }
                    if (i >= byteNum) {
                        break;
                    }
                }
//                if (beginIndex <= byteNum) {
//                    int length = byteNum - beginIndex;
//                    lastByteArr = new byte[length];
//                    System.arraycopy(data, beginIndex, lastByteArr, 0, length);
//                }
            }

            FINISH = true;
            callFinish();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isBadTraceData(byte[] lineByteArr, int endIndex) {
        if (lineByteArr.length < 20 || endIndex < 10) {
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

        // http.status_code=200
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
    private void updateWrongTraceId(long[] badTraceIdList, int arrLength, int batchPos) {
        // 不论是否有badTrace数据，都要向server上报
        msg.setBatchPos(batchPos);
        badTraceIdList[badTraceIdList.length - 1] = arrLength;
        msg.setMsgBody(JSON.toJSONString(badTraceIdList));
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
            previous = BATCH_COUNT - 1;
        }
        int next = pos + 1;
        if (next == BATCH_COUNT) {
            next = 0;
        }
        int previousAndPrevious = previous - 1;
        if (previousAndPrevious == -1) {
            previousAndPrevious = BATCH_COUNT - 1;
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
        BATCH_BEANS[previous].reset();
        CONSUME_BATCH_POS = batchPos - 1;
        semaphores[previousAndPrevious].release();
        // to clear spans, don't block client process thread. TODO to use lock/notify
        return JSON.toJSONString(wrongTraceMap);
    }

    private static void getWrongTraceWithBatch(int batchPos, Set<Long> traceIdSet, Map<Long, List<String>> wrongTraceMap) {
        BatchBean batchBean = BATCH_BEANS[batchPos];
        for (Long traceId : traceIdSet) {
            byte[] data = batchBean.getDataByTraceId(traceId);
            if (data == null) {
                continue;
            } else {
                List<String> existSpanList = wrongTraceMap.get(traceId);
                if (existSpanList == null) {
                    existSpanList = new ArrayList<>();
                    wrongTraceMap.put(traceId, existSpanList);
                }
                int beginPos = 0;
                int i = 0;
                while (true) {
                    for (i = beginPos; i < data.length; i++) {
                        if (data[i] == 10) {
                            existSpanList.add(new String(data, beginPos, i - beginPos));
                            beginPos = i + 1;
                            break;
                        }
                    }
                    if (i >= data.length) {
                        break;
                    }
                }
            }
        }
    }

}

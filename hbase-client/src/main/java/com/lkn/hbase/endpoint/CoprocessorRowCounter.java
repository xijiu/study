package com.lkn.hbase.endpoint;

/**
 * @author likangning
 * @since 2018/5/18 上午9:44
 */

import com.lkn.hbase.ConnHbase;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.ipc.BlockingRpcCallback;
import org.apache.hadoop.hbase.ipc.ServerRpcController;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class CoprocessorRowCounter extends ConnHbase {

	@Test
	public void exeCoprocessor() throws Throwable {

		HTable table = (HTable) conn.getTable(TableName.valueOf("report_meta"));

		//发送请求
		final ExampleProtos.CountRequest request = ExampleProtos.CountRequest.getDefaultInstance();

		//回调函数 call方法
		Map<byte[], Long> results = table.coprocessorService(ExampleProtos.RowCountService.class,
						null, null, (counter) -> {
								ServerRpcController controller = new ServerRpcController();
								BlockingRpcCallback<ExampleProtos.CountResponse> rpcCallback = new BlockingRpcCallback<>();

								//实现在server端
								counter.getRowCount(controller, request, rpcCallback);
								ExampleProtos.CountResponse response = rpcCallback.get();
								if (controller.failedOnException()) {
									throw controller.getFailedOn();
								}
								//返回
								return (response != null && response.hasCount()) ? response.getCount() : 0;
						});

		int sum = 0;
		int count = 0;

		for (Long l : results.values()) {
			sum += l;
			count++;
		}
		System.out.println("row count = " + sum);
		System.out.println("region count = " + count);
	}
}

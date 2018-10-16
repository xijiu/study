package com.lkn.hbase.paging;


import com.lkn.hbase.ConnHbase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

public class MyAggregationClient extends ConnHbase {

    @Test
    public void test() throws Throwable {
        String tableName = "test";
        //提高RPC通信时长
        configuration.setLong("hbase.rpc.timeout", 600000);
        //设置Scan缓存
        configuration.setLong("hbase.jdk_client.scanner.caching", 1000);
        Configuration configuration = HBaseConfiguration.create(super.configuration);
        AggregationClient aggregationClient = new AggregationClient(configuration);
        Scan scan = customerScan();
        long rowCount = aggregationClient.rowCount(TableName.valueOf(tableName), new LongColumnInterpreter(), scan);
        System.out.println("row count is " + rowCount);
    }

    private Scan customerScan() {
        Scan scan = new Scan();
        //根据列族名统计行数
//        scan.addFamily(Bytes.toBytes("big"));
//        scan.addColumn(Bytes.toBytes("age"), Bytes.toBytes("big"));

        SingleColumnValueFilter filter = new SingleColumnValueFilter(
                Bytes.toBytes("age"),
                Bytes.toBytes("small"),
//                null,
                CompareFilter.CompareOp.EQUAL,
                Bytes.toBytes("15")
        );
        scan.setFilter(filter);
        return scan;
    }
}

package com.lkn.hbase;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;


public class ConnHbase {
    private static Configuration configuration = null;
    private static Admin admin = null;
    private static Connection conn = null;

    /**
     * 初始化连接
     */
    @Before
    public void init() throws IOException {
//        System.setProperty("hadoop.home.dir", "D:\\hadoop-2.7.3");   //必备条件之一
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "localhost");   //hadoop14,hadoop15,hadoop16为hostname
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        conn = ConnectionFactory.createConnection(configuration);
        admin = conn.getAdmin();
    }

    /**
     * 关闭连接
     */
    @After
    public void destroy() {
        if (admin != null) {
            try {
                admin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 建表
     *
     * @throws IOException
     */
    @Test
    public void createTable() throws IOException {
        String tableName = "test1";
        String[] families = {"name", "age", "sex"};
        if (admin.tableExists(TableName.valueOf(tableName))) {
            System.out.println(tableName + "已存在");
        } else {
            HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
            for (String family : families) {
                tableDesc.addFamily(new HColumnDescriptor(family));
            }
            admin.createTable(tableDesc);
            System.out.println("Table created");
        }
    }

    /**
     * 新增列簇
     */
    @Test
    public void addFamily() {
        String tableName = "test1";
        String family = "born";
        try {
            HColumnDescriptor columnDesc = new HColumnDescriptor(family);
            admin.addColumn(TableName.valueOf(tableName), columnDesc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询表信息
     */
    @Test
    public void query() throws IOException {
        String tableName = "test";
        HTable hTable = null;
        ResultScanner scann = null;

        hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
        scann = hTable.getScanner(new Scan());
        for (Result rs : scann) {
            System.out.println("RowKey为：" + new String(rs.getRow()));

            //按cell进行循环
            for (Cell cell : rs.rawCells()) {
                System.out.println("列簇为：" + new String(CellUtil.cloneFamily(cell)));
                System.out.println("列修饰符为：" + new String(CellUtil.cloneQualifier(cell)));
                System.out.println("值为：" + new String(CellUtil.cloneValue(cell)));
            }
            System.out.println("=============================================");
        }
        scann.close();
        hTable.close();
    }

    /**
     * 根据rowkey查询单行
     */
    @Test
    public void queryByRowKey() throws IOException {
        String key = "lkn1";
        String tableName = "test";
        HTable hTable = null;
        try {
            hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
            Result rs = hTable.get(new Get(Bytes.toBytes(key)));
            System.out.println(tableName + "表RowKey为" + key + "的行数据如下：");
            for (Cell cell : rs.rawCells()) {
                System.out.println("列簇为：" + new String(CellUtil.cloneFamily(cell)));
                System.out.println("值为：" + new String(CellUtil.cloneValue(cell)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            hTable.close();
        }
    }



    /**
     * 插入单行单列簇单列修饰符数据
     */
    @Test
    public void addOneRecord() {
        String tableName = "";
        String key = "";
        String family = "";
        String col = "";
        String val = "";
        HTable hTable = null;
        try {
            hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
            Put p = new Put(Bytes.toBytes(key));
            p.addColumn(Bytes.toBytes(family), Bytes.toBytes(col), Bytes.toBytes(val));
            if (p.isEmpty()) {
                System.out.println("数据插入异常，请确认数据完整性，稍候重试");
            } else {
                hTable.put(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (hTable != null) {
                try {
                    hTable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 插入单行单列簇多列修饰符数据
     */
    @Test
    public void addMoreRecord() throws IOException {
        String tableName = "test";
        String key = "";
        String family = "";
        Map<String, String> colVal = Maps.newHashMap();
        HTable hTable = null;
        try {
            hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
            Put p = new Put(Bytes.toBytes(key));
            for(String col : colVal.keySet()){
                String val = colVal.get(col);
                if(StringUtils.isNotBlank(val)){
                    p.addColumn(Bytes.toBytes(family),Bytes.toBytes(col),Bytes.toBytes(val));
                } else {
                    System.out.println("列值为空，请确认数据完整性");
                }
            }
//当put对象没有成功插入数据时，此时调用hTable.put(p)方法会报错：java.lang.IllegalArgumentException:No columns to insert
            if(p.isEmpty()){
                System.out.println("数据插入异常，请确认数据完整性，稍候重试");
            }else{
                hTable.put(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            hTable.close();
        }
    }



}

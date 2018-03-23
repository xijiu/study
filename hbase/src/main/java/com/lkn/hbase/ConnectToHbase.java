package com.lkn.hbase;

import java.io.IOException;

//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.hadoop.hbase.HColumnDescriptor;
//import org.apache.hadoop.hbase.HTableDescriptor;
//import org.apache.hadoop.hbase.TableName;
//import org.apache.hadoop.hbase.client.Admin;
//import org.apache.hadoop.hbase.client.Connection;
//import org.apache.hadoop.hbase.client.ConnectionFactory;


public class ConnectToHbase {
//    public static Configuration conf;
//    public static Connection connection;
//    public static Admin admin;
//
//    public static void main(String[] args) throws IOException {
//
//        conf = HBaseConfiguration.create();
//        conf.set("hbase.master", "192.168.142.129:16000");
//
//        connection = ConnectionFactory.createConnection(conf);
//        admin = connection.getAdmin();
//
//        HTableDescriptor table = new HTableDescriptor(TableName.valueOf("table1"));
//        table.addFamily(new HColumnDescriptor("group1")); //创建表时至少加入一个列组
//
//        if(admin.tableExists(table.getTableName())){
//            admin.disableTable(table.getTableName());
//            admin.deleteTable(table.getTableName());
//        }
//        admin.createTable(table);
//    }
}

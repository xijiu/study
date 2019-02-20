package com.lkn.hbase;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.HttpConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;


public class ConnHbase {
	protected Configuration configuration = null;
	protected Admin admin = null;
	protected Connection conn = null;

	/**
	 * 初始化连接
	 */
	@Before
	public void init() throws IOException {
		System.setProperty("hadoop.home.dir", "/Users/likangning/cloudera/cdh5.7/hadoop");   //必备条件之一
		configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.quorum", "localhost");   //hadoop14,hadoop15,hadoop16为hostname
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		conn = ConnectionFactory.createConnection(configuration);
		admin = conn.getAdmin();
		admin.getConnection();
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
		String tableName = "KYLIN_4XCRK8KQLY";
		HTable hTable = null;
		ResultScanner scann = null;

		hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
		scann = hTable.getScanner(new Scan());
		for (Result rs : scann) {
			System.out.println("RowKey为：" + Bytes.toHex(rs.getRow()));
			System.out.println("RowKey为：" + Bytes.toStringBinary(rs.getRow()));

			//按cell进行循环
			for (Cell cell : rs.rawCells()) {
				System.out.println("列簇为：" + Bytes.toHex(cell.getRowArray()));
				System.out.println("列修饰符为：" + Bytes.toString(CellUtil.cloneQualifier(cell)));
				System.out.println("值为：" + Bytes.toHex(CellUtil.cloneValue(cell)));
				System.out.println("值为2：" + Bytes.toHex(cell.getValueArray()));
				System.out.println("*************** value长度为：" + cell.getValueArray().length);
			}
			System.out.println("=============================================");
		}
		scann.close();
		hTable.close();
	}

	public static String getRealRowKey(KeyValue kv) {
		int rowlength = Bytes.toShort(kv.getBuffer(), kv.getOffset()+KeyValue.ROW_OFFSET);
		String rowKey = Bytes.toStringBinary(kv.getBuffer(), kv.getOffset()+KeyValue.ROW_OFFSET + Bytes.SIZEOF_SHORT, rowlength);
		return rowKey;
	}


	/**
	 * 查询表信息
	 */
	@Test
	public void query2() throws IOException {
		HTable hTable = null;
		ResultScanner scann = null;

		hTable = (HTable) conn.getTable(TableName.valueOf("test"));
		scann = hTable.getScanner(new Scan());
		for (Result result : scann) {
			byte[] value = result.getValue(Bytes.toBytes("age"), Bytes.toBytes("big"));

			System.out.println("结果： " + ((value == null || value.length == 0) ? "" : new String(value)));
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
		String key = "lkn";
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

	@Test
	public void addManyRecord() {
		String tableName = "test";
		String family = "age";
		HTable hTable = null;
		try {
			hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
			for (int i = 0; i < 20; i++) {
				String key = "1061-" + "201803" + to2StringLen(i) + "-1";
				Put p = new Put(Bytes.toBytes(key));
				p.addColumn(Bytes.toBytes(family), null, Bytes.toBytes(String.valueOf(randomCount())));
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

	private String to2StringLen(int i) {
		if (i >= 10) {
			return String.valueOf(i);
		} else if (i >= 0 && i < 10) {
			return "0" + i;
		}
		return String.valueOf(i);
	}

	private Integer randomCount() {
		int time = (int) (Math.random() * 10);
		time = time * 10;
		if (time == 0) {
			return randomCount();
		}
		return time;
	}

	/**
	 * 插入单行单列簇单列修饰符数据
	 */
	@Test
	public void addOneRecord() {
		String tableName = "test";
		String key = "lkn3";
		String family = "age";
		String col = null;
		String val = "20";
		HTable hTable = null;
		try {
			hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
			Put p = new Put(Bytes.toBytes(key));
			p.addColumn(Bytes.toBytes(family), null, Bytes.toBytes(val));
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
			for (String col : colVal.keySet()) {
				String val = colVal.get(col);
				if (StringUtils.isNotBlank(val)) {
					p.addColumn(Bytes.toBytes(family), Bytes.toBytes(col), Bytes.toBytes(val));
				} else {
					System.out.println("列值为空，请确认数据完整性");
				}
			}
			//当put对象没有成功插入数据时，此时调用hTable.put(p)方法会报错：java.lang.IllegalArgumentException:No columns to insert
			if (p.isEmpty()) {
				System.out.println("数据插入异常，请确认数据完整性，稍候重试");
			} else {
				hTable.put(p);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			hTable.close();
		}
	}


}

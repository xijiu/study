package com.lkn.mysql;

import org.junit.After;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.*;
import java.util.UUID;

/**
 * mysql主键性能的对比
 * 分别向两张表（除了主键外一模一样，一张表的主键为bigint且自增，一张表为uuid）中插入100w数据，比较性能及索引文件大小
 *
 *
 ----------------主键自增----------------
 耗时(ms): 10095
 占用总空间(M)：62.60

 ----------------主键uuid----------------
 耗时(ms): 19035
 占用总空间(M)：122.76

 ----------------主键随机生成----------------
 耗时(ms): 12934
 占用总空间(M)：141.85

 ----------------主键自增（间隙逐渐增大）----------------
 耗时(ms): 10355
 占用总空间(M)：60.59


 针对主键递增的设计，mysql有相应的优化策略，即当其检测到主键为递增时，会将B+树的叶子节点写满后再进行新叶子节点的写入；
 但这样也带来了一个bug，详见：https://blog.csdn.net/sparkliang/article/details/30035191


 在数据量较小的时候，感知不到uuid响应速度缓慢，主要原因是索引文件会全部命中page cache。当数据量越大，响应时间越长

 *
 * @author likangning
 * @since 2019/3/11 下午4:32
 */
public class CompareMysqlPK {

	private static Connection connection;

	private static String db = "test";

	private int recordNum = 1000000;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + db + "?rewriteBatchedStatements=true&useServerPrepStmts=true","root","root");
			connection.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void compare() throws Exception {
		incrementTest();
		uuidTest();
		randomIntTest();
		increment2Test();
	}

	private void randomIntTest() throws SQLException, InterruptedException {
		System.out.println("\r\n----------------主键随机生成----------------");
		String tableName = "goods_pk_bigint_random";
		createOrTruncateTable(tableName, 3);
		insertRandomData(tableName);
		showTableSpace(tableName);
	}

	private void incrementTest() throws Exception {
		System.out.println("\r\n----------------主键自增----------------");
		String tableName = "goods_pk_bigint";
		createOrTruncateTable(tableName, 1);
		insertIncrementData(tableName);
		showTableSpace(tableName);
	}

	private void increment2Test() throws Exception {
		System.out.println("\r\n----------------主键自增（间隙逐渐增大）----------------");
		String tableName = "goods_pk_bigint2";
		createOrTruncateTable(tableName, 3);
		insertIncrement2Data(tableName);
		showTableSpace(tableName);
	}

	private void uuidTest() throws Exception {
		System.out.println("\r\n----------------主键uuid----------------");
		String tableName = "goods_pk_uuid";
		createOrTruncateTable(tableName, 2);
		insertUuidData(tableName);
		showTableSpace(tableName);
	}

	private void insertUuidData(String tableName) throws SQLException {
		long begin = System.currentTimeMillis();
		String sql = "insert into " + tableName + "(UUID,NAME,AGE,ADDRESS,MOBILE) values(?,?,?,?,?)";
		PreparedStatement ps = connection.prepareStatement(sql);
		for (int i = 0; i < recordNum; i++) {
			ps.setString(1, UUID.randomUUID().toString().replace("-", "").substring(0, 20));
			ps.setString(2, "name" + i);
			ps.setInt(3, i);
			ps.setString(4, "address" + i);
			ps.setInt(5, 158106712);
			ps.addBatch();
			if (i % 1000 == 0) {
				ps.executeBatch();
			}
		}
		ps.executeBatch();
		ps.close();
		long end = System.currentTimeMillis();
		System.out.println("耗时(ms): " + (end - begin));
	}

	private void showTableSpace(String tableName) throws SQLException, InterruptedException {
		Thread.sleep(10000);
		String sql = "select sum(DATA_LENGTH)+sum(INDEX_LENGTH) from information_schema.tables where table_schema='" + db + "' and TABLE_NAME='" + tableName + "'";
		PreparedStatement ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			BigDecimal tableSize = rs.getBigDecimal(1);
			System.out.println("占用总空间(B)：" + tableSize);
			BigDecimal result = tableSize.divide(new BigDecimal(1024 * 1024), 2, BigDecimal.ROUND_FLOOR);
			System.out.println("占用总空间(M)：" + result);
		}
		rs.close();
		ps.close();

	}

	private void insertIncrementData(String tableName) throws SQLException {
		long begin = System.currentTimeMillis();
		String sql = "insert into " + tableName + "(NAME,AGE,ADDRESS,MOBILE) values(?,?,?,?)";
		PreparedStatement ps = connection.prepareStatement(sql);
		for (int i = 0; i < recordNum; i++) {
			ps.setString(1, "name" + i);
			ps.setInt(2, i);
			ps.setString(3, "address" + i);
			ps.setInt(4, 158106712);
			ps.addBatch();
			if (i % 10000 == 0) {
				ps.executeBatch();
			}
		}
		ps.executeBatch();
		ps.close();
		long end = System.currentTimeMillis();
		System.out.println("耗时(ms): " + (end - begin));
	}

	private void insertIncrement2Data(String tableName) throws SQLException {
		long begin = System.currentTimeMillis();
		String sql = "insert into " + tableName + "(NAME,AGE,ADDRESS,MOBILE,ID) values(?,?,?,?,?)";
		PreparedStatement ps = connection.prepareStatement(sql);
		for (int i = 0; i < recordNum; i++) {
			ps.setString(1, "name" + i);
			ps.setInt(2, i);
			ps.setString(3, "address" + i);
			ps.setInt(4, 158106712);
			ps.setInt(5, i * 2);
			ps.addBatch();
			if (i % 10000 == 0) {
				ps.executeBatch();
			}
		}
		ps.executeBatch();
		ps.close();
		long end = System.currentTimeMillis();
		System.out.println("耗时(ms): " + (end - begin));
	}

	private void insertRandomData(String tableName) throws SQLException {
		long begin = System.currentTimeMillis();
		String sql = "insert into " + tableName + "(NAME,AGE,ADDRESS,MOBILE,ID) values(?,?,?,?,?)";
		PreparedStatement ps = connection.prepareStatement(sql);
		for (int i = 0; i < recordNum / 2; i++) {
			ps.setString(1, "name" + i);
			ps.setInt(2, i);
			ps.setString(3, "address" + i);
			ps.setInt(4, 158106712);
			ps.setInt(5, i);
			ps.addBatch();
			ps.setString(1, "name" + i);
			ps.setInt(2, i);
			ps.setString(3, "address" + i);
			ps.setInt(4, 158106712);
			ps.setInt(5, recordNum - i);
			ps.addBatch();
			if (i % 5000 == 0) {
				ps.executeBatch();
			}
		}
		ps.executeBatch();
		ps.close();
		long end = System.currentTimeMillis();
		System.out.println("耗时(ms): " + (end - begin));
	}

	private void createOrTruncateTable(String tableName, int flag) throws SQLException {
		if (isExist(tableName)) {
			String sql = "truncate table " + tableName;
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.execute();
			ps.close();
		} else {
			if (flag == 1) {
				createIncrementTable(tableName);
			} else if (flag == 2) {
				createUuidTable(tableName);
			} else {
				createRandomTable(tableName);
			}
		}
	}

	private void createRandomTable(String tableName) throws SQLException {
		String sql = "CREATE TABLE `" + tableName + "` (\n" +
				"  `ID` bigint(20) NOT NULL,\n" +
				"  `NAME` varchar(45) DEFAULT NULL,\n" +
				"  `AGE` int(11) DEFAULT NULL,\n" +
				"  `ADDRESS` varchar(255) DEFAULT NULL,\n" +
				"  `MOBILE` int(11) DEFAULT NULL,\n" +
				"  PRIMARY KEY (`ID`)\n" +
				") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.execute();
		ps.close();
	}

	private void createUuidTable(String tableName) throws SQLException {
		String sql = "CREATE TABLE `" + tableName + "` (\n" +
				"  `UUID` varchar(20) NOT NULL,\n" +
				"  `NAME` varchar(45) DEFAULT NULL,\n" +
				"  `AGE` int(11) DEFAULT NULL,\n" +
				"  `ADDRESS` varchar(255) DEFAULT NULL,\n" +
				"  `MOBILE` int(11) DEFAULT NULL,\n" +
				"  PRIMARY KEY (`UUID`)\n" +
				") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.execute();
		ps.close();
	}

	private void createIncrementTable(String tableName) throws SQLException {
		String sql = "CREATE TABLE `" + tableName + "` (\n" +
				"  `ID` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
				"  `NAME` varchar(45) DEFAULT NULL,\n" +
				"  `AGE` int(11) DEFAULT NULL,\n" +
				"  `ADDRESS` varchar(255) DEFAULT NULL,\n" +
				"  `MOBILE` int(11) DEFAULT NULL,\n" +
				"  PRIMARY KEY (`ID`)\n" +
				") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.execute();
		ps.close();
	}

	private boolean isExist(String tableName) throws SQLException {
		boolean exist = false;
		String sql = "SELECT table_name FROM information_schema.TABLES WHERE table_schema='" + db + "' and table_name ='" + tableName + "'";
		PreparedStatement ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String table = rs.getString(1);
			if (table != null && table.equalsIgnoreCase(tableName)) {
				exist = true;
				break;
			}
		}
		rs.close();
		ps.close();
		return exist;
	}


	@After
	public void after() throws SQLException {
		connection.close();
	}

}

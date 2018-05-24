package com.lkn.calcite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.Properties;

/**
 * @author likangning
 * @since 2018/5/7 下午8:50
 */
public class SimpleTest {
	private Connection connection;

	@Before
	public void before() {
		try {
			Class.forName("org.apache.calcite.jdbc.Driver");
			Properties info = new Properties();
			connection = DriverManager.getConnection("jdbc:calcite:model=/Users/likangning/study/study/calcite/src/main/resources/School.json", info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void after() throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	/**
	 * scan所有数据
	 * @throws Exception
	 */
	@Test
	public void myTest() throws Exception {
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery("select * from \"Student\"");
		while (rs.next()) {
			printResultSet(rs);
		}
	}

	private void printResultSet(ResultSet rs) throws SQLException {
		System.out.print(rs.getString(1) + " | ");
		System.out.print(rs.getString(2) + " | ");
		System.out.print(rs.getString(3) + " | ");
		System.out.print(rs.getString(4) + " | ");
		System.out.print(rs.getString(5) + " | ");
		System.out.println("");
	}

	/**
	 * 求总数count
	 * @throws Exception
	 */
	@Test
	public void countTest() throws Exception {
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery("select count(*) from \"Student\"");
		while (rs.next()) {
			System.out.println("count result : " + rs.getObject(1));
		}
	}

	/**
	 * 实现where过滤
	 * @throws Exception
	 */
	@Test
	public void whereTest() throws Exception {
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery("select * from \"Student\" where \"name\" = 'fengysh'");
		while (rs.next()) {
			printResultSet(rs);
		}
	}


	@Test
	public void originTest() {
		try {
			ResultSet result = connection.getMetaData().getTables(null, null, null, null);
			while(result.next()) {
				System.out.println("========> Catalog : " + result.getString(1) + ",Database : " + result.getString(2) + ",Table : " + result.getString(3));
			}
			result.close();
			result = connection.getMetaData().getColumns(null, null, "Student", null);
			while(result.next()) {
				System.out.println("========> name : " + result.getString(4) + ", type : " + result.getString(5) + ", typename : " + result.getString(6));
			}
			result.close();

			Statement st = connection.createStatement();
			result = st.executeQuery("select THE_SYEAR(\"birthday\", 'year'), 1 , count(1) from \"Student\" as S "
							+ "INNER JOIN \"Class\" as C on S.\"classId\" = C.\"id\" group by THE_SYEAR(\"birthday\", 'year')");
			while(result.next()) {
				System.out.println(result.getString(1) + "\t" + result.getString(2) + "\t" + result.getString(3));
			}
			result.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

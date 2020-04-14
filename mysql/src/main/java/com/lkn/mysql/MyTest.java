package com.lkn.mysql;

import org.junit.Test;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author likangning
 * @since 2020/4/8 上午10:03
 */
public class MyTest {

	private static Connection connection = CompareMysqlPK.connection;

	private static String querySql = "select * from test.fast_c_resume where user_id = 100000 limit 1";

	private static AtomicLong atomicLong = new AtomicLong();

	@Test
	public void compare() throws Exception {
		ExecutorService executorService = Executors.newCachedThreadPool();
		int threadNum = 100;
		int exeTime = 1000;
		for (int i = 0; i < threadNum; i++) {
			final int num = i + 1;
			executorService.submit(() -> {
				Connection connection = null;
				try {
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?rewriteBatchedStatements=true&useServerPrepStmts=true","root","root");
					PreparedStatement preparedStatement = connection.prepareStatement(querySql);
					long begin = System.currentTimeMillis();
//					System.out.println("线程 " + num + "开始 ： " + begin);
					for (int j = 0; j < exeTime; j++) {
						long singleBegin = System.currentTimeMillis();
						ResultSet rs = preparedStatement.executeQuery();
						while (rs.next()) {
							long userId = rs.getLong("user_id");
						}
						rs.close();
						long singleCost = System.currentTimeMillis() - singleBegin;
						if (singleCost > 300) {
							System.out.println(System.currentTimeMillis() + " : " + singleCost);
						}
					}
					long cost = System.currentTimeMillis() - begin;
					atomicLong.addAndGet(cost);
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});

		}

		executorService.shutdownNow();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

		System.out.println("total cost : " + atomicLong);
		System.out.println("per cost : " + atomicLong.longValue() / (threadNum * exeTime));
	}


	@Test
	public void insertTest() throws Exception {
		String insertSql = "INSERT INTO `test`.`fast_c_resume` (`user_id`, `user_city`, `user_address`, `longitude`, `latitude`, `max_range`, `min_salary`, `industry_preference`, `can_outsource`, `company_preference`, `is_recommend`, `nearby_pdf_file_url`, `nearby_file_url`, `production_img_url`, `production_pdf_file_url`, `nearby_id`, `production_url`, `production_name`, `distinct`, `business_area`, `status`, `is_del`, `create_time`, `update_time`) VALUES (?, '2', '2', '2', '2', '2', '2', '2', b'0', '2', b'0', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', b'0', '2020-01-10 12:33:10', '2020-01-10 12:33:10');\n";
		PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
		for (int i = 11; i < 20000; i++) {
			preparedStatement.setLong(1, i);
			preparedStatement.executeUpdate();
		}
		connection.close();
	}

}

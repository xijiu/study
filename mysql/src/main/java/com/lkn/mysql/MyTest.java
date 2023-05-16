package com.lkn.mysql;

import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author likangning
 * @since 2020/4/8 上午10:03
 */
public class MyTest {

	private static Connection connection;

	private static String db = "wolong_extend";

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/fly?useUnicode=true","root","root");
			connection.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Test
	public void insertTestNew() throws Exception {
		String insertSql = "INSERT INTO `fly`.`deploy_task` (`seq`, `task_id`, `instance_id`, `task_level`, `action`, `target_id`, `status`, `exec`, `data`, `modify_by`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		connection.setAutoCommit(false);
		PreparedStatement ps = connection.prepareStatement(insertSql);
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			ps.setInt(1, new Random().nextInt());
			ps.setString(2, System.currentTimeMillis() + "");
			ps.setString(3, System.currentTimeMillis() + "");
			ps.setInt(4, 0);
			ps.setString(5, "DEPLOY2");
			ps.setString(6, System.currentTimeMillis() + "");
			ps.setInt(7, 1);
			ps.setInt(8, 1);
			ps.setString(9, "{\"currConfig\":{\"acrossZone\":false,\"descVersion\":1,\"diskCategory\":\"cloud_efficiency\",\"diskSize\":900,\"enableAcl\":false,\"enableVpcSasl\":true,\"forceAcrossZone\":false,\"instanceId\":\"alikafka_post-cn-zvp2mrxzk00d\",\"ioMaxSpec\":\"alikafka.hw2.300xlarge\",\"maxMsgSize\":10,\"msgStoreTime\":10000,\"nodes\":{101:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":101},102:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":102},103:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":103},104:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":104},105:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":105},106:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":106},107:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":107},108:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":108},109:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":109},110:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":110},111:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":111},112:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":112},113:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":113},114:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":114},115:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":115}},\"offsetStoreTime\":10000,\"ourVpcId\":\"ourVpcId\",\"pubNet\":0,\"regionId\":\"cn-beijing\",\"specType\":\"professional\",\"state\":\"running\",\"tieredNas\":true,\"tieredNasStoreSpace\":0,\"vSwitchId\":\"vSwitchId\",\"version\":\"8.0.10.2-5.0.3\",\"vpcId\":\"vpcId\"},\"exeNum\":1352,\"majorHandler\":\"CLUSTER_UPDATE\",\"newConfig\":{\"acrossZone\":false,\"descVersion\":1,\"diskCategory\":\"cloud_efficiency\",\"diskSize\":900,\"enableAcl\":false,\"enableVpcSasl\":true,\"forceAcrossZone\":false,\"instanceId\":\"alikafka_post-cn-zvp2mrxzk00d\",\"ioMaxSpec\":\"alikafka.hw2.1000xlarge\",\"maxMsgSize\":10,\"msgStoreTime\":10000,\"nodes\":{128:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":128},129:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":129},130:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":130},131:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":131},132:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":132},133:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":133},134:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":134},135:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":135},136:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":136},137:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":137},138:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":138},139:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":139},140:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":140},141:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":141},142:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":142},143:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":143},144:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":144},145:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":145},146:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":146},147:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":147},148:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":148},149:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":149},150:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":150},101:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":101},102:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":102},103:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":103},104:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":104},105:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":105},106:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":106},107:{\"cpu\":4,\"disk\":0,\"memory\":16,\"nodeIndex\":107},108:{\"cpu\":4,\"disk\"");
			ps.setString(10, "deploy2");
			ps.executeUpdate();
			if (i % 1000 == 0) {
				connection.commit();
			}
			if (i % 10000 == 0) {
				System.out.println(i);
			}
		}
		long cost = System.currentTimeMillis() - begin;
		System.out.println("耗时 ： "  + cost);
		connection.close();
	}




	@Test
	public void insertTest() throws Exception {
		String insertSql = "INSERT INTO `wolong_extend`.`tb_unit_0000` (`bid`, `create_time`, `is_paused`, `logic_state`, `negative_query`, `negative_word`, `name`, `planid`, `userid`, `delete_time`, `platform`, `last_update_time`, `match_factor`, `deeplink`) VALUES (100, '2020-07-20 14:42:14.0', 0, 0, '', '', 'lwj0720tdxk003-天地豪侠', 114286881, 210601880, '9998-11-30 00:00:00.0', 7, '2020-07-20 14:32:25.0', '', '')";
		connection.setAutoCommit(false);
		PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 49797182; i++) {
			preparedStatement.executeUpdate();
			if (i % 1000 == 0) {
				connection.commit();
			}
			if (i % 1000000 == 0) {
				System.out.println(i);
			}
		}
		long cost = System.currentTimeMillis() - begin;
		System.out.println("耗时 ： "  + cost);
		connection.close();
	}


	@Test
	public void insertTest2222() throws Exception {
		String insertSql = "INSERT INTO `wolong_extend`.`tb_unit_0000` (`bid`, `create_time`, `is_paused`, `logic_state`, `negative_query`, `negative_word`, `name`, `planid`, `userid`, `delete_time`, `platform`, `last_update_time`, `match_factor`, `deeplink`) VALUES (100, '2020-07-20 14:42:14.0', 0, 0, '', '', 'lwj0720tdxk003-天地豪侠', 114286881, 210601880, '9998-11-30 00:00:00.0', 7, '2020-07-20 14:32:25.0', '', '')";
		connection.setAutoCommit(false);
		Statement st = connection.createStatement();
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 49797182; i++) {
			st.execute(insertSql);
			if (i % 1000 == 0) {
				connection.commit();
			}
			if (i % 100000 == 0) {
				System.out.println(i);
			}
		}
		long cost = System.currentTimeMillis() - begin;
		System.out.println("耗时 ： "  + cost);
		connection.close();
	}


	@Test
	public void insertTest22223() throws Exception {
		String insertSql = "INSERT INTO `wolong_extend`.`tb_unit_0000` (`bid`, `create_time`, `is_paused`, `logic_state`, `negative_query`, `negative_word`, `name`, `planid`, `userid`, `delete_time`, `platform`, `last_update_time`, `match_factor`, `deeplink`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		connection.setAutoCommit(false);
		PreparedStatement ps = connection.prepareStatement(insertSql);
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 49797182; i++) {
			ps.setInt(1, 100);
			ps.setObject(2, "2020-07-20 14:42:14.0");
			ps.setObject(3, 0);
			ps.setObject(4, 0);
			ps.setObject(5, "");
			ps.setObject(6, "");
			ps.setObject(7, "lwj0720tdxk003-asdfasdf");
			ps.setObject(8, 114286881);
			ps.setObject(9, 210601880);
			ps.setObject(10, "9998-11-30 00:00:00.0");
			ps.setObject(11, 7);
			ps.setObject(12, "2020-07-20 14:32:25.0");
			ps.setObject(13, "");
			ps.setObject(14, "");
			ps.addBatch();
			if (i % 1000 == 0) {
				ps.executeBatch();
			}
			if (i % 1000000 == 0) {
				System.out.println(i);
			}
		}
		long cost = System.currentTimeMillis() - begin;
		System.out.println("耗时 ： "  + cost);
		connection.close();
	}

	@Test
	public void queryTest22223() throws Exception {
		String querySql = "select * from tb_unit_0000 where id in (?)";
		PreparedStatement ps = connection.prepareStatement(querySql);
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 300; i++) {
			String ids = appendIds(10);
			ResultSet rs = ps.executeQuery("select * from tb_unit_0000 where id in (" + ids + ")");
			while (rs.next()) {
				rs.getObject(1);
				rs.getObject(2);
				rs.getObject(3);
				rs.getObject(4);
				rs.getObject(5);
				rs.getObject(6);
				rs.getObject(7);
				rs.getObject(8);
				rs.getObject(9);
				rs.getObject(10);
				rs.getObject(11);
				rs.getObject(12);
				rs.getObject(13);
				rs.getObject(14);
			}
		}
		long cost = System.currentTimeMillis() - begin;
		System.out.println("耗时 ： "  + cost);
		connection.close();
	}

	private Random random = new Random();

	private String appendIds(int number) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < number; i++) {
			sb.append(random.nextInt(50000000)).append(",");
		}
		sb = sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}

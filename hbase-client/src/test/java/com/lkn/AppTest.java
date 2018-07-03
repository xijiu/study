package com.lkn;

import com.google.common.base.Strings;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

/**
 * Unit test for simple App.
 */
public class AppTest {

	@org.junit.Test
	public void bbb2() {
		String sql = appendSql("user", new Object[][]{{"age", "=", 5}, {"name", "like", "张三"}},
				"role", new Object[][]{{"level", "desc"}}, 20, 100);
		System.out.println(sql);
	}

	private String appendSql(String table, Object[][] where, String groupBy,
													 Object[][] orderBy, Integer limit, Integer offset) {

		StringBuilder sb = new StringBuilder("select * from ");
		sb.append(table);
		if (where != null && where.length > 0) {
			appendWhere(sb, where);
		}
		if (!Strings.isNullOrEmpty(groupBy)) {
			sb.append(" group by ").append(groupBy);
		}
		if (orderBy != null && orderBy.length > 0) {
			sb.append(" order by ");
			for (Object[] orderOper : orderBy) {
				sb.append(orderOper[0]).append(" ").append(orderOper[1]).append(",");
			}
			delLastChar(sb, 1);
		}
		if (limit != null && offset != null) {
			sb.append(" limit ").append(limit).append(" offset ").append(offset);
		}
		return sb.toString();
	}

	/**
	 * @param sb
	 * @param where
	 */
	private void appendWhere(StringBuilder sb, Object[][] where) {
		sb.append(" where ");
		for (Object[] condition : where) {
			Object whereOper = condition[1];
			if ("between".equalsIgnoreCase(whereOper.toString())) {
				sb.append(condition[0]).append(" between ").append(condition[1]).append(" and ").append(condition[2]);
			} else if ("in".equalsIgnoreCase(whereOper.toString())) {
				sb.append(condition[0]).append(" in (").append(condition[1]).append(")");
			} else {
				sb.append(condition[0]).append(" ").append(condition[1]).append(" '").append(condition[2]).append("' and ");
			}
		}
		delLastChar(sb, 4);
	}

	/**
	 * 删除StringBuilder的最末尾字符
	 *
	 * @param sb
	 * @param size 最末尾的几位字符
	 * @return
	 */
	private StringBuilder delLastChar(StringBuilder sb, int size) {
		if (sb != null) {
			return sb.delete(sb.length() - size, sb.length());
		}
		return sb;
	}

	@Test
	public void transTest() throws Exception {
		Object a = 15;
		User user = new User();
		Field field = User.class.getDeclaredField("name");
		System.out.println(field.getType());
		field.setAccessible(true);
		try {
			field.set(user, a);
		} catch (Exception e) {
			if (field.getType() == String.class) {
				field.set(user, String.valueOf(a));
			}
		}
		System.out.println(user);
	}

	@Test
	public void connCmd() throws Exception {
		String command = "hbase hbck";
		String[] cmdA = { "/bin/sh", "-c", command };
		Process process = Runtime.getRuntime().exec(cmdA);
		BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(bis));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}

		process.waitFor();
		if (process.exitValue() != 0) {
			System.out.println("error!");
		}

		bis.close();
		br.close();
	}
}

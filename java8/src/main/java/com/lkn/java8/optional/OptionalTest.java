package com.lkn.java8.optional;

import com.lkn.java8.optional.bean.Record;
import com.lkn.java8.optional.bean.User;
import org.junit.Test;

import java.util.Optional;

/**
 * @author likangning
 * @since 2018/5/21 下午12:24
 */
public class OptionalTest {

	@Test
	public void test1() {
		String userName = getNameFor8(null);
		System.out.println(userName);
	}

	/**
	 * java7及之前版本的判断空的用法
	 * @param user
	 * @return
	 */
	private String getName(User user) {
		if (user == null) {
			return "Unknown";
		}
		return user.getName();
	}

	/**
	 * java8的optional
	 * @param user
	 * @return
	 */
	private String getNameFor8(User user) {
		return Optional.ofNullable(user)
						.map(User::getName)
						.orElse("Unknown");
	}


	@Test
	public void test2() {
		User user = new User();
		Long score = getOptional2(user);
		System.out.println(score);
	}

	private Long getOptional2(User user) {
		return Optional.ofNullable(user)
						.map(User::getRecord)
						.map(Record::getScore)
						.orElse(0L);
	}
}

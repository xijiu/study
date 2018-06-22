package com.lkn.spring.tools;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 字符串工具类的测试
 * @author likangning
 * @since 2018/6/21 上午10:39
 */
public class StringToolsTest {

	/**
	 * 判断某个子字符串在父字符串中出现的次数
	 */
	@Test
	public void countMatchesTest() {
		int num = StringUtils.countMatches("hello world", "o");
		System.out.println(num);
		assertTrue(num == 2);
	}

	/**
	 * 判断字符串是否为空
	 */
	@Test
	public void stringEmptyTest() {
		assertTrue(StringUtils.isEmpty(""));
		assertTrue(StringUtils.isEmpty(null));
		assertTrue(StringUtils.isNotEmpty("abc"));
		assertFalse(StringUtils.isNotEmpty(""));
	}

	/**
	 * 字符串去除两端空格
	 * 只删除两端空格
	 */
	@Test
	public void trimTest() {
		assertTrue(StringUtils.trim("  abc ").equals("abc"));
		assertTrue(StringUtils.trim("  a  b c ").equals("a  b c"));
		assertTrue(StringUtils.trim(null) == null);
		assertTrue(StringUtils.trimToEmpty(null).equals(""));
	}

	/**
	 * 字符串通过分隔符进行拼接
	 */
	@Test
	public void stringAppendTest() {
		String str = StringUtils.joinWith(",", "I", "love", "chinese", "family");
		System.out.println(str);
		assertTrue(str.equals("I,love,chinese,family"));
	}


}

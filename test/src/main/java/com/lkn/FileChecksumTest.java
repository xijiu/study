package com.lkn;

import org.junit.Test;

/**
 * @author likangning
 * @since 2020/5/8 上午10:11
 */
public class FileChecksumTest {

	@Test
	public void aaaaa() throws Exception {
		String sb = "2_1_2_subpath_url";
		System.out.println(extractIndexByKey(sb));
	}

	private Integer extractIndexByKey(String key) {
		String[] split = key.split("_");
		String numStr = "";
		for (String ele : split) {
			char[] chars = ele.toCharArray();
			boolean isNum = true;
			for (char aChar : chars) {
				// 当前字符为非数字
				if (!Character.isDigit(aChar)) {
					isNum = false;
					break;
				}
			}
			if (isNum) {
				numStr += ele;
			} else {
				break;
			}
		}
		return Integer.parseInt(numStr);
	}
}

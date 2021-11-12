package com.lkn;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author likangning
 * @since 2020/5/23 上午8:43
 */
public class User {

	private Long id;

	private String name;

	private List<Role> roles = new ArrayList<>();

	@Test
	public void test() {
		User user = new User();
//		System.out.println(user.compareVersion("1.0", "1.0.0"));
//		System.out.println(user.compareVersion("0.1", "1.1"));
//		System.out.println(user.compareVersion("1.0.1", "1"));
		System.out.println(user.compareVersion("1.01", "1.001"));
//		System.out.println(user.compareVersion("1.0.1", "1"));
	}


	public int compareVersion(String version1, String version2) {
		int num1 = 0;
		int num2 = 0;
		int len1 = version1.length();
		int len2 = version2.length();
		int begin2 = 0;

		for (int i = 0; i < len1; i++) {
			char c1 = version1.charAt(i);
			if (c1 != '.') {
				num1 = num1 * 10 + (c1 - '0');
			} else {
				if (begin2 >= len2) {
					if (num1 > 0) {
						return 1;
					} else {
						num1 = 0;
						continue;
					}
				}
				int j;
				for (j = begin2; j < len2; j++) {
					char c2 = version2.charAt(j);
					if (c2 != '.') {
						num2 = num2 * 10 + (c2 - '0');
					} else {
						if (num1 < num2) {
							return -1;
						} else if (num1 > num2) {
							return 1;
						} else {
							num1 = 0;
							num2 = 0;
						}
						begin2 = j + 1;
						break;
					}
				}
				if (num1 < num2) {
					return -1;
				} else if (num1 > num2) {
					return 1;
				} else {
					num1 = 0;
					num2 = 0;
				}
				if (j >= len2) {
					begin2 = len2;
				}
			}
		}
		int k;
		for (k = begin2; k < len2; k++) {
			char c2 = version2.charAt(k);
			if (c2 != '.') {
				num2 = num2 * 10 + (c2 - '0');
			} else {
				break;
			}
		}
		begin2 = k + 1;
		int compare = Integer.compare(num1, num2);
		if (compare != 0) {
			return compare;
		} else {
			if (begin2 >= len2) {
				return 0;
			}
			num2 = 0;
			for (int j = begin2; j < len2; j++) {
				char c2 = version2.charAt(j);
				if (c2 != '.') {
					num2 = num2 * 10 + (c2 - '0');
				} else {
					if (num2 != 0) {
						return -1;
					}
					num2 = 0;
				}
			}
		}
		if (num2 != 0) {
			return -1;
		} else {
			return 0;
		}
	}

	@Test
	public void test3() {
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		list.add("2");
		list.add("3");

		list.add(1, "4");
		System.out.println(list);
	}
}

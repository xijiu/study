package com.lkn.java8.stream.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author likangning
 * @since 2018/5/21 上午10:59
 */
@Data
@AllArgsConstructor
public class Person {
	private Long id;
	private String name;
	private Integer age;
	/**
	 * 1：男生
	 * 2：女生
	 */
	private Integer sex;
}

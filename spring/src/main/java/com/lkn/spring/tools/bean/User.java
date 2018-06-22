package com.lkn.spring.tools.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author likangning
 * @since 2018/6/21 上午11:11
 */
@Data
@AllArgsConstructor
public class User {
	private Long id;
	private String name;
	private Boolean sex;
}
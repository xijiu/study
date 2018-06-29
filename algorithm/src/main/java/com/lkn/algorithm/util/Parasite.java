package com.lkn.algorithm.util;

import lombok.Data;

/**
 * 寄生虫
 *
 * @author likangning
 * @since 2018/6/27 下午8:47
 */
@Data
public class Parasite<T> {

	private T t;

	public T get() {
		return t;
	}

	public void set(T t) {
		this.t = t;
	}
}

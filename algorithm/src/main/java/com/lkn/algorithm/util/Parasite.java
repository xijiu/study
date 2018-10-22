package com.lkn.algorithm.util;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 寄生虫
 *
 * @author likangning
 * @since 2018/6/27 下午8:47
 */
@Data
@NoArgsConstructor
public class Parasite<T> {

	private T t;

	public Parasite(T t) {
		set(t);
	}

	public T get() {
		return t;
	}

	public void set(T t) {
		this.t = t;
	}
}

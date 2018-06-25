package com.lkn.algorithm.util;

import java.util.Collection;

/**
 * @author likangning
 * @since 2018/6/22 下午3:50
 */
public class PubTools {
	public static boolean isEmptyCollection(Collection<?> targeCollection) {
		return targeCollection == null || targeCollection.size() == 0;
	}
}

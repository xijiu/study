package com.lkn.algorithm.lru;

import com.google.common.base.Objects;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author likangning
 * @since 2019/2/19 下午3:02
 */
public class TestLru {

	private LRU<Integer> lru = new LRU<>();

	@Test
	public void test() {
		lru.add(1);
		lru.add(2);
		lru.add(3);
		lru.add(4);
		Assert.assertTrue(Objects.equal(lru.toString(), "4, 3, 2, 1"));
		lru.add(2);
		Assert.assertTrue(Objects.equal(lru.toString(), "2, 4, 3, 1"));
		lru.add(1);
		Assert.assertTrue(Objects.equal(lru.toString(), "1, 2, 4, 3"));
		lru.add(5);
		Assert.assertTrue(Objects.equal(lru.toString(), "5, 1, 2, 4"));
		lru.add(6);
		Assert.assertTrue(Objects.equal(lru.toString(), "6, 5, 1, 2"));
		lru.add(2);
		Assert.assertTrue(Objects.equal(lru.toString(), "2, 6, 5, 1"));
		lru.add(1);
		Assert.assertTrue(Objects.equal(lru.toString(), "1, 2, 6, 5"));
		lru.add(2);
		Assert.assertTrue(Objects.equal(lru.toString(), "2, 1, 6, 5"));
		lru.add(3);
		Assert.assertTrue(Objects.equal(lru.toString(), "3, 2, 1, 6"));
		lru.add(7);
		Assert.assertTrue(Objects.equal(lru.toString(), "7, 3, 2, 1"));
		lru.add(6);
		Assert.assertTrue(Objects.equal(lru.toString(), "6, 7, 3, 2"));
		lru.add(3);
		Assert.assertTrue(Objects.equal(lru.toString(), "3, 6, 7, 2"));
		lru.add(2);
		Assert.assertTrue(Objects.equal(lru.toString(), "2, 3, 6, 7"));
		lru.add(1);
		Assert.assertTrue(Objects.equal(lru.toString(), "1, 2, 3, 6"));
		lru.add(2);
		Assert.assertTrue(Objects.equal(lru.toString(), "2, 1, 3, 6"));
		lru.add(3);
		Assert.assertTrue(Objects.equal(lru.toString(), "3, 2, 1, 6"));
		lru.add(6);
		Assert.assertTrue(Objects.equal(lru.toString(), "6, 3, 2, 1"));
	}
}

package com.lkn;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lkn.ai.uct.Slot;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Unit test for simple App.
 */
public class AppTest {

	@Test
	public void shouldAnswerWithTrue() {
		List<Bean> set = Lists.newArrayList();
		Bean bean = new Bean(3);
		set.add(new Bean(9));
		set.add(bean);
		set.add(new Bean(5));
		bean.setId(10);

		Collections.sort(set);
		System.out.println(set);
	}

	@Data
	@AllArgsConstructor
	private class Bean implements Comparable<Bean> {
		private int id;

		@Override
		public int compareTo(Bean o) {
			return Integer.compare(this.id, o.id);
		}
	}

	@Test
	public void test() {
		List<Slot> list = Lists.newArrayList();
		Slot slot1 = Slot.newInstance(1);
		slot1.setPlayTimes(0);

		Slot slot2 = Slot.newInstance(2);
		slot2.setPlayTimes(1);

		list.add(slot1);
		list.add(slot2);
		Collections.sort(list);
		System.out.println(list);
	}

	@Test
	public void calcTest() {
		print(new BigDecimal(3));
		print(new BigDecimal(3.0000000010000001D));
		print(new BigDecimal(4));
		print(new BigDecimal(5));
	}

	private void print(BigDecimal x) {
		BigDecimal y = calc(x);
		System.out.println("[" + x + "," + y + "]");
	}

	/**
	 * 二次方程求解
	 * y = 4x*x + 2x + 9
	 */
	private BigDecimal calc(BigDecimal x) {
		BigDecimal result1 = x.multiply(x).multiply(new BigDecimal(4));
		BigDecimal result2 = x.multiply(new BigDecimal(2));
		return result1.add(result2).add(new BigDecimal(9));
	}


}

package com.lkn.ai.uct;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * @author likangning
 * @since 2018/9/5 上午9:16
 */
public class Uct {

	/** 1-置信区间最大值     2-平均最大值 */
	public static int TYPE = 1;

	/** 老虎机的数量 */
	private static int SLOT_NUMBER = 10;

	/** 预计打算玩的总次数 */
	private static int PLAN_TOTAL_TIMES = 200;

	/** 每次玩的金额 */
	private static int PER_MONEY = 1;

	/** 所有的老虎机 */
	private static List<Slot> SLOTS;

	@Test
	public void batchPlayGame() {
		int times = 10000;
		int totalReward = 0;
		for (int i = 0; i < times; i++) {
			beginGame();
			totalReward = totalReward + statMoney();
		}
		System.out.println("平均收益: " + (double)totalReward / times);
	}


	@Test
	public void beginGame() {
		initSlots();
		play();
		int totalReward = statMoney();
		System.out.println("总收益：" + totalReward);
	}

	private int statMoney() {
		int totalReward = 0;
		for (Slot slot : SLOTS) {
			totalReward = totalReward + slot.getReward();
		}
		return totalReward;
	}

	private void play() {
		for (int i = 0; i < PLAN_TOTAL_TIMES; i++) {
			Slot maxSlot = getMaxConfidenceSlot();
			maxSlot.play(PER_MONEY);
//			if (i >= 9) {
//				printConfidence();
//			}
		}
//		System.out.println(SLOTS);
	}

	private void printConfidence() {
		List<Bean> list = Lists.newArrayList();
		for (Slot slot : SLOTS) {
			list.add(new Bean(slot.getNumber(), slot.getMaxConfidence()));
		}
		Collections.sort(list);
		System.out.println("置信排名：" + list);
	}

	private void initSlots() {
		if (SLOTS != null && SLOTS.size() > 0) {
			SLOTS.clear();
		}
		SLOTS = Lists.newArrayListWithExpectedSize(SLOT_NUMBER);
		for (int i = 0; i < SLOT_NUMBER; i++) {
			SLOTS.add(Slot.newInstance(i + 1));
		}
	}

	/**
	 * 获取置信度最高的老虎机
	 */
	private Slot getMaxConfidenceSlot() {
		Collections.sort(SLOTS);
		return SLOTS.get(0);
	}

	@Data
	@AllArgsConstructor
	private class Bean implements Comparable<Bean> {
		private int number;
		private double confidence;

		@Override
		public int compareTo(Bean o) {
			return Double.compare(o.getConfidence(), this.getConfidence());
		}
	}
}

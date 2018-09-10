package com.lkn.ai.uct;

import lombok.Data;
import org.apache.commons.lang3.RandomUtils;

/**
 * 老虎机
 *
 * @author likangning
 * @since 2018/9/5 上午9:16
 */
@Data
public class Slot implements Comparable<Slot> {

	/** 玩老虎机的总次数 */
	private static int PLAY_TOTAL_TIMES = 0;

	/** 编号 */
	private int number;

	/** 玩该老虎机的次数 */
	private int playTimes = 0;

	/** 该老虎机总回报值 */
	private int reward = 0;

	/** 该老虎机的回报率 20% - 80% */
	private double rewardPercent;

	/** 在该老虎机上的总支出 */
	private double payout = 0D;

	private Slot() {
	}

	public static Slot newInstance(int number) {
		Slot slot = new Slot();
		slot.number = number;
		slot.rewardPercent = randomPercent(number);
		return slot;
	}

	private static double randomPercent(int number) {
//		return RandomUtils.nextInt(20, 80) / (double)100;
		return (double)(number * 5) / (double)100;
	}

	/**
	 * 如果命中，那么返回双倍金钱，否则返回0
	 * @param money	投入金钱
	 * @return	回报金额
	 */
	public int play(int money) {
		int currentReward;
		double random = RandomUtils.nextDouble(0.0D, 1.0D);
		if (random <= rewardPercent) {
			currentReward = money * 10;
		} else {
			currentReward = 0;
		}
		System.out.println("投资老虎机 " + number + "，投入金额 " + money + "，获得回报 " + currentReward);
		payout = payout + money;
		reward = reward + currentReward;
		playTimes++;
		PLAY_TOTAL_TIMES++;
		return currentReward;
	}

	/**
	 * 该老虎机置信区间的最大值
	 * @return	置信区间最大值
	 */
	public double getMaxConfidence() {
		if (Uct.TYPE == 1) {
//			printConfidence();
			// 返回最大置信值
//			return (double)reward / (double)playTimes + Math.sqrt(Math.log(2 * (PLAY_TOTAL_TIMES) / playTimes));	// 非正宗
			return (double)reward / (double)playTimes + Math.sqrt(2 * Math.log(PLAY_TOTAL_TIMES) / playTimes);	// 正宗
		} else {
			// 返回最大平均值
			return (double)reward / (double)playTimes;
		}
	}

	private void printConfidence() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(payout / (double)playTimes - Math.sqrt((double)2 * Math.log(PLAY_TOTAL_TIMES) / playTimes));
		sb.append(",");
		sb.append(payout / (double)playTimes + Math.sqrt((double)2 * Math.log(PLAY_TOTAL_TIMES) / playTimes));
		sb.append("]");
		System.out.println(number + " 置信区间：" + sb.toString());
	}

	@Override
	public int compareTo(Slot o) {
		if (this.getPlayTimes() == 0 && o.getPlayTimes() > 0) {
			return -1;
		} else if (this.getPlayTimes() > 0 && o.getPlayTimes() == 0) {
			return 1;
		} else if (this.getPlayTimes() == 0 && o.getPlayTimes() == 0) {
			return 0;
		} else {
			return -Double.compare(this.getMaxConfidence(), o.getMaxConfidence());
		}
	}
}

package com.lkn.mode.visitor.viewer;

import com.lkn.mode.visitor.bill.ConsumeBill;
import com.lkn.mode.visitor.bill.IncomeBill;

/**
 * @author likangning
 * @since 2018/6/20 上午9:09
 */
public class Boss implements Viewer {

	@Override
	public void view(ConsumeBill bill) {
		System.out.println("老板正在计算总消费数目。。。当前正在观看支出账单：" + bill.getItem());
	}

	@Override
	public void view(IncomeBill bill) {
		System.out.println("老板正在计算总收入数目。。。当前正在观看收入账单：" + bill.getItem());
	}

}

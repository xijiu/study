package com.lkn.mode.visitor.viewer;

import com.lkn.mode.visitor.bill.ConsumeBill;
import com.lkn.mode.visitor.bill.IncomeBill;

/**
 * @author likangning
 * @since 2018/6/20 上午9:09
 */
public class CPA implements Viewer {

	@Override
	public void view(ConsumeBill bill) {
		System.out.println("注会查看工资是否交个人所得税，当前账单：" + bill.getItem());
	}

	@Override
	public void view(IncomeBill bill) {
		System.out.println("注会查看每一项收入详情，当前账单：" + bill.getItem());
	}

}

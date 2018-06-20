package com.lkn.mode.visitor.viewer;

import com.lkn.mode.visitor.bill.ConsumeBill;
import com.lkn.mode.visitor.bill.IncomeBill;

/**
 * @author likangning
 * @since 2018/6/20 上午9:08
 */
public interface Viewer {

	//查看消费的单子
	void view(ConsumeBill bill);

	//查看收入的单子
	void view(IncomeBill bill);

}

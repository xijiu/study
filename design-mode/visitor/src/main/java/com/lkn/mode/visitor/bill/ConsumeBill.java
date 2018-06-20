package com.lkn.mode.visitor.bill;

import com.lkn.mode.visitor.viewer.Viewer;
import lombok.Getter;

/**
 * 一条消费账单
 *
 * @author likangning
 * @since 2018/6/20 上午9:08
 */
public class ConsumeBill implements Bill{

	@Getter
	private double amount;

	@Getter
	private String item;

	public ConsumeBill(double amount, String item) {
		super();
		this.amount = amount;
		this.item = item;
	}

	@Override
	public void accept(Viewer viewer) {
		viewer.view(this);
	}


}

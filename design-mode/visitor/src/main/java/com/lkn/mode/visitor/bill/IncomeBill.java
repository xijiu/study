package com.lkn.mode.visitor.bill;

import com.lkn.mode.visitor.viewer.Viewer;
import lombok.Getter;

/**
 * 一条收入账单
 *
 * @author likangning
 * @since 2018/6/20 上午9:08
 */
public class IncomeBill implements Bill{

	@Getter
	private double amount;

	@Getter
	private String item;

	public IncomeBill(double amount, String item) {
		super();
		this.amount = amount;
		this.item = item;
	}

	@Override
	public void accept(Viewer viewer) {
		viewer.view(this);
	}

}

package com.lkn.mode.visitor;

import com.lkn.mode.visitor.bill.Bill;
import com.lkn.mode.visitor.viewer.Viewer;

import java.util.ArrayList;
import java.util.List;

/**
 * 账本
 * @author likangning
 * @since 2018/6/20 上午9:09
 */
public class AccountBook {
	// 账本中会有多条账单
	private List<Bill> billList = new ArrayList<>();

	// 添加一条账单
	public void addBill(Bill bill){
		billList.add(bill);
	}

	// 供账本的查看者查看账本
	public void show(Viewer viewer){
		for (Bill bill : billList) {
			bill.accept(viewer);
		}
	}
}

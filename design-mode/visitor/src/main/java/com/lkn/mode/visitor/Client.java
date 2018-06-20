package com.lkn.mode.visitor;

import com.lkn.mode.visitor.bill.ConsumeBill;
import com.lkn.mode.visitor.bill.IncomeBill;
import com.lkn.mode.visitor.viewer.Viewer;
import com.lkn.mode.visitor.viewer.Boss;
import com.lkn.mode.visitor.viewer.CPA;

/**
 * @author likangning
 * @since 2018/6/20 上午9:10
 */
public class Client {

	public static void main(String[] args) {
		AccountBook accountBook = new AccountBook();
		// 创建账本数据
		createData(accountBook);


		Viewer boss = new Boss();
		Viewer cpa = new CPA();

		//两个访问者分别访问账本
		accountBook.show(boss);
		accountBook.show(cpa);
	}

	private static void createData(AccountBook accountBook) {
		//添加两条收入
		accountBook.addBill(new IncomeBill(10000, "卖商品"));
		accountBook.addBill(new IncomeBill(12000, "卖广告位"));

		//添加两条支出
		accountBook.addBill(new ConsumeBill(1000, "工资"));
		accountBook.addBill(new ConsumeBill(2000, "材料费"));
	}
}

package com.lkn.mode.visitor.bill;

import com.lkn.mode.visitor.viewer.Viewer;

/**
 * 一条账单
 * @author likangning
 * @since 2018/6/20 上午9:08
 */
public interface Bill {

	void accept(Viewer viewer);

}

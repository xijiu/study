package com.lkn.ai.neural_network;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;

import java.util.Random;

/**
 * @author likangning
 * @since 2018/9/10 上午11:37
 */
public class BaseTest {

	/**
	 * 生成一个row行col列的随机矩阵
	 * @param row	行数
	 * @param col	列数
	 * @return	随机矩阵
	 */
	protected RealMatrix randomMatrix(int row, int col) {
		double[][] randomArr = new double[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				randomArr[i][j] = new Random().nextDouble();
				if (RandomUtils.nextBoolean()) {
					randomArr[i][j] = randomArr[i][j] * -1;
				}
			}
		}
		return new Array2DRowRealMatrix(randomArr);
	}

	@Test
	public void test() {

		for (int i = 0; i < 10; i++) {
			RealMatrix realMatrix = randomMatrix(1, 3);
			System.out.println(realMatrix);
		}
	}
}

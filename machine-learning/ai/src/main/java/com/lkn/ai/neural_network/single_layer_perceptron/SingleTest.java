package com.lkn.ai.neural_network.single_layer_perceptron;

import com.lkn.ai.neural_network.BaseTest;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Before;
import org.junit.Test;

/**
 * @author likangning
 * @since 2018/9/10 上午9:54
 */
public class SingleTest extends BaseTest {

	/** 输入值 */
	private RealMatrix input;

	/** 最后结果，例如 1、-1 */
	private RealMatrix result;

	/** 权值，随机生成 */
	private RealMatrix weight;

	/** 学习率 */
	private double learnRate = 0.11D;

	/**
	 * 运行前数据准备
	 */
	@Before
	public void prepareData() {
		double[][] inputValue = {
				{1, 3, 3},
				{1, 4, 3},
				{1, 1, 1}
		};
		input = new Array2DRowRealMatrix(inputValue);

		double[][] resultValue = {
				{1, 1, -1}
		};
		result = new Array2DRowRealMatrix(resultValue);
		weight = randomMatrix(1, 3);
	}

	@Test
	public void begin() {
		RealMatrix tmpResult = input.multiply(weight.transpose());
		// 标记正负的矩阵
		RealMatrix signMatrix = sign(tmpResult);
	}

	private RealMatrix sign(RealMatrix targetMatrix) {
		int row = targetMatrix.getRowDimension();
		int col = targetMatrix.getColumnDimension();
		double[][] sign = new double[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				double entry = targetMatrix.getEntry(i, j);
				sign[i][j] = entry == 0D ? 0 : (entry > 0 ? 1 : -1);
			}
		}
		return new Array2DRowRealMatrix(sign);
	}
}

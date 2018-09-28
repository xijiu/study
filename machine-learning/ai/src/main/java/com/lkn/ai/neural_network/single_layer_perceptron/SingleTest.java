package com.lkn.ai.neural_network.single_layer_perceptron;

import com.lkn.ai.neural_network.BaseTest;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * 单层传感器测试用例
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
		// 输入参数
		double[][] inputValue = {
				{1, 3, 3},
				{1, 4, 3},
				{1, 1, 1}
		};
		input = new Array2DRowRealMatrix(inputValue);

		// 输入参数的目标结果
		double[][] resultValue = {
				{1, 1, -1}
		};
		result = new Array2DRowRealMatrix(resultValue);
		// 随机生成一个1行3列的矩阵
		weight = randomMatrix(1, 3);
	}

	@Test
	public void begin() {
		System.out.println("初始随机权重");
		printMatrix(weight);
		for (int i = 0; i < 100; i++) {
			System.out.println("当前循环次数" + i + "------------------------------------------------------------");
			if (update()) {
				break;
			}
		}
	}

	private boolean update() {
		// 神经网络的计算
		RealMatrix tmpResult = input.multiply(weight.transpose());
		// 标记正负的矩阵
		RealMatrix signMatrix = sign(tmpResult);
		System.out.println("经过计算后的矩阵");
		printMatrix(signMatrix);
		if (isMatrixEquals(signMatrix, result.transpose())) {
			return true;
		} else {
			RealMatrix inputChange = result.subtract(signMatrix.transpose()).multiply(input).scalarMultiply(learnRate).scalarMultiply((double) 1 / input.getRowDimension());
			System.out.println("******变化矩阵内容******");
			printMatrix(weight);
			weight = weight.add(inputChange);
			printMatrix(weight);
			return false;
		}
	}
}

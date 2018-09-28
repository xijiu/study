package com.lkn.ai.neural_network.linear_perceptron;

import com.lkn.ai.neural_network.BaseTest;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Before;
import org.junit.Test;

/**
 * @author likangning
 * @since 2018/9/14 下午12:07
 */
public class LinearTest extends BaseTest {

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
				{1, 0, 0, 0, 0, 0},
				{1, 0, 1, 0, 0, 1},
				{1, 1, 0, 1, 0, 0},
				{1, 1, 1, 1, 1, 1}
		};
		input = new Array2DRowRealMatrix(inputValue);

		// 输入参数的目标结果
		double[][] resultValue = {
				{-1, 1, 1, -1}
		};
		result = new Array2DRowRealMatrix(resultValue);
		// 随机生成一个1行6列的矩阵
		weight = randomMatrix(1, 6);
	}

	@Test
	public void begin() {
		System.out.println("初始随机权重");
		printMatrix(weight);
		for (int i = 0; i < 10000; i++) {
			if (update()) {
				break;
			}
		}
//		double a = weight.getEntry(0, 5);
//		double b = weight.getEntry(0, 2) + ;

		System.out.println("最终权重");
		printMatrix(weight);
		System.out.println("最终线性运行结果：");
		RealMatrix result = input.multiply(weight.transpose());
		printMatrix(result);
	}

	private boolean update() {
		// 神经网络的计算
		RealMatrix tmpResult = input.multiply(weight.transpose());
		// 线性神经网络的激活函数：y = x
		RealMatrix signMatrix = tmpResult;
		RealMatrix inputChange = result.subtract(signMatrix.transpose()).multiply(input).scalarMultiply(learnRate).scalarMultiply((double) 1 / input.getRowDimension());
		weight = weight.add(inputChange);
		return false;
	}

}

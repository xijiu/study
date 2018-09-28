package com.lkn.ai.neural_network;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;

import java.util.Arrays;
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

	protected void printMatrix(RealMatrix matrix) {
		int rowDimension = matrix.getRowDimension();
		for (int i = 0; i < rowDimension; i++) {
			System.out.println(Arrays.toString(matrix.getRow(i)));
		}
	}

	protected boolean isMatrixEquals(RealMatrix matrix1, RealMatrix matrix2) {
		if (matrix1 != null && matrix2 != null) {
			int rowDimension = matrix1.getRowDimension();
			int columnDimension = matrix1.getColumnDimension();
			if (matrix2.getRowDimension() == rowDimension && matrix2.getColumnDimension() == columnDimension) {
				for (int i = 0; i < rowDimension; i++) {
					for (int j = 0; j < columnDimension; j++) {
						if (matrix1.getEntry(i, j) != matrix2.getEntry(i, j)) {
							return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	protected RealMatrix sign(RealMatrix targetMatrix) {
		int row = targetMatrix.getRowDimension();
		int col = targetMatrix.getColumnDimension();
		double[][] sign = new double[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				double entry = targetMatrix.getEntry(i, j);
				sign[i][j] = entry >= 0 ? 1 : -1;
			}
		}
		return new Array2DRowRealMatrix(sign);
	}

	@Test
	public void test() {

		for (int i = 0; i < 10; i++) {
			RealMatrix realMatrix = randomMatrix(1, 3);
			System.out.println(realMatrix);
		}
	}
}

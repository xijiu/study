package com.lkn.common.math;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * 输入一个四则运算的String类型的表达式，计算出其结果
 * @author likangning
 * @since 2018/5/17 上午9:07
 */
public class Arithmetic {
	private String expression = "2*(2*5.3+2.8)-4/(8.2*(5-3))";
//	private String expression = "100-23*2";
	private Stack<String> stack = new Stack<>();

	@Test
	public void test() {
		double a = 2*(2*5.3+2.8)-4/(8.2*(5-3));
		System.out.println(a);
		BigDecimal bigDecimal = doCalc(expression);
		System.out.println("result is : " + bigDecimal);
	}

	@Test
	public void test2() {
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			doCalc(expression);
			stack = new Stack<>();
		}
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}

	/**
	 * 执行计算
	 * @param expression	4则运算表达式
	 * @return	数字
	 */
	private BigDecimal doCalc(String expression) {
		expression = expression.replace(" ", "");
		char[] chars = expression.toCharArray();
		StringBuilder element = new StringBuilder();
		for (char c : chars) {
			if (isNumberOrDot(c)) {
				element.append(c);
			} else {
				if (element.length() > 0) {
					stack.push(element.toString());
					element = new StringBuilder();
				}
				stack.push(String.valueOf(c));
				if (Objects.equal(Operator.RIGHT_BRACKET.key, String.valueOf(c))) {
					popStack();
				}
			}
		}
		if (element.length() > 0) {
			stack.push(element.toString());
		}

		List<String> finalList = Lists.newArrayList(stack.toArray(new String[stack.size()]));
		return calcWithoutBracket(finalList);
	}

	private void popStack() {
		List<String> list = Lists.newArrayList();
		String element;
		while (!Objects.equal((element = stack.pop()), Operator.LEFT_BRACKET.key)) {
			if (!Objects.equal(element, Operator.RIGHT_BRACKET.key)) {
				list.add(element);
			}
		}
		// 列表反转
		Collections.reverse(list);
		BigDecimal tempResult = calcWithoutBracket(list);
		stack.push(tempResult.toString());
	}


	/**
	 * 计算一个不带括号的表达式
	 * @param list
	 * @return
	 */
	private BigDecimal calcWithoutBracket(List<String> list) {
		// 最简单的情况，不存在优先级的问题，直接进行操作
		if (list.size() == 3) {
			return calcTwoNumbers(list.get(0), list.get(1), list.get(2));
		} else {
			Operator front = Operator.getOperator(list.get(1));
			Operator behind = Operator.getOperator(list.get(3));
			if (front.level >= behind.level) {
				String left = list.remove(0);
				String operatorStr = list.remove(0);
				String right = list.remove(0);
				BigDecimal result = calcWithoutBracket(Arrays.asList(left, operatorStr, right));
				list.add(0, result.toString());
				return calcWithoutBracket(list);
			} else {
				String left = list.remove(2);
				String operatorStr = list.remove(2);
				String right = list.remove(2);
				BigDecimal result = calcWithoutBracket(Arrays.asList(left, operatorStr, right));
				list.add(2, result.toString());
				return calcWithoutBracket(list);
			}
		}
	}

	/**
	 * 是否是数字或小数点
	 */
	private boolean isNumberOrDot(char c) {
		return isNumber(c) || isDot(c);
	}

	/**
	 * 是否是数字
	 */
	private boolean isNumber(char c) {
		return c == '0' || c == '1' || c == '2' || c == '3' || c == '4'
						|| c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
	}

	/**
	 * 是否是小数点
	 */
	private boolean isDot(char c) {
		return c == '.';
	}

	/**
	 * 两个数字的计算
	 * @param leftNumber
	 * @param operator
	 * @param rightNumber
	 * @return
	 */
	private BigDecimal calcTwoNumbers(String leftNumber, String operator, String rightNumber) {
		Operator operatorEnum = Operator.getOperator(operator);
		switch (operatorEnum) {
			case ADD:
				return new BigDecimal(leftNumber).add(new BigDecimal(rightNumber));
			case SUBTRACT:
				return new BigDecimal(leftNumber).subtract(new BigDecimal(rightNumber));
			case MULTIPLY:
				return new BigDecimal(leftNumber).multiply(new BigDecimal(rightNumber));
			case DIVIDE:
				return new BigDecimal(leftNumber).divide(new BigDecimal(rightNumber), 2, BigDecimal.ROUND_HALF_DOWN);
			default:
				throw new RuntimeException();
		}
	}

	/**
	 * 操作符的枚举
	 */
	private enum Operator {
		ADD("+", 1),
		SUBTRACT("-", 1),
		MULTIPLY("*", 2),
		DIVIDE("/", 2),
		LEFT_BRACKET("(", 4),
		RIGHT_BRACKET(")", 4),
		;

		// 符号描述
		private String key;
		// 优先级，该值越大，代表优先级越高
		private int level;

		Operator(String key, int level) {
			this.key = key;
			this.level = level;
		}

		public static Operator getOperator(String key) {
			Operator[] values = Operator.values();
			for (Operator operator : values) {
				if (Objects.equal(operator.key, key)) {
					return operator;
				}
			}
			throw new IllegalArgumentException("wrong key params : " + key);
		}
	}

}

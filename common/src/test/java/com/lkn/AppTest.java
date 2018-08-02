package com.lkn;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;

/**
 * Unit test for simple App.
 */
public class AppTest {
	@Test
	public void shouldAnswerWithTrue() {
		BigDecimal divide = BigDecimal.valueOf(100).divide(BigDecimal.valueOf(0), 2, RoundingMode.HALF_UP);

		System.out.println(divide);
	}

	@Test
	public void test() {
		String column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "Spring__Test");
		System.out.println(column);
	}

	private boolean isCollectionNullOrEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

}

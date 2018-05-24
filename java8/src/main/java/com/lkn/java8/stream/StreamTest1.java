package com.lkn.java8.stream;

import com.lkn.java8.stream.bean.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author likangning
 * @since 2018/5/21 上午10:59
 */
public class StreamTest1 {
	private List<Person> personList = new ArrayList<>();

	@Before
	public void prepareData() {
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			personList.add(new Person(Long.valueOf(i), "name" + i, (int)(Math.random() * 100), random.nextBoolean() ? 1 : 2));
		}
	}


	/**
	 * 双冒号的用法
	 * 以下三种方式等效
	 */
	@Test
	public void doubleColon() {
		// 方式1： java1.7及之前的调用方式，该方式在代码上较为啰嗦
		Stream<Person> stream1 = personList.stream();
		stream1.forEach(new Consumer<Person>() {
			@Override
			public void accept(Person person) {
				System.out.println("方式1：" + person);
			}
		});


		// 方式2：引入lambda
		Stream<Person> stream2 = personList.stream();
		stream2.forEach(person -> System.out.println("方式2：" + person));


		// 方式3：采用"::"，语法更加简洁
		// 不过此种方式不能应对入参可变的情形，如方式2
		// 故方式2与方式3必将共存
		Stream<Person> stream3 = personList.stream();
		stream3.forEach(System.out::println);
	}

	/**
	 * 过滤实例
	 */
	@Test
	public void filter() {
		System.out.println(personList);
		Stream<Person> stream = personList.stream();
		stream = stream.filter(StreamTest1::isBoy);
		stream.forEach(System.out::println);
	}

	/**
	 * 数据类型转换实例
	 * map主要用于数据结构类型的转换
	 */
	@Test
	public void map() {
		System.out.println(personList);
		Stream<Person> stream = personList.stream();
		stream = stream.filter(StreamTest1::isBoy);
		Stream<Long> idStream = stream.map(Person::getId);
		idStream.forEach(id -> System.out.println(id));
	}

	/**
	 * 计算总数
 	 */
	@Test
	public void count() {
		System.out.println(personList);
		Stream<Person> stream = personList.stream();
		stream = stream.filter(StreamTest1::isBoy);
		System.out.println(stream.count());
	}


	/**
	 * 集合set的搜集例子
	 */
	@Test
	public void setCollector() {
		System.out.println(personList);
		Stream<Person> stream = personList.stream();
		stream = stream.filter(StreamTest1::isBoy);
		Set<Person> collect = stream.collect(Collectors.toSet());
		System.out.println("set is : " + collect);
	}

	/**
	 * map的搜集例子
	 */
	@Test
	public void mapCollector() {
		System.out.println(personList);
		Stream<Person> stream = personList.stream();
		stream = stream.filter(StreamTest1::isBoy);
		Map<Long, Person> map = stream.collect(Collectors.toMap(Person::getId, person -> person));
		System.out.println("map is : " + map);
	}

	private static boolean isBoy(Person person) {
		return person != null && person.getSex().equals(1);
	}
}

package com.lkn.rpc;

/**
 * @author likangning
 * @since 2018/6/7 下午8:10
 */
public class HelloServiceImpl implements HelloService {

	public String hello(String name) {
		return "Hello " + name;
	}

}

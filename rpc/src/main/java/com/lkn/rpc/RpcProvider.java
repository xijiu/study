package com.lkn.rpc;

/**
 * @author likangning
 * @since 2018/6/7 下午8:10
 */
public class RpcProvider {

	public static void main(String[] args) throws Exception {
		HelloService service = new HelloServiceImpl();
		RpcFramework.export(service, 1234);
	}

}

package org.zollty.tool.proxydemo;

import java.lang.reflect.Proxy;

import org.jretty.util.ArrayUtils;

/**
 * 将我的MyService实例对象转为EchoService对象 ，并调用其$echo方法
 * 
 * 这样做的目的在于，可以给任何一个API附加一个$echo方法，外部应用都可以调用。
 * 对RPC 服务化的API来说，是非常有用的，相当于可以给所以API增加方法
 */
public class Test {

	public static void main(String[] args) {

		MyServiceImpl target = new MyServiceImpl();

		Class<?>[] interfaces = new Class<?>[] { EchoService.class };
		interfaces = ArrayUtils.addAll(interfaces, target.getClass().getInterfaces());

		Object obj = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), 
				interfaces,
				new MyInvocationHandler(target));

		MyService myService = (MyService) obj;
		System.out.println("-----------------------------------");
		myService.sayHello();

		EchoService echoService = (EchoService) obj;
		System.out.println("++++++++++++++++++++++++++++++++++++");
		Object result = echoService.$echo("hello echo");
		System.out.println(result.toString());
	}

}
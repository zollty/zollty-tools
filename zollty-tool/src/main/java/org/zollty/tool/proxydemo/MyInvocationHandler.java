package org.zollty.tool.proxydemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvocationHandler implements InvocationHandler {

	private Object target;
	
	public MyInvocationHandler(Object target) {
		this.target = target;
	}

	// 调用方法时，会首先执行此invoke
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		// 如果是 $echo 方法则直接返回传入的第一个参数，否则执行该方法
		if (method.getName().equals("$echo")) {
			return args[0];
		}
		return method.invoke(target, args);
	}

}
package com.practice.JdkDynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK动态代理实现类
 * 实现了InvocationHandler接口，用于创建动态代理对象并处理方法调用
 */
public class JdkDynamicProxy implements InvocationHandler {
    private Object target;

    /**
     * 获取目标对象的代理对象
     * @param target 目标对象，需要被代理的对象实例
     * @return 返回目标对象的代理对象
     */
    public Object getProxy(Object target){
        this.target = target;
        // 创建并返回代理对象
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                this
        );
    }

    /**
     * 代理对象的方法调用处理器
     * 在目标方法执行前后添加增强逻辑
     * @param proxy 代理对象本身
     * @param method 被调用的方法对象
     * @param args 方法调用时传入的参数数组
     * @return 返回目标方法执行的结果
     * @throws Throwable 方法执行过程中可能抛出的异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("前置加强");
        // 执行目标对象的方法
        Object result = method.invoke(target,args);
        System.out.println("后置加强");
        return result;
    }

    /**
     * 主方法，用于测试JDK动态代理功能
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 创建UserService的代理对象并调用save方法
        UserService proxy = (UserService) new JdkDynamicProxy().getProxy(new UserServiceImpl());
        proxy.save();
    }
}

package com.practice.CglibDynamicProxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLIB动态代理实现类
 * 实现MethodInterceptor接口，用于创建CGLIB代理对象并处理方法拦截
 */
public class CglibProxy implements MethodInterceptor {

    /**
     * 创建指定类的CGLIB代理对象
     * @param clazz 需要被代理的类的Class对象
     * @return 返回代理对象实例
     */
    public Object getProxy(Class<?> clazz){
        // 创建Enhancer对象，用于生成代理类
        Enhancer enhancer = new Enhancer();
        // 设置父类，即被代理的类
        enhancer.setSuperclass(clazz);
        // 设置回调对象，即当前实例
        enhancer.setCallback(this);
        // 创建并返回代理对象
        return enhancer.create();
    }

    /**
     * 方法拦截器，处理代理对象的方法调用
     * @param o 被代理的对象
     * @param method 被拦截的方法
     * @param objects 方法参数数组
     * @param methodProxy 方法代理对象
     * @return 原方法执行结果
     * @throws Throwable 方法执行可能抛出的异常
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("前置增强");
        // 调用父类（被代理类）的方法
        Object result = methodProxy.invokeSuper(o, objects);
        System.out.println("后置增强");
        return result;
    }

    /**
     * 主方法，用于测试CGLIB动态代理功能
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 创建UserService的代理对象
        UserService proxy = (UserService) new CglibProxy().getProxy(UserService.class);
        // 调用代理对象的方法
        proxy.save();
    }
}


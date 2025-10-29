package com.dynamicproxy.ProxyFactory;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 自动代理创建工厂类，根据目标对象是否实现接口自动选择JDK动态代理或CGLIB动态代理。
 */
public class AutoProxyCreator {

    /**
     * 根据目标对象是否有实现接口来决定使用哪种代理方式创建代理对象。
     * 如果目标对象实现了至少一个接口，则使用JDK动态代理；否则使用CGLIB动态代理。
     *
     * @param target 被代理的目标对象，不能为null
     * @return 返回生成的代理对象
     */
    public static Object createProxy(Object target){
        // 判断目标对象是否实现了接口
        if (target.getClass().getInterfaces().length > 0){
            //如果有接口，使用jdk动态代理
            System.out.println("检测到接口，使用JDK动态代理");
            return Proxy.newProxyInstance(
                    target.getClass().getClassLoader(),
                    target.getClass().getInterfaces(),
                    new JdkHandler(target)
            );
        }else {
            //无接口 使用CGLIB动态代理（基于继承目标类）
            System.out.println("未检测到接口，使用CGLIB动态代理");
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(target.getClass());
            enhancer.setCallback(new CglibInterceptor(target));
            return enhancer.create();
        }
    }

    /**
     * JDK动态代理处理器类，负责处理通过JDK动态代理生成的代理对象的方法调用。
     */
    static class JdkHandler implements InvocationHandler{
        private final Object target;

        /**
         * 构造方法，初始化被代理的目标对象。
         *
         * @param target 被代理的目标对象
         */
        public JdkHandler(Object target){
            this.target = target;
        }

        /**
         * 实现InvocationHandler接口的方法，在代理对象调用方法时会被触发。
         *
         * @param proxy  代理对象本身
         * @param method 被调用的方法对象
         * @param args   方法调用时传入的参数数组
         * @return 原方法执行后的返回结果
         * @throws Throwable 方法执行过程中可能抛出的异常
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("【代理】jdk代理前置通知" + method.getName());
            Object result = method.invoke(target, args);
            return result;
        }
    }

    /**
     * CGLIB方法拦截器类，用于处理通过CGLIB生成的代理对象的方法调用。
     */
    static class CglibInterceptor implements MethodInterceptor{
        private final Object target;

        /**
         * 构造方法，初始化被代理的目标对象。
         *
         * @param target 被代理的目标对象
         */
        public CglibInterceptor(Object target) {
            this.target = target;
        }

        /**
         * 实现MethodInterceptor接口的方法，在代理对象调用方法时会被触发。
         *
         * @param o           由CGLIB生成的代理对象
         * @param method      被调用的方法对象
         * @param objects     方法调用时传入的参数数组
         * @param methodProxy CGLIB提供的方法代理对象，可以用来调用父类的方法
         * @return 原方法执行后的返回结果
         * @throws Throwable 方法执行过程中可能抛出的异常
         */
        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            System.out.println("【代理】CGLIB代理前置处理");
            Object result = methodProxy.invokeSuper(o, objects);
            return result;
        }
    }
}

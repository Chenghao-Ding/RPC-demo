package com.dch.proxy;

import com.dch.common.Invocation;
import com.dch.common.URL;
import com.dch.loadbalance.Loadbalance;
import com.dch.protocal.HttpClient;
import com.dch.register.MapRemoteRegister;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class ProxyFactory {

    public static <T> T getProxy(final Class interfaceClass) {
        // 用户配置

        Object proxyInstance = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                Invocation invocation = new Invocation(interfaceClass.getName(), method.getName(),
                        method.getParameterTypes(), args);

                HttpClient httpClient = new HttpClient();
                //服务mock等

                // 服务发现
                List<URL> list = MapRemoteRegister.get(interfaceClass.getName());

                URL url = Loadbalance.random(list);

                // 服务调用
                String result = null;

                //逻辑

                //重试机制 int max=3; while(max>3){
                // } if(max-- == 0) continue; 不能总是调同一个URL记录已经使用的URL invokedURLs = new ArrayList<>();
                //list.remove(invokedURLs);

                try {
                    result = httpClient.send(url.getHostname(), url.getPort(), invocation);
                } catch (Exception e) {
                    //error-callback = com.zhouyu.helloServiceErrorCallback
                    //容错机制 重试机制
                    return "报错了";
                }

                return result;

            }
        });

        return (T) proxyInstance;
    }
}
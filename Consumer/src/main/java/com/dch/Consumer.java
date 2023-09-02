package com.dch;

import com.dch.proxy.ProxyFactory;


public class Consumer {

    public static void main(String[] args) {

        HelloService helloService = ProxyFactory.getProxy(HelloService.class);
        String result = helloService.sayHello("dch 123");
        System.out.println(result);

    }
}
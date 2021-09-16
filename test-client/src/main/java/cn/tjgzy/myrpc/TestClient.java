package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.client.RpcClientProxy;

import java.lang.reflect.Proxy;

/**
 * @author GongZheyi
 * @create 2021-09-16-12:12
 */
public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1",8889);
        // 根据HelloService获取一个代理对象，执行时会调用invoke方法发送RpcRequest
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        TestService testService = rpcClientProxy.getProxy(TestService.class);


        HelloObject helloObject = new HelloObject(12, "This is a message");
        String s = helloService.hello(helloObject);
        int number = testService.getNumber(12);
        System.out.println(s);
        System.out.println(number);
    }
}

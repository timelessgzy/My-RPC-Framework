package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.client.RpcClientProxy;

/**
 * @author GongZheyi
 * @create 2021-09-16-12:12
 */
public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1",8889);
        // 根据HelloService获取一个代理对象，执行时会调用invoke方法发送RpcRequest
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String s = helloService.hello(object);
        System.out.println(s);
    }
}

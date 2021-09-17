package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.transport.RpcClientProxy;
import cn.tjgzy.myrpc.transport.socket.client.SocketClient;

/**
 * @author GongZheyi
 * @create 2021-09-16-12:12
 */
public class SocketTestClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient("127.0.0.1", 9000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
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

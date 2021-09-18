package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.loadbalancer.RoundRobinLoadBalancer;
import cn.tjgzy.myrpc.transport.netty.client.NettyClient;
import cn.tjgzy.myrpc.transport.RpcClientProxy;

/**
 * @author GongZheyi
 * @create 2021-09-17-11:58
 */
public class NettyTestClient {
    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient(new RoundRobinLoadBalancer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        TestService testService = rpcClientProxy.getProxy(TestService.class);
        HelloObject object = new HelloObject(11, "This is a netty message");
        String res = helloService.hello(object);
        int number = testService.getNumber(12);
        System.out.println(res);
        System.out.println(number);
    }
}

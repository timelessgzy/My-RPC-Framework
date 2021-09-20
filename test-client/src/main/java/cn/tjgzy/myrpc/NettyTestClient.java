package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.loadbalance.RoundRobinLoadBalancer;
import cn.tjgzy.myrpc.transport.netty.client.NettyClient;
import cn.tjgzy.myrpc.transport.RpcClientProxy;

/**
 * @author GongZheyi
 * @create 2021-09-17-11:58
 */
public class NettyTestClient {
    public static void main(String[] args) {
        // 新建一个Netty客户端
        NettyClient nettyClient = new NettyClient(new RoundRobinLoadBalancer());
        // 新建一个代理类处理器，代理实现类的逻辑在这里
        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient);

        // 获取代理实现类
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        TestService testService = rpcClientProxy.getProxy(TestService.class);
        HelloObject object = new HelloObject(11, "This is a netty message");
        String res = helloService.hello(object);
        System.out.println(res);

        int number = testService.getNumber(12);

        System.out.println(number);
    }
}

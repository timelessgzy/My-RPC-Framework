package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.config.RpcServiceConfig;
import cn.tjgzy.myrpc.loadbalance.impl.ConsistentHashLoadBalancer;
import cn.tjgzy.myrpc.loadbalance.impl.RoundRobinLoadBalancer;
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
//        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient,
//                new RpcServiceConfig("Group1", HelloService.class));
//
//        // 获取代理实现类
//        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
//        HelloObject object = new HelloObject(11, "This is a netty message");
//        String res = helloService.hello(object);
//        System.out.println(res);




        // 不同组
        // 新建一个代理类处理器，代理实现类的逻辑在这里
        RpcClientProxy rpcClientProxy2 = new RpcClientProxy(nettyClient,
                new RpcServiceConfig());
        TestService testService = rpcClientProxy2.getProxy(TestService.class);

        int number = testService.getNumber(12);



//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            System.out.println("sleep了.......");
//            e.printStackTrace();
//        }
//        int number2 = testService.getNumber(12);
//        int number3 = testService.getNumber(12);
//
        System.out.println(number);
//        System.out.println(number2);
//        System.out.println(number3);
    }
}

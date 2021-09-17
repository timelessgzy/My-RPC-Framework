package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.registry.DefaultServiceRegistry;
import cn.tjgzy.myrpc.registry.ServiceRegistry;
import cn.tjgzy.myrpc.service.HelloServiceImpl;
import cn.tjgzy.myrpc.service.TestServiceImpl;
import cn.tjgzy.myrpc.transport.netty.server.NettyServer;

/**
 * @author GongZheyi
 * @create 2021-09-17-11:56
 */
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        TestService testService = new TestServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        serviceRegistry.register(testService);
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(9999);
    }
}

package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.provider.DefaultServiceProvider;
import cn.tjgzy.myrpc.provider.ServiceProvider;
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
        NettyServer nettyServer = new NettyServer("127.0.0.1",9999);
        nettyServer.publishService(helloService,HelloService.class);
        nettyServer.publishService(testService,TestService.class);
        nettyServer.start();
    }
}

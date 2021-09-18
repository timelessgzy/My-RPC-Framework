package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.provider.DefaultServiceProvider;
import cn.tjgzy.myrpc.provider.ServiceProvider;
import cn.tjgzy.myrpc.transport.socket.server.SocketServer;
import cn.tjgzy.myrpc.service.HelloServiceImpl;
import cn.tjgzy.myrpc.service.TestServiceImpl;

/**
 * @author GongZheyi
 * @create 2021-09-16-12:10
 */
public class TestServer {
    public static void main(String[] args) {
        TestService testService =  new TestServiceImpl();
        HelloService helloService = new HelloServiceImpl();

        ServiceProvider serviceProvider = new DefaultServiceProvider();
        serviceProvider.addServiceProvider(helloService);
        serviceProvider.addServiceProvider(testService);

        SocketServer socketServer = new SocketServer(serviceProvider);
//        socketServer.start(8889);
    }
}

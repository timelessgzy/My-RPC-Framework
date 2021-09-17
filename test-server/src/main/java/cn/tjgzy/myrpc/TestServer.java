package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.registry.DefaultServiceRegistry;
import cn.tjgzy.myrpc.registry.ServiceRegistry;
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

        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        serviceRegistry.register(testService);

        SocketServer socketServer = new SocketServer(serviceRegistry);
        socketServer.start(8889);
    }
}

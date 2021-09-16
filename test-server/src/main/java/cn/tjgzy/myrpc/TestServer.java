package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.server.RpcServer;
import cn.tjgzy.myrpc.service.HelloServiceImpl;

/**
 * @author GongZheyi
 * @create 2021-09-16-12:10
 */
public class TestServer {
    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer();
        HelloService helloService = new HelloServiceImpl();
        rpcServer.register(helloService,8889);
    }
}

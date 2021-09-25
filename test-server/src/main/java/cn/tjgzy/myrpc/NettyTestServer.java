package cn.tjgzy.myrpc;

import cn.tjgzy.myrpc.annotation.ServiceScan;
import cn.tjgzy.myrpc.service.HelloServiceImpl;
import cn.tjgzy.myrpc.service.TestServiceImpl;
import cn.tjgzy.myrpc.transport.netty.server.NettyServer;

/**
 * @author GongZheyi
 * @create 2021-09-17-11:56
 */
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer("127.0.0.1",8888);
        nettyServer.start();
    }
}

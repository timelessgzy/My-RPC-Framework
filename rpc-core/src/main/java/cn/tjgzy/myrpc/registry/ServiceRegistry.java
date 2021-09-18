package cn.tjgzy.myrpc.registry;

import java.net.InetSocketAddress;

/** 服务注册接口
@author GongZheyi
@create 2021-09-18-8:34
*/
public interface ServiceRegistry {

    void register(String serviceName, InetSocketAddress inetSocketAddress);

}

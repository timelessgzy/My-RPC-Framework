package cn.tjgzy.myrpc.registry;

import cn.tjgzy.myrpc.entity.RpcRequest;

import java.net.InetSocketAddress;

/** 服务发现接口
 * @author GongZheyi
 * @create 2021-09-18-10:43
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名称查找服务实体
     *
     * @param rpcRequest
     * @return 服务实体
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);

}

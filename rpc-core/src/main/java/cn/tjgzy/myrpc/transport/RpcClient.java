package cn.tjgzy.myrpc.transport;

import cn.tjgzy.myrpc.entity.RpcRequest;

/**
 * @author GongZheyi
 * @create 2021-09-17-9:27
 */
public interface RpcClient {

    Object sendRequest(RpcRequest rpcRequest);

}

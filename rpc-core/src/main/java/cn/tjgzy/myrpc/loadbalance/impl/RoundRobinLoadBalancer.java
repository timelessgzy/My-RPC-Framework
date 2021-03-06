package cn.tjgzy.myrpc.loadbalance.impl;

import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.exception.RpcException;
import cn.tjgzy.myrpc.loadbalance.AbstractLoadBalance;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author GongZheyi
 * @create 2021-09-18-13:18
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalance {

    private int index = 0;

    @Override
    protected Instance doSelect(List<Instance> instances, RpcRequest rpcRequest) {
        if (instances == null || instances.size() == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }
}

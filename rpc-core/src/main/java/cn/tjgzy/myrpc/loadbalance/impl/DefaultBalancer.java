package cn.tjgzy.myrpc.loadbalance.impl;

import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.loadbalance.AbstractLoadBalance;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author GongZheyi
 * @create 2021-09-27-22:18
 */
public class DefaultBalancer extends AbstractLoadBalance {
    @Override
    protected Instance doSelect(List<Instance> instances, RpcRequest rpcRequest) {
        return null;
    }
}

package cn.tjgzy.myrpc.loadbalance;

import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.exception.RpcException;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @author GongZheyi
 * @create 2021-09-18-13:16
 */
public class RandomLoadBalancer implements LoadBalancer {
    
    @Override
    public Instance select(List<Instance> instances, RpcRequest rpcRequest) {
        if (instances == null || instances.size() == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        int length = instances.size();
        int randomIndex = new Random().nextInt(length);
        return instances.get(randomIndex);
    }
}

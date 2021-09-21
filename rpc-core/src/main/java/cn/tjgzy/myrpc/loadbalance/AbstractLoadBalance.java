package cn.tjgzy.myrpc.loadbalance;

import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.exception.RpcException;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author GongZheyi
 * @create 2021-09-21-14:41
 */
public abstract class AbstractLoadBalance implements LoadBalancer {
    @Override
    public Instance select(List<Instance> instances, RpcRequest rpcRequest) {
        if (instances == null || instances.size() == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        if (instances.size() == 1) {
            return instances.get(0);
        }
        return doSelect(instances, rpcRequest);
    }

    protected abstract Instance doSelect(List<Instance> instances, RpcRequest rpcRequest);

}

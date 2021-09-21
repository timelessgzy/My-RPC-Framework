package cn.tjgzy.myrpc.registry;

import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.exception.RpcException;
import cn.tjgzy.myrpc.loadbalance.LoadBalancer;
import cn.tjgzy.myrpc.loadbalance.RandomLoadBalancer;
import cn.tjgzy.myrpc.utils.NacosUtils;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author GongZheyi
 * @create 2021-09-18-10:44
 */
public class NacosServiceDiscovery implements ServiceDiscovery {

    private final LoadBalancer loadBalancer;

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    // 默认采用随机
    public NacosServiceDiscovery() {
        this.loadBalancer = new RandomLoadBalancer();
    }

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String serviceName = rpcRequest.getInterfaceName();
        try {
            List<Instance> instances = NacosUtils.getAllInstance(serviceName);
            // 负载均衡策略
            Instance instance = loadBalancer.select(instances, rpcRequest);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
    }
}

package cn.tjgzy.myrpc.registry;

import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.exception.RpcException;
import cn.tjgzy.myrpc.utils.NacosUtils;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
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

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private final NamingService namingService;

    public NacosServiceDiscovery() {
        this.namingService = NacosUtils.getNacosNamingService();
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtils.getAllInstance(namingService, serviceName);
            // TODO：负载均衡策略
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
    }
}

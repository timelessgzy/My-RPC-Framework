package cn.tjgzy.myrpc.registry;


import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.exception.RpcException;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author GongZheyi
 * @create 2021-09-18-8:35
 */
public class NacosServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private static final String NACOS_ADDR = "127.0.0.1:8848";

    private static final NamingService namingService;

    static {
        try {
            namingService = NamingFactory.createNamingService(NACOS_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        String host = inetSocketAddress.getHostName();
//        String hostAddress = inetSocketAddress.getAddress().getHostAddress();
        int port = inetSocketAddress.getPort();
        logger.info("注册的host：" + host);
        try {
            namingService.registerInstance(serviceName, host, port);
        } catch (NacosException e) {
            logger.error("注册实例失败");
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            // TODO：负载均衡策略
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
    }
}

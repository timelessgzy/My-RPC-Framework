package cn.tjgzy.myrpc.utils;

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
 * @create 2021-09-18-10:20
 */
public class NacosUtils {

    private static final Logger logger = LoggerFactory.getLogger(NacosUtils.class);

    private static final String NACOS_ADDR = "127.0.0.1:8848";


    /**
     * 注册服务到Nacos
     * @param namingService
     * @param serviceName
     * @param inetSocketAddress
     * @throws NacosException
     */
    public static void registerService(NamingService namingService, String serviceName, InetSocketAddress inetSocketAddress) throws NacosException {
        String hostIp = inetSocketAddress.getAddress().getHostAddress();
        int port = inetSocketAddress.getPort();
        namingService.registerInstance(serviceName, hostIp, port);
    }

    /**
     * 连接到Nacos
     * @return
     */
    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(NACOS_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }


    /**
     * 根据服务名称获取实例
     * @param namingService
     * @param serviceName
     * @return
     * @throws NacosException
     */
    public static List<Instance> getAllInstance(NamingService namingService, String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }


    /**
     * 注销实例
     */
    public static void deregister() {

    }
}

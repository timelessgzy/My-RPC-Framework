package cn.tjgzy.myrpc.utils;

import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.exception.RpcException;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 管理Nacos连接的工具类
 * @author GongZheyi
 * @create 2021-09-18-10:20
 */
public class NacosUtils {

    private static final Logger logger = LoggerFactory.getLogger(NacosUtils.class);
    /**
     * 与 Nacos的连接
     */
    private static final NamingService namingService;
    /**
     * 服务名称集合
     */
    private static final Set<String> serviceNames = new HashSet<>();

    private static InetSocketAddress address;

    private static final String NACOS_ADDR = "127.0.0.1:8848";

    static {
        namingService = getNacosNamingService();
    }

    /**
     * 注册服务到Nacos
     * @param serviceName
     * @param inetSocketAddress
     * @throws NacosException
     */
    public static void registerService(String serviceName, InetSocketAddress inetSocketAddress) throws NacosException {
        String hostIp = inetSocketAddress.getAddress().getHostAddress();
        int port = inetSocketAddress.getPort();
        try {
            namingService.registerInstance(serviceName, hostIp, port);
            NacosUtils.address = inetSocketAddress;
            serviceNames.add(serviceName);
        } catch (Exception e) {
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }

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
     * @param serviceName
     * @return
     * @throws NacosException
     */
    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }


    /**
     * 注销实例
     */
    public static void deregister() {
        if (!serviceNames.isEmpty() && address != null) {
            String hostIp = address.getAddress().getHostAddress();
            int port = address.getPort();
            for(String serviceName: serviceNames) {
                // 注销
                try {
                    namingService.deregisterInstance(serviceName,hostIp,port);
                } catch (NacosException e) {
                    logger.error("注销服务{}失败", serviceName, e);
                }
            }
        }
    }
}

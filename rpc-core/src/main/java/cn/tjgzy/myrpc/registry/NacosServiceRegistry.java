package cn.tjgzy.myrpc.registry;


import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.exception.RpcException;
import cn.tjgzy.myrpc.utils.NacosUtils;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/** Nacos服务注册中心
 * @author GongZheyi
 * @create 2021-09-18-8:35
 */
public class NacosServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);


    private final NamingService namingService;

    public NacosServiceRegistry() {
        this.namingService = NacosUtils.getNacosNamingService();
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtils.registerService(namingService,serviceName,inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注册实例失败");
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}

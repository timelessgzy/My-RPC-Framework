package cn.tjgzy.myrpc.provider;


import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GongZheyi
 * @create 2021-09-16-15:10
 */
public class DefaultServiceProvider implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceProvider.class);

    /**
     * 注册接口名集合
     * key为接口全类名，value为实现类对象
     */
    private static final Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    /**
     * 注册过的实现类集合。key为实现类全类名
     */
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();



    @Override
    public <T> void addServiceProvider(T service) {
        String serviceImplName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceImplName)) {
            return;
        }
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces == null || interfaces.length == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class i: interfaces) {
            serviceMap.put(i.getCanonicalName(),service);
        }
        registeredService.add(serviceImplName);
        logger.info("向接口: {} 注册服务: {}", interfaces, serviceImplName);
    }

    @Override
    public Object getService(String interfaceName) {
        Object service = serviceMap.get(interfaceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}

package cn.tjgzy.myrpc.provider;

/**
 * @author GongZheyi
 * @create 2021-09-16-15:09
 */
public interface ServiceProvider {

    /**
     * 将一个服务注册进注册表
     * @param service 待注册的服务实体
     * @param <T> 服务实体类
     */
    <T> void addServiceProvider(T service, String serviceName);

    /**
     * 根据服务名称获取服务实体
     * @param interfaceName 服务名称
     * @return 服务实体
     */
    Object getService(String interfaceName);

}

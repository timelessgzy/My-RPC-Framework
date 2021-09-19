package cn.tjgzy.myrpc.transport;

/**
 * @author GongZheyi
 * @create 2021-09-17-9:27
 */
public interface RpcServer {

    void start();

    /**
     * 向注册中心注册服务
     * @param service
     * @param serviceName
     * @param <T>
     */
    <T> void publishService(T service, String serviceName);
}

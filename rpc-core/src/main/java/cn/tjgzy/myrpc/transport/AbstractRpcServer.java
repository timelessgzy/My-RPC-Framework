package cn.tjgzy.myrpc.transport;

import cn.tjgzy.myrpc.annotation.RpcService;
import cn.tjgzy.myrpc.annotation.ServiceScan;
import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.exception.RpcException;
import cn.tjgzy.myrpc.provider.ServiceProvider;
import cn.tjgzy.myrpc.registry.ServiceRegistry;
import cn.tjgzy.myrpc.utils.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @author GongZheyi
 * @create 2021-09-19-9:18
 */
public abstract class AbstractRpcServer implements RpcServer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices() {
        // 获取启动类
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        // 获取@ScanService标注的扫描路径
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        // 如果默认为空，则扫描启动类所在包和子包下的所有类
        if("".equals(basePackage)) {
            // 启动类所在包basePackage
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        // 获取basePackage下的所有类
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(RpcService.class)) {
                // 获取注释的serviceName和group
                String serviceName = clazz.getAnnotation(RpcService.class).name();
                String group = clazz.getAnnotation(RpcService.class).group();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                // 如果没有设置服务别名，默认为空，则获取接口名称进行发布
                if("".equals(serviceName)) {
                    System.out.println("没有设置别名");
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces){
                        System.out.println("注册接口，名称为" + oneInterface.getCanonicalName() + group);
                        publishService(obj, oneInterface.getCanonicalName() + group);
                    }
                } else {
                    System.out.println("设置了别名");
                    publishService(obj, serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName,new InetSocketAddress(host, port));
    }
}

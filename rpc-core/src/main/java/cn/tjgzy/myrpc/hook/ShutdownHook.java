package cn.tjgzy.myrpc.hook;

import cn.tjgzy.myrpc.utils.NacosUtils;
import cn.tjgzy.myrpc.utils.ThreadPoolFactoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author GongZheyi
 * @create 2021-09-18-12:41
 */
public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);
//    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("添加钩子函数，关闭后将注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            logger.info("触发钩子，开始进行注销");
            // 向注册中心进行注销
            NacosUtils.deregister();
            // 关闭线程池
            ThreadPoolFactoryUtils.shutdownAllThreadPool();
        }));
    }

}

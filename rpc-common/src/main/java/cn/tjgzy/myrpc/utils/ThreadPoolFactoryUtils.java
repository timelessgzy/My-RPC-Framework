package cn.tjgzy.myrpc.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author GongZheyi
 * @create 2021-09-20-9:51
 */
public class ThreadPoolFactoryUtils {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolFactoryUtils.class);

    /**
     * 通过 threadNamePrefix 来区分不同线程池（把相同 threadNamePrefix 的线程池看作是为同一业务场景服务）。
     * key: threadNamePrefix
     * value: threadPool
     */
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();


//    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix) {
//        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
//        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
//    }
//
//
//
//    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig,
//                                                                 String threadNamePrefix, Boolean daemon) {
//        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix, k -> createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon));
//        // 如果 threadPool 被 shutdown 的话就重新创建一个
//        if (threadPool.isShutdown() || threadPool.isTerminated()) {
//            THREAD_POOLS.remove(threadNamePrefix);
//            threadPool = createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon);
//            THREAD_POOLS.put(threadNamePrefix, threadPool);
//        }
//        return threadPool;
//    }
//
//
//    private static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig,
//                                                    String threadNamePrefix, Boolean daemon) {
//        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
//        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(), customThreadPoolConfig.getMaximumPoolSize(),
//                customThreadPoolConfig.getKeepAliveTime(), customThreadPoolConfig.getUnit(), customThreadPoolConfig.getWorkQueue(),
//                threadFactory);
//    }



    /**
     * 创建 ThreadFactory 。如果threadNamePrefix不为空则使用自建ThreadFactory，否则使用defaultThreadFactory
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @return ThreadFactory
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix) {
        if (threadNamePrefix != null) {
            ThreadFactory threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat(threadNamePrefix + "-%d")
                    .setDaemon(false).build();
            return threadFactory;
        }
        return Executors.defaultThreadFactory();
    }


    /**
     * 关闭所有线程池
     */
    public static void shutdownAllThreadPool() {
//        logger.info("开始关闭线程池");
        THREAD_POOLS.entrySet().stream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            logger.info("shut down thread pool [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                e.printStackTrace();
            }
        });
    }



    /**
     * 打印线程池的状态
     *
     * @param threadPool 线程池对象
     */
    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool) {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status"));
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            logger.info("============ThreadPool Status=============");
            logger.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
            logger.info("Active Threads: [{}]", threadPool.getActiveCount());
            logger.info("Number of Tasks : [{}]", threadPool.getCompletedTaskCount());
            logger.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
            logger.info("===========================================");
        }, 0, 1, TimeUnit.SECONDS);
    }
}

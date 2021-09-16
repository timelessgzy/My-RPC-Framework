package cn.tjgzy.myrpc.server;

import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import cn.tjgzy.myrpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author GongZheyi
 * @create 2021-09-16-12:05
 */
public class RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private final ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry;
    private RequestHandler requestHandler = new RequestHandler();


    public RpcServer(ServiceRegistry serviceRegistry) {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY), threadFactory);
        this.serviceRegistry = serviceRegistry;
    }


//    public void register(Object service, int port) {
//        try (ServerSocket serverSocket = new ServerSocket(port)) {
//            logger.info("服务器正在启动...");
//            Socket socket;
//            while((socket = serverSocket.accept()) != null) {
//                logger.info("客户端已连接！Ip为：" + socket.getInetAddress() + "端口为：" + socket.getPort());
//                threadPool.execute(new WorkerThread(socket, service));
//            }
//        } catch (IOException e) {
//            logger.error("连接时有错误发生：", e);
//        }
//    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动...");
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                logger.info("客户端已连接！Ip为：" + socket.getInetAddress() + "端口为：" + socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket,requestHandler, serviceRegistry));
            }
        } catch (IOException e) {
            logger.error("连接时有错误发生：", e);
        }
    }

}

package cn.tjgzy.myrpc.transport.socket.server;

import cn.tjgzy.myrpc.provider.ServiceProvider;
import cn.tjgzy.myrpc.transport.RequestHandler;
import cn.tjgzy.myrpc.transport.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author GongZheyi
 * @create 2021-09-16-12:05
 */
public class SocketServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;
    private RequestHandler requestHandler = new RequestHandler();

    private static String host;
    private static int port;



    public SocketServer(ServiceProvider serviceProvider) {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY), threadFactory);
        this.serviceProvider = serviceProvider;
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

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动...");
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                logger.info("客户端已连接！Ip为：" + socket.getInetAddress() + "端口为：" + socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket,requestHandler, serviceProvider));
            }
        } catch (IOException e) {
            logger.error("连接时有错误发生：", e);
        }
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceName) {

    }

}

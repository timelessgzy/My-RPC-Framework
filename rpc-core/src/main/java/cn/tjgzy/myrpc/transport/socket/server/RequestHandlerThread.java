package cn.tjgzy.myrpc.transport.socket.server;

import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import cn.tjgzy.myrpc.provider.ServiceProvider;
import cn.tjgzy.myrpc.transport.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

/**
 * @author GongZheyi
 * @create 2021-09-16-15:52
 */
public class RequestHandlerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceProvider serviceProvider;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceProvider serviceProvider) {
        this.requestHandler = requestHandler;
        this.serviceProvider = serviceProvider;
        this.socket = socket;
    }


    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            // 读取客户端传来的rpcRequest
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            // 根据RpcRequest的接口名称获取对象
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceProvider.getService(interfaceName);
            // 交给requestHandler处理，返回值为result
            Object result = requestHandler.handle(rpcRequest, service);
            // 构造rpcResponse并返回
            // TODO:requestId
            objectOutputStream.writeObject(RpcResponse.success(result,""));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}

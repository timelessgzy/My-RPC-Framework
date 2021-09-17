package cn.tjgzy.myrpc.transport;

import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * @author GongZheyi
 * @create 2021-09-16-11:58
 */
@Data
@AllArgsConstructor
public class RpcClientProxy implements InvocationHandler {

    private String host;
    private int port;
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        // 构建RpcRequest
//        RpcRequest rpcRequest = RpcRequest.builder()
//                .interfaceName(method.getDeclaringClass().getName())
//                .methodName(method.getName())
//                .parameters(args)
//                .paramTypes(method.getParameterTypes())
//                .build();
//        System.out.println(rpcRequest);
//        RpcResponse rpcResponse = sendRequest(rpcRequest, host, port);
        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes());
        return client.sendRequest(rpcRequest);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * BIO的发送方法，NIO发送方法在RpcClient实现类
     * 建立Socket连接，发送rpcRequest
     * @param rpcRequest
     * @param host
     * @param port
     * @return 返回服务端响应RpcResponse
     */
    public RpcResponse sendRequest(RpcRequest rpcRequest, String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return (RpcResponse)objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

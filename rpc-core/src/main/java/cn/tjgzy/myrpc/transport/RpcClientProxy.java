package cn.tjgzy.myrpc.transport;

import cn.tjgzy.myrpc.constant.ResponseCode;
import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import cn.tjgzy.myrpc.exception.RpcException;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    private final RpcClient client; // 客户端

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

          // v4.1之前的版本，sendRequest是阻塞方法
//        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(),
//                method.getName(), args, method.getParameterTypes());
//        return client.sendRequest(rpcRequest);
        /**
         * 通过CompletableFuture获取结果
         */
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(),
                                                method.getDeclaringClass().getName(),
                                                method.getName(), args, method.getParameterTypes());
        RpcResponse rpcResponse = null;
        CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>)client.sendRequest(rpcRequest);
        rpcResponse = completableFuture.get();
        // 进行校验
        check(rpcResponse,rpcRequest);
        return rpcResponse.getData();
    }

    public void check(RpcResponse rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            logger.error("调用服务失败，未获取到RpcResponse");
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE);
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            logger.error("rpcRequest与rpcResponse的请求ID不同");
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH);
        }
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "INTERFACE_NAME:"  + rpcRequest.getInterfaceName());
        }
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

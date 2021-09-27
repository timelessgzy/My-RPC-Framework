package cn.tjgzy.myrpc.transport;

import cn.tjgzy.myrpc.constant.ResponseCode;
import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import cn.tjgzy.myrpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author GongZheyi
 * @create 2021-09-16-15:52
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public Object handle(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        Object result = null;
        result = invokeTargetMethod(rpcRequest, service);
        logger.info("服务:{} 成功调用方法:{}", rpcRequest.getRpcServiceName(), rpcRequest.getMethodName());
//        try {
//            result = invokeTargetMethod(rpcRequest, service);
//            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getRpcServiceName(), rpcRequest.getMethodName());
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            logger.error("调用或发送时有错误发生：", e);
//            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE);
//        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws IllegalAccessException, InvocationTargetException {
        Method method;
        try {
            System.out.println(service);
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        System.out.println("开始调用");
        Object result = method.invoke(service, rpcRequest.getParameters());
        System.out.println("调用结果为：" + result);
        return result;
    }

}

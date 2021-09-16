package cn.tjgzy.myrpc.exception;

import cn.tjgzy.myrpc.constant.RpcError;

/**
 * @author GongZheyi
 * @create 2021-09-16-15:31
 */
public class RpcException extends RuntimeException {
    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcError error) {
        super(error.getMessage());
    }
}

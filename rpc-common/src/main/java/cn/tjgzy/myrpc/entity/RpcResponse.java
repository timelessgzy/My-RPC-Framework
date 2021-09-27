package cn.tjgzy.myrpc.entity;

import cn.tjgzy.myrpc.constant.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author GongZheyi
 * @create 2021-09-16-11:54
 */
@Data
public class RpcResponse<T> implements Serializable {

    /**
     * 响应对应的请求号
     */
    private String requestId;

    /**
     * 响应状态码
     */
    private Integer statusCode;

    /**
     * 响应状态补充信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code, String requestId) {
        return fail(code,requestId, code.getMessage());
    }

    public static <T> RpcResponse<T> fail(ResponseCode code, String requestId, String message) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());
        response.setMessage(message);
        return response;
    }

}

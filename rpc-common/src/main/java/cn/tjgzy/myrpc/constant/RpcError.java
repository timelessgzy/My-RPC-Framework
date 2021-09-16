package cn.tjgzy.myrpc.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author GongZheyi
 * @create 2021-09-16-15:32
 */
@AllArgsConstructor
@Getter
public enum RpcError {

    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口");

    private final String message;

}

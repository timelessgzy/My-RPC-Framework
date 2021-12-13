package cn.tjgzy.myrpc.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

/**
 * @author GongZheyi
 * @create 2021-09-16-11:54
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)
public class RpcRequest implements Serializable {
    /**
     * 请求号
     */
    private String requestId;

    /**
     * 待调用接口名称
     */
    private String interfaceName;

    /**
     * 待调用方法名称
     */
    private String methodName;

    /**
     * 调用方法的参数
     */
    private Object[] parameters;

    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;

    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;

    /**
     * 服务所属的group，用于区分同一接口的多个实现类
     */
    private String group;

    private String rpcServiceName;

    /**
     * 获取服务名称：接口名 + 组名
     * @return
     */
    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup();
    }

}

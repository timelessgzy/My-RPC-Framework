package cn.tjgzy.myrpc.config;

import lombok.*;

/**
 * @author GongZheyi
 * @create 2021-09-23-10:42
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {
    /**
     * when the interface has multiple implementation classes, distinguish by group
     */
    private String group = "";

    /**
     * target service
     */
    private Object service;

    public String getRpcServiceName() {
        System.out.println("RpcConfig中的servicename为" + this.getInterfaceName() + this.getGroup());
        return this.getInterfaceName() + this.getGroup();
    }

    public String getInterfaceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}

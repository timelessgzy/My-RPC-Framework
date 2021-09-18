package cn.tjgzy.myrpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author GongZheyi
 * @create 2021-09-18-13:15
 */
public interface LoadBalancer {
    Instance select(List<Instance> instances);
}

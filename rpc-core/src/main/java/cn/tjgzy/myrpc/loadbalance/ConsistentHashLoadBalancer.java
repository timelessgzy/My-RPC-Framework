package cn.tjgzy.myrpc.loadbalance;

import cn.tjgzy.myrpc.entity.RpcRequest;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.MD5Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author GongZheyi
 * @create 2021-09-21-8:39
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    // key为需要调用的服务名称，value为选择器
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    public Instance select(List<Instance> instances, RpcRequest rpcRequest) {
        // map保存Instance的地址与Instance实例
        Map<String,Instance> map = new HashMap();

        instances.stream().forEach(instance -> {
            String address = instance.getIp() + ":" + instance.getPort();
            map.put(address,instance);
        });
        // 地址集合
        List<String> serviceAddresses = new ArrayList<>(map.keySet());

        System.out.println(Arrays.toString(serviceAddresses.toArray()));

        int identityHashCode = System.identityHashCode(serviceAddresses);
        String rpcServiceName = rpcRequest.getInterfaceName();
        ConsistentHashSelector selector = selectors.get(rpcServiceName);
        // check for updates
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(rpcServiceName, new ConsistentHashSelector(serviceAddresses, 160, identityHashCode));
            selector = selectors.get(rpcServiceName);
        }
        String selectKey = selector.select(rpcServiceName + Arrays.stream(rpcRequest.getParameters()));
        System.out.println(selectKey);
        return map.get(selectKey);
    }

    /**
     * 静态内部类ConsistentHashSelector选择器
     * 根据需要调用的服务名进行选择
     */
    static class ConsistentHashSelector {
        /**
         * 虚拟节点集合，key为hash值，value为服务地址
         */
        private final TreeMap<Long, String> virtualInvokers;

        private final int identityHashCode;

        /**
         * replicaNumber：每个服务器生成replicaNumber个虚拟节点
         */
        private final int replicaNumber;

        ConsistentHashSelector(List<String> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;
            this.replicaNumber = replicaNumber;
            // 对于每一个ip地址
            for (String invoker : invokers) {
                // 每4个节点一组，生成一个16字节128位的md5摘要
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(invoker + i);
                    System.out.println("digest" + Arrays.toString(digest));
                    // 针对每个一个重复节点将其等间隔的分布在环形hash空间上
                    for (int h = 0; h < 4; h++) {
                        // 计算节点hash值
                        // 将128位分为4部分，0-31,32-63,64-95,95-128，并生成4个32位数，存于long中，long的高32位都为0
                        // 并作为虚拟结点的key，即计算位置
                        long m = hash(digest, h);
                        System.out.println("hash,m:" + m);
                        // 将节点放到环形hash空间上
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
            // 节点已经布置完成
            virtualInvokers.entrySet().forEach(entry -> {
                System.out.println("key:" + entry.getKey() + "     value:" + entry.getValue());
            });
            System.out.println("一共有" + virtualInvokers.size());
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            return md.digest();
        }

        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24
                    | (long) (digest[2 + idx * 4] & 255) << 16
                    | (long) (digest[1 + idx * 4] & 255) << 8
                    | (long) (digest[idx * 4] & 255))
                    & 4294967295L;
        }

        public String select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            return selectForKey(hash(digest, 0));
        }

        public String selectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }
    }
    
}

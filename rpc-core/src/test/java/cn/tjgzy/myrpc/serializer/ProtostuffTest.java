package cn.tjgzy.myrpc.serializer;

import cn.tjgzy.myrpc.entity.RpcRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @author GongZheyi
 * @create 2021-12-13-14:04
 */
public class ProtostuffTest {
    @Test
    public void test1() {
        HelloObject helloObject = new HelloObject(1, "1111");
        ProtostuffSerializer serializer = new ProtostuffSerializer();
        byte[] bytes = serializer.serialize(helloObject);
        System.out.println(bytes);
        HelloObject o = (HelloObject) serializer.deserialize(bytes, HelloObject.class);
        System.out.println(o.getMessage());
    }
}

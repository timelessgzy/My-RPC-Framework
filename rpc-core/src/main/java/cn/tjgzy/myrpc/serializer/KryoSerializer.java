package cn.tjgzy.myrpc.serializer;

import cn.tjgzy.myrpc.constant.SerializerCode;
import cn.tjgzy.myrpc.transport.RpcClient;
import cn.tjgzy.myrpc.transport.RpcServer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author GongZheyi
 * @create 2021-09-17-16:13
 */
public class KryoSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
//        kryo.register(RpcServer.class);
//        kryo.register(RpcClient.class);
        kryo.setReferences(true);
        // 关闭注册行为，防止分布式环境下通过序号序列化失败
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // kryo的输出流
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObject(output,obj);
        byte[] bytes = output.toBytes();
        KRYO_THREAD_LOCAL.remove();
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        Object o = kryo.readObject(input, clazz);
        KRYO_THREAD_LOCAL.remove();
        return o;
    }

    @Override
    public int getCode() {
        return SerializerCode.KRYO.getCode();
    }
}

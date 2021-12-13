package cn.tjgzy.myrpc.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GongZheyi
 * @create 2021-12-13-13:51
 */
public class ProtostuffSerializer implements CommonSerializer {

    /**
     * 避免每次序列化都要重新申请Buffer空间
     */
    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    /**
     * schema缓存，每个class对应自己的schema
     */
    private static Map<Class<?>, Schema<?>> schemaMap = new ConcurrentHashMap<>();

    @Override
    public byte[] serialize(Object obj) {
        Class<?> clazz = obj.getClass();
        Schema schema = getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Schema schema = getSchema(clazz);
        Object obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    @Override
    public int getCode() {
        return 2;
    }

    /**
     * 传入clazz对象从缓存中获取对应的schema
     * @param clazz
     * @param <?>
     * @return
     */
    public static  Schema<?> getSchema(Class<?> clazz) {
        if (schemaMap.containsKey(clazz)) {
            return schemaMap.get(clazz);
        }
        Schema<?> schema = RuntimeSchema.getSchema(clazz);
        schemaMap.put(clazz, schema);
        return schema;
    }
}

package cn.tjgzy.myrpc.serializer;

/**
 * @author GongZheyi
 * @create 2021-09-17-10:12
 */
public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new ProtostuffSerializer();
//            case 3:
//                return new ProtobufSerializer();
            default:
                return null;
        }
    }

    /**
     * 传入对象进行序列化
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 将字节反序列化为指定的类对象
     * @param bytes
     * @param clazz
     * @return
     */
    Object deserialize(byte[] bytes, Class<?> clazz);

    /**
     * 返回序列化方式
     * @return
     */
    int getCode();
}

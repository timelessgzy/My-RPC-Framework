package cn.tjgzy.myrpc.serializer;

import cn.tjgzy.myrpc.constant.SerializerCode;
import cn.tjgzy.myrpc.entity.RpcRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author GongZheyi
 * @create 2021-09-17-10:35
 */
public class JsonSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(obj);
            return bytes;
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 这里由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类型
     * 需要重新判断处理
     * @param obj
     * @return
     * @throws IOException
     */
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        if (rpcRequest.getHeartBeat()) {
            // 如果是心跳包，则属性全部为空，不需要再进行序列化
            return obj;
        }
        for(int i = 0; i < rpcRequest.getParamTypes().length; i ++) {
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            if(!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
                // 把object数组的元素序列化为bytes
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                // 根据该元素的实际类型进行辅助反序列化
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return rpcRequest;
    }



    @Override
    public int getCode() {
        return SerializerCode.JSON.getCode();
    }
}

package cn.tjgzy.myrpc.codec;

import cn.tjgzy.myrpc.compress.Compress;
import cn.tjgzy.myrpc.compress.DefaultCompress;
import cn.tjgzy.myrpc.compress.GzipCompress;
import cn.tjgzy.myrpc.compress.factory.CompressFactory;
import cn.tjgzy.myrpc.constant.PackageType;
import cn.tjgzy.myrpc.constant.RpcError;
import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import cn.tjgzy.myrpc.exception.RpcException;
import cn.tjgzy.myrpc.factory.SingletonFactory;
import cn.tjgzy.myrpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 +---------------+---------------+-----------------+-------------+
 |  Magic Number |  Package Type | Serializer Type | Data Length |
 |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 +---------------+---------------+-----------------+-------------+
 |                          Data Bytes                           |
 |                   Length: ${Data Length}                      |
 +---------------------------------------------------------------+
 * @author GongZheyi
 * @create 2021-09-17-10:20
 */
public class CommonDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private final Compress compress = CompressFactory.getCompressInstance(1);


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int magicNumber = in.readInt();
        if (magicNumber != MAGIC_NUMBER) {
            logger.error("传来的协议包不支持");
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        int packageType = in.readInt();
        Class<?> packageClass;
        if (packageType == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageType == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.info("不识别的协议包，不是request也不是response");
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }

        int dataLength = in.readInt();
        byte[] bytes = new byte[dataLength];
        in.readBytes(bytes);
        bytes = compress.decompress(bytes);
        // 反序列化
        Object o = serializer.deserialize(bytes, packageClass);
        out.add(o);
    }
}

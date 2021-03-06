package cn.tjgzy.myrpc.codec;

import cn.tjgzy.myrpc.compress.Compress;
import cn.tjgzy.myrpc.compress.DefaultCompress;
import cn.tjgzy.myrpc.compress.GzipCompress;
import cn.tjgzy.myrpc.compress.factory.CompressFactory;
import cn.tjgzy.myrpc.constant.PackageType;
import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.factory.SingletonFactory;
import cn.tjgzy.myrpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 +---------------+---------------+-----------------+-------------+
 |  Magic Number |  Package Type | Serializer Type | Data Length |
 |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 +---------------+---------------+-----------------+-------------+
 |                          Data Bytes                           |
 |                   Length: ${Data Length}                      |
 +---------------------------------------------------------------+
 * @author GongZheyi
 * @create 2021-09-17-10:10
 */
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private final CommonSerializer serializer;
    private final Compress compress = CompressFactory.getCompressInstance(1);

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        if (msg instanceof RpcRequest) {
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        out.writeInt(serializer.getCode());
        // εΊεε
        byte[] bytes = serializer.serialize(msg);
        bytes = compress.compress(bytes);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}

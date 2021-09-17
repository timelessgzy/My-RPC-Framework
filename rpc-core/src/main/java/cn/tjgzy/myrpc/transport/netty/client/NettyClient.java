package cn.tjgzy.myrpc.transport.netty.client;

import cn.tjgzy.myrpc.codec.CommonDecoder;
import cn.tjgzy.myrpc.codec.CommonEncoder;
import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import cn.tjgzy.myrpc.serializer.JsonSerializer;
import cn.tjgzy.myrpc.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author GongZheyi
 * @create 2021-09-17-9:47
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private String host;
    private int port;

    public static final Bootstrap BOOTSTRAP;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        BOOTSTRAP = new Bootstrap();
        BOOTSTRAP.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new CommonDecoder());
                        ch.pipeline().addLast(new CommonEncoder(new JsonSerializer()));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try {
            ChannelFuture future = BOOTSTRAP.connect(host, port).sync();
            logger.info("客户端连接到服务器 {}:{}", host, port);
            Channel channel = future.channel();
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生: ", e);
        }
        return null;
    }
}

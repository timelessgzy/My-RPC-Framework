package cn.tjgzy.myrpc.transport.netty.client;

import cn.tjgzy.myrpc.codec.CommonDecoder;
import cn.tjgzy.myrpc.codec.CommonEncoder;
import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import cn.tjgzy.myrpc.factory.SingletonFactory;
import cn.tjgzy.myrpc.loadbalance.LoadBalancer;
import cn.tjgzy.myrpc.loadbalance.RandomLoadBalancer;
import cn.tjgzy.myrpc.registry.NacosServiceDiscovery;
import cn.tjgzy.myrpc.registry.ServiceDiscovery;
import cn.tjgzy.myrpc.serializer.KryoSerializer;
import cn.tjgzy.myrpc.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @author GongZheyi
 * @create 2021-09-17-9:47
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final ServiceDiscovery serviceDiscovery;

    public static final Bootstrap BOOTSTRAP;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this(new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
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
                        ch.pipeline().addLast(new CommonEncoder(new KryoSerializer()));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }


    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {

        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();

        try {
            // 通过注册中心找到服务，返回服务地址
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
            String hostIp = inetSocketAddress.getAddress().getHostAddress();
            int port = inetSocketAddress.getPort();

            ChannelFuture future = BOOTSTRAP.connect(hostIp, port).sync();

            Channel channel = future.channel();

            logger.info("客户端连接到服务器 {}:{},channel是{}", hostIp, port,channel);

            if (channel.isActive()) {
                // 将rpcRequest的ID和结果凭证resultFuture保存起来
                unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
                channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        future1.channel().close();
                        resultFuture.completeExceptionally(future1.cause());
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
//                channel.closeFuture().sync();
//                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
//                RpcResponse rpcResponse = channel.attr(key).get();
//                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error("发送消息时有错误发生: ",e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}

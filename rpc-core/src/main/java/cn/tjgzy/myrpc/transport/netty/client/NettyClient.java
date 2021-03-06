package cn.tjgzy.myrpc.transport.netty.client;

import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import cn.tjgzy.myrpc.factory.SingletonFactory;
import cn.tjgzy.myrpc.loadbalance.LoadBalancer;
import cn.tjgzy.myrpc.loadbalance.impl.RandomLoadBalancer;
import cn.tjgzy.myrpc.registry.NacosServiceDiscovery;
import cn.tjgzy.myrpc.registry.ServiceDiscovery;
import cn.tjgzy.myrpc.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
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
    private static final EventLoopGroup group;
    public static final Bootstrap bootstrap;
    private final ServiceDiscovery serviceDiscovery;



    private final UnprocessedRequests unprocessedRequests;


    public NettyClient() {
        this(new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
//        NioEventLoopGroup group = new NioEventLoopGroup();
//        BOOTSTRAP = new Bootstrap();
//        BOOTSTRAP.group(group)
//                .channel(NioSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE, true)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new CommonDecoder());
//                        ch.pipeline().addLast(new CommonEncoder(new KryoSerializer()));
//                        ch.pipeline().addLast(new NettyClientHandler());
//                    }
//                });
    }


    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {

        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();

        try {
            // ???????????????????????????????????????????????????
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);

            String hostIp = inetSocketAddress.getAddress().getHostAddress();
            int port = inetSocketAddress.getPort();

//            ChannelFuture future = BOOTSTRAP.connect(hostIp, port).sync();
//            Channel channel = future.channel();

            Channel channel = ChannelProvider.get(inetSocketAddress);

//            logger.info("??????????????????????????? {}:{},channel???{}", hostIp, port,channel);

            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }

            // ???rpcRequest???ID???????????????resultFuture????????????
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);

            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("?????????????????????: %s", rpcRequest.toString()));
                    logger.info("?????????channel???" + channel);
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("??????????????????????????????: ", future1.cause());
                }
            });
//                channel.closeFuture().sync();
//                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
//                RpcResponse rpcResponse = channel.attr(key).get();
//                return rpcResponse.getData();
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error("??????????????????????????????: ",e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

}

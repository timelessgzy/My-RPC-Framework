package cn.tjgzy.myrpc.transport.netty.server;

import cn.tjgzy.myrpc.codec.CommonDecoder;
import cn.tjgzy.myrpc.codec.CommonEncoder;
import cn.tjgzy.myrpc.config.ShutdownHook;
import cn.tjgzy.myrpc.provider.ServiceProviderImpl;
import cn.tjgzy.myrpc.registry.NacosServiceRegistry;
import cn.tjgzy.myrpc.serializer.JsonSerializer;
import cn.tjgzy.myrpc.serializer.KryoSerializer;
import cn.tjgzy.myrpc.serializer.ProtostuffSerializer;
import cn.tjgzy.myrpc.transport.AbstractRpcServer;
import cn.tjgzy.myrpc.utils.ThreadPoolFactoryUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.concurrent.TimeUnit;

/**
 * @author GongZheyi
 * @create 2021-09-17-9:37
 */
public class NettyServer extends AbstractRpcServer {

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        scanServices();
    }

    @Override
    public void start() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2,
                ThreadPoolFactoryUtils.createThreadFactory("service-handler-group"));

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new CommonDecoder());
                            ch.pipeline().addLast(new CommonEncoder(new ProtostuffSerializer()));
                            ch.pipeline().addLast(serviceHandlerGroup, new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host,port).sync();
            // ???????????????????????????????????????????????????
            ShutdownHook.getShutdownHook().addClearAllHook();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.info("??????????????????????????????" + e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }

}

package cn.tjgzy.myrpc.transport.netty.server;

import cn.tjgzy.myrpc.codec.CommonDecoder;
import cn.tjgzy.myrpc.codec.CommonEncoder;
import cn.tjgzy.myrpc.hook.ShutdownHook;
import cn.tjgzy.myrpc.provider.ServiceProviderImpl;
import cn.tjgzy.myrpc.provider.ServiceProvider;
import cn.tjgzy.myrpc.registry.NacosServiceRegistry;
import cn.tjgzy.myrpc.registry.ServiceRegistry;
import cn.tjgzy.myrpc.serializer.KryoSerializer;
import cn.tjgzy.myrpc.transport.AbstractRpcServer;
import cn.tjgzy.myrpc.transport.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

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
                            ch.pipeline().addLast(new CommonDecoder());
                            ch.pipeline().addLast(new CommonEncoder(new KryoSerializer()));
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host,port).sync();
            // 添加钩子方法，关闭时向服务中心注销
            ShutdownHook.getShutdownHook().addClearAllHook();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.info("启动服务器时发生错误" + e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}

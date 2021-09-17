package cn.tjgzy.myrpc.transport.netty.server;

import cn.tjgzy.myrpc.entity.RpcRequest;
import cn.tjgzy.myrpc.entity.RpcResponse;
import cn.tjgzy.myrpc.registry.DefaultServiceRegistry;
import cn.tjgzy.myrpc.registry.ServiceRegistry;
import cn.tjgzy.myrpc.transport.RequestHandler;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 入站处理器，收到RpcRequest报文，发送RpcResponse报文
 * @author GongZheyi
 * @create 2021-09-17-11:01
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {

        String interfaceName = msg.getInterfaceName();
        Object service = serviceRegistry.getService(interfaceName);
        Object result = requestHandler.handle(msg, service);
        // 构造Response报文
        RpcResponse<Object> rpcResponse = RpcResponse.success(result);
        ChannelFuture future = ctx.writeAndFlush(rpcResponse);
        future.addListener(ChannelFutureListener.CLOSE);

        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
